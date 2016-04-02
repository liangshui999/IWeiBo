package com.example.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.example.contants.ActivityConstant;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步加载图片的类，先从缓存中寻找，再从文件系统中寻找，都找不到就从网络下载
 * Created by asus-cp on 2016-03-27.
 */
public class ImageAsynLoader {
    private static String tag="ImageAsynLoader";
    public static String URL_KEY="urlkey";
    public static String BITMAP_KEY="bitmapkey";
    private ImageCallBak imageCallBak;
    public static final int UPDATE_IMAGE=0;//更新imagview
    private  Context context;
    private  ExecutorService executorService;
    private  int maxMemory;
    private  int maxCache;
    //内存中的缓存区域
    private LruCache<String,Bitmap> cache;
    private int width;//屏幕宽
    private int height;//屏幕高

    public ImageAsynLoader(int width,int height){
        this.width=width;
        this.height=height;
        init();
    }

    public ImageAsynLoader(){
        init();
    }

    public void init(){
        context=MyApplication.getContext();
        executorService= Executors.newCachedThreadPool();//新建一个可缓存线程池
        maxMemory= (int) (Runtime.getRuntime().maxMemory()/1024);//当前应用程序所用使用的最大内存
        maxCache=maxMemory/8;
        cache=new LruCache<String,Bitmap>(maxCache){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //API 19
                    return value.getAllocationByteCount()/1024;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)  //API 12

                {
                    return value.getByteCount()/1024;
                }
                return value.getRowBytes() * value.getHeight()/1024;

            }
            //默认的实现是一个空方法，我的实现：当内存达到设定的最大值时，清理内存
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
//                if(evicted && oldValue!=null){
//                    oldValue.recycle();//释放bitmap资源
//                }
            }
        };



    }

    private  Handler myHandler=new MyHandler();
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_IMAGE:
                    Bundle bundle=msg.getData();
                    String url=bundle.getString(URL_KEY);
                    Bitmap bitmap=bundle.getParcelable(BITMAP_KEY);
                    imageCallBak.refresh(bitmap,url);
                    break;
            }
        }
    }


    /**
     * 获取bitmap
     *
     */
    public Bitmap getBitmap(String url,ImageCallBak imagecallBak){

        this.imageCallBak=imagecallBak;
        Bitmap bitmap=null;
        bitmap=getBitmapFromCache(url);
        if(bitmap!=null){
            return bitmap;
        }else{
            bitmap=getBitmapFromFile(url);
            if(bitmap!=null){
                    return bitmap;
            }else{
                getBitmapFromInternet(url);//这个会开新线程去下载，无法返回
               return null;
            }
        }
    }


    /**
     * 从内存中读取birmap
     */
    public Bitmap getBitmapFromCache(String url){
        return cache.get(url);
    }
    /**
     * 从本地文件系统中获取bitmap
     */
    public Bitmap getBitmapFromFile(String url){
        String fileName=transformUrl(url);
        InputStream in=null;
        Bitmap bitmap=null;
        try {
            in=context.openFileInput(fileName);
            bitmap= BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
    /**
     * 从网络获取bitmap
     */
    public  void getBitmapFromInternet(final String urlString){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn=null;
                URL url = null;
                Bitmap bitmap=null;
                try {
                    url = new URL(urlString);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(ActivityConstant.GET);
                    conn.setConnectTimeout(5 * 1000);
                    int contentLength=conn.getContentLength();
                    String response=conn.getResponseMessage();
                    Log.d(tag,"contentlength="+contentLength);
                    Log.d(tag,"response="+response);
                    InputStream in = conn.getInputStream();

                    int beishu=calculateInSampleSize(in, 200, 200);
                    Log.d(tag,"beishu="+beishu);
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inSampleSize=beishu;

                    options.inJustDecodeBounds=false;
                    HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();//注意这里的搞法，哥们是重新练了一次网，重新获取in，只有这样才行
                    conn1.setRequestMethod(ActivityConstant.GET);
                    conn1.setConnectTimeout(5 * 1000);
                    InputStream in1 = conn1.getInputStream();
                    bitmap=BitmapFactory.decodeStream(in1,null,options);
                    Log.d(tag,"bitmap大小="+bitmap.getByteCount());


                    Message message=Message.obtain();
                    message.what=UPDATE_IMAGE;
                    Bundle bundle=new Bundle();
                    bundle.putParcelable(BITMAP_KEY,bitmap);
                    //这里是关键所在，URl是对方imageview的tag，因此哥们必须原封不动的保存下来，然后通过回调方法返给他，。只能通过这种方式
                    //通过全局变量是不靠谱，因为会被下一个请求覆盖掉
                    bundle.putString(URL_KEY, urlString);//因为每一个下载请求的url，哥们都必须替你保存下来，然后原封不动的还给你，因为这个string是imageview的tag
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                    //顺序很重要，先发送消息，再存
                    //将bitmap存入缓存
                    cache.put(urlString, bitmap);
                    saveBitmapToFile(urlString, bitmap);//将bitmap存入文件
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(conn!=null){
                        conn.disconnect();
                    }
                }

                }
            });




    }
    /**
     * 把url中所有的/符号替换成a，http://www.baidu.com替换成http:aawww.baidu.com
     */
    public String transformUrl(String s){
        return s.replaceAll("/","a");
    }

    /**
     * 将bitmap存入文件
     */
    public void saveBitmapToFile(String url,Bitmap bitmap){
        FileOutputStream out=null;
        String fileName=transformUrl(url);
        try {
            out=context.openFileOutput(fileName,Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 回调接口
     */
    public interface ImageCallBak{
        public void refresh(Bitmap bitmap,String url);
    }

    /**
     * 计算压缩的比例
     */
    public int calculateInSampleSize(
            InputStream in, int reqWidth, int reqHeight) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeStream(in,null,options);
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(tag,"Height"+height);
        Log.d(tag,"width"+width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            inSampleSize=inSampleSize*2;
        }

        return inSampleSize;
    }

    /**
     * 返回压缩后的bitmap
     * @param reqWidth 压缩后需要的宽
     * @param reqHeight 压缩后需要的高
     */
    public Bitmap decodeSampledBitmapFromStream(InputStream in,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//这个属性设置为true的时候，会将图片的宽高信息返回到options里面，但不返回bimap
        BitmapFactory.decodeStream(in, null,options);


        BitmapFactory.Options o2 = new BitmapFactory.Options();
        // Calculate inSampleSize
        o2.inSampleSize = 2;

        // Decode bitmap with inSampleSize set

        Log.d(tag,"reqWidth="+reqWidth);
        Log.d(tag,"reqHeight"+reqHeight);
        return BitmapFactory.decodeStream(in,null,null);
    }

}

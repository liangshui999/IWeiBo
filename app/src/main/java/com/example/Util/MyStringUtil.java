package com.example.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

import com.example.asus_cp.activity.PersonalHomePageActivity;
import com.example.asus_cp.activity.WebActivity;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对string做特殊处理，高亮显示部分string，将string里面的某些部分替换成图片
 * Created by asus-cp on 2016-03-30.
 */
public class MyStringUtil {
    public static final String GET="GET";
    public static final String PHRASE="phrase";
    public static final String GIFURL="url";
    private String tag="MyStringUtil";
    public static final String START = "start";
    public static final String END = "end";
    public static final String PIPEI_CONTENT = "piPeiContent";
    private String zfName="\\w*[\\u4e00-\\u9fa5]*\\w*[\\u4e00-\\u9fa5]*\\s:";//转发名称
    private String topic="#.*#";//解析话题
    private String at="@\\w*[\\u4e00-\\u9fa5]*\\w*[\\u4e00-\\u9fa5]*";//只解析：中文，[a-zA-Z_0-9]
    private String emotionResolve ="\\[\\w*[\\u4e00-\\u9fa5]*\\w*[\\u4e00-\\u9fa5]*\\]";//解析表情
    private String urlResolve="(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
              + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                       + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
                       + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                       + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
                       + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                       + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
                       + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";

    private Map<String,String>emotions;//存放表情名称和表情url的集合，名称是键，url是值
    private boolean isCompleteSaveToHashmap=false;//是否已经将表情的数据缓存到hashmap里面
    private ImageAsynLoader loader;
    private StatusesAPI statusesAPI;

    public static final int TOPIC=1;//话题的标记
    public static final int AT=2;
    public static final int EMOTION=3;
    public static final int URL=4;
    public static final int ZF_NAME=5;

    private Context context;

    public static final String URL_KEY="urlKey";//传送url到webview里面时的key



    public MyStringUtil(StatusesAPI statusesAPI){
        emotions=new HashMap<String,String>();
        loader=new ImageAsynLoader();
        this.statusesAPI=statusesAPI;
        getAndSaveEmotions();//获取表情的数据
        context=MyApplication.getContext();
    }



    /**
     * 对传进来的string设置各种特效
     */
    public SpannableString setHuiZong(String str) {
        final SpannableString spanString = new SpannableString(str);
        setTx(spanString,str,Pattern.compile(topic),TOPIC);//给话题设置特效
        setTx(spanString,str,Pattern.compile(at), AT);//给@设置特效
        setTx(spanString,str, Pattern.compile(emotionResolve), EMOTION);//给表情设置特效
        setTx(spanString,str, Pattern.compile(urlResolve), URL);//给url设置特效
        setTx(spanString, str, Pattern.compile(zfName), ZF_NAME);//给zf设置特效
        return spanString;

    }

    /**
     * 查找需要设置特效的地方的开始的位置和结束的位置，比如：#联通春交会#，[大笑]
     */
    public List<Map<String, String>> getPostion(String str, Pattern pattern) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Matcher matcher = pattern.matcher(str);
        Map<String, String> map = null;
//        if (flag==EMOTION) {     //是表情
            while (matcher.find()) {
                map = new HashMap<String, String>();
                map.put(START, matcher.start() + "");
                map.put(END, matcher.end() + "");
                map.put(PIPEI_CONTENT, matcher.group());
               // Log.d(tag, "是表情"+"start=" + matcher.start() + "....." + "end=" + matcher.end()+"名字="+matcher.group());
                list.add(map);
            }
         /*else {      //不是表情
            while (matcher.find()) {
                map = new HashMap<String, String>();
                map.put(START, matcher.start() + "");
                map.put(END, matcher.end() + "");
               // Log.d(tag,"不是表情"+"start="+matcher.start()+"........"+"end="+matcher.end());
                list.add(map);
            }
        }*/
       // Log.d(tag, "得到的list长度=" + list.size());
        return list;
    }
    /**
     * 设置特效的方法
     */
    public SpannableString setTx(final SpannableString spanString,String str,Pattern pattern,int flag){

        List<Map<String, String>> positons=getPostion(str,pattern);
       // Log.d(tag, "得到的position长度=" + positons.size());
        if(positons!=null){
            switch(flag){
                case TOPIC:
                    for(Map<String, String>map:positons){
                        int start=Integer.parseInt(map.get(START));
                        int end=Integer.parseInt(map.get(END));
                        spanString.setSpan(new ForegroundColorSpan(Color.GREEN),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case AT:
                    for(Map<String, String>map:positons){
                        int start=Integer.parseInt(map.get(START));
                        int end=Integer.parseInt(map.get(END));
                        spanString.setSpan(new ForegroundColorSpan(Color.GREEN),start,end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanString.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                Intent intent=new Intent(context, PersonalHomePageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在非acitivty中启动，必须设置这个选项
                                context.startActivity(intent);//用隐式意图开启活动

                            }
                        },start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case EMOTION:
                    for(Map<String, String>map:positons){
                        final int start=Integer.parseInt(map.get(START));
                        final int end=Integer.parseInt(map.get(END));
                        String emotinName=map.get(PIPEI_CONTENT);
                        if(isCompleteSaveToHashmap){
                            String url=emotions.get(emotinName);
                            Bitmap bitmap=loader.getBitmap(url, new ImageAsynLoader.ImageCallBak() {
                                @Override
                                public void refresh(Bitmap bitmap, String url) {
                                    if(bitmap!=null){
                                        spanString.setSpan(new ImageSpan(MyApplication.getContext(),bitmap),
                                                start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                }
                            });
                            if(bitmap!=null){
                                spanString.setSpan(new ImageSpan(MyApplication.getContext(),bitmap),
                                        start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                    }
                    break;
                case URL:
                    for(Map<String, String>map:positons){
                        int start=Integer.parseInt(map.get(START));
                        int end=Integer.parseInt(map.get(END));
                        final String url=map.get(PIPEI_CONTENT);
                        spanString.setSpan(new ForegroundColorSpan(Color.RED),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanString.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                Intent intent=new Intent(context, WebActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在非acitivty中启动，必须设置这个选项
                                intent.putExtra(URL_KEY,url);
                                context.startActivity(intent);
                            }
                        },start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case ZF_NAME:
                    for(Map<String, String>map:positons){
                        int start=Integer.parseInt(map.get(START));
                        int end=Integer.parseInt(map.get(END));
                        spanString.setSpan(new ForegroundColorSpan(Color.BLUE),start,end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanString.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                Intent intent=new Intent(context, PersonalHomePageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在非acitivty中启动，必须设置这个选项
                                context.startActivity(intent);//用隐式意图开启活动

                            }
                        },start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;

            }

        }
        return spanString;
    }
    /**
     * 获取所有的微博表情，并存放到emotios里面(这个是多此一举了，可以废弃掉了)
     */
    public void getEmotionsFeiQi(){
            new Thread(new Runnable() {
                URL url=null;
                HttpURLConnection conn=null;
                InputStream in=null;
                ByteArrayOutputStream out=null;
                @Override
                public void run() {
                    try {
                        url=new URL("https://api.weibo.com/2/emotions.json");
                        conn= (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod(GET);
                        conn.setConnectTimeout(5 * 1000);
                        in=conn.getInputStream();
                        out=new ByteArrayOutputStream();
                        int len=0;
                        byte[] buf=new byte[1024];
                        while((len=in.read(buf))!=-1){
                            out.write(buf,0,len);
                            out.flush();
                        }
                        parseEmotionJson(out.toString());
                        Log.d(tag, "输出流字节数组=" + out.toString());
                        isCompleteSaveToHashmap=true;
                    } catch (Exception e) {
                        Log.d(tag, e.toString());
                    }finally {
                        conn.disconnect();
                        if(out!=null){
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }).start();
    }

    /**
     * 获取新浪微博提供的表情，并存放到emotions里面
     */
    public void getAndSaveEmotions(){
        statusesAPI.emotions(StatusesAPI.EMOTION_TYPE_FACE, StatusesAPI.LANGUAGE_CNNAME, new RequestListener() {
            @Override
            public void onComplete(String s) {
                parseEmotionJson(s);
                isCompleteSaveToHashmap=true;
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
    }

    /**
     * 解析表情api返回的json数据,解析出来后装到map集合里面
     */
    public void parseEmotionJson(String str){
        Log.d(tag, "str=" + str);
        try {
            JSONArray jsonArray=new JSONArray(str);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString(PHRASE);
                String url=jsonObject.getString(GIFURL);
                emotions.put(name, url);
                Log.d(tag, "name=" + name+"url="+url);
               // Log.d(tag, "集合长度="+emotions.size()+"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

package com.example.asus_cp.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.Util.ImageAsynLoader;
import com.example.adapter.WeiBoListAdapter;
import com.example.asus_cp.iweibo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by asus-cp on 2016-04-01.
 */
public class PicOperateActivity extends Activity{
    @Bind(R.id.img_pic_operate)
    ImageView imgPicOperate;
    private ImageAsynLoader loader;
    public static  float FANGD_DA= (float) 1.1;//点击放大按钮时的放大比例
    public static  float SUO_XIAO= (float) 0.9;//点击缩小按钮时的缩小比例
    private Bitmap compeleteBitmap;//下载原始图的bitmap
    private Bitmap resizeBmp;//重新构建的bitmap
    private String tag="PicOperateActivity";
    private int i=0;
    private int j=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_operate_layout);
        ButterKnife.bind(this);
        String url=getIntent().getStringExtra(WeiBoListAdapter.URL_KEY_MYLISTENER);//获取原始图片的url
        loader=new ImageAsynLoader(320,480);
        Bitmap bitmap=loader.getBitmap(url, new ImageAsynLoader.ImageCallBak() {
            @Override
            public void refresh(Bitmap bitmap, String url) {
                if(bitmap!=null){
                    imgPicOperate.setImageBitmap(bitmap);
                    compeleteBitmap=bitmap;
                }

            }
        });
        if(bitmap!=null){
            imgPicOperate.setImageBitmap(bitmap);
            compeleteBitmap=bitmap;
        }


    }

    @OnClick(R.id.img_fangda) void onImgFangdaClick() {
        //TODO implement
        imgPicOperate.setImageMatrix(getMatrix(compeleteBitmap, FANGD_DA));
        imgPicOperate.setImageBitmap(resizeBmp);

    }



    @OnClick(R.id.img_suoxiao) void onImgSuoxiaoClick() {
        //TODO implement
        imgPicOperate.setImageMatrix(getMatrix(compeleteBitmap, SUO_XIAO));
        imgPicOperate.setImageBitmap(resizeBmp);
    }

    /**
     * 获取matrix
     * @param ratio 缩放的比例
     */
    public Matrix getMatrix(Bitmap bitmap,float ratio){
        Matrix matrix=new Matrix();
        int orginalWidth=bitmap.getWidth();
        int orginalHeight=bitmap.getHeight();

        matrix.postScale(ratio, ratio);//注意这里的写法
        Log.d(tag, "orginalWidth=" + orginalWidth);
        Log.d(tag, "orginalHeight=" + orginalHeight);
        resizeBmp=Bitmap.createBitmap(bitmap, 0, 0, orginalWidth, orginalHeight, matrix, true);
        compeleteBitmap=resizeBmp;
        bitmap=null;//把原来的bitmap清空，节约内存
        System.gc();//调用垃圾回收器回收垃圾
        return matrix;
    }


}

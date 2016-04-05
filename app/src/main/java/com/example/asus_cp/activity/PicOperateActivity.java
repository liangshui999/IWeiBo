package com.example.asus_cp.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.Util.ImageAsynLoader;
import com.example.adapter.WeiBoListAdapter;
import com.example.asus_cp.iweibo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 点击图片之后，对图片进行缩放
 * Created by asus-cp on 2016-04-01.
 */
public class PicOperateActivity extends Activity{
    @Bind(R.id.img_pic_operate)
    ImageView imgPicOperate;
    private ImageAsynLoader loader;
    public static  float FANGD_DA= (float) 1.1;//点击放大按钮时的放大比例
    public static  float SUO_XIAO= (float) 0.9;//点击缩小按钮时的缩小比例
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
                }

            }
        });
        if(bitmap!=null){
            imgPicOperate.setImageBitmap(bitmap);
        }


        //下面是我自己写的ontouchLister
//        imgPicOperate.setOnTouchListener(new View.OnTouchListener() {
//            //下面这几个变量千万不要写在ontouch（）方法里面
//            Matrix currentMatrix=new Matrix();
//            Matrix matrix=new Matrix();
//            float dx = 0;//拖曳时x方向的拖曳量
//            float dy = 0;//拖曳时y方向的拖曳
//            PointF firstPoint = new PointF();
//            PointF secondPoint=new PointF();
//            float firstDistance;//2个手指按下时的初始距离
//            private static final int INIT=0;
//            private static final int DRAG=1;
//            private static final int ZOOM=2;
//            private int mode=INIT;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (MotionEventCompat.getActionMasked(event)){
//                    case MotionEvent.ACTION_DOWN:   //  第一个手指按下
//                        mode=DRAG;
//                        firstPoint.set(event.getX(),event.getY());
//                        currentMatrix.set(imgPicOperate.getImageMatrix());//记录下第一根手指按下时的矩阵状态
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if(mode==DRAG){//拖曳模式
//                            float x=event.getX();
//                            float y=event.getY();
//                            dx=x-firstPoint.x;
//                            dy=y-firstPoint.y;
//                            matrix.set(currentMatrix);//先设置成初始矩阵，然后在初始矩阵的基础上变化
//                            matrix.postTranslate(dx, dy);
//                        }else if(mode==ZOOM){//缩放模式
//                            float distance=getDistance(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
//                            //if(distance>10f){//因为之前设置的是只要有2根手指就默认是缩放模式，所以这里需要判断
//                                float scale=distance/firstDistance;
//                                PointF midPoint=getMidPoint(event.getX(0),event.getY(0),
//                                        event.getX(1),event.getY(1));
//                                matrix.set(currentMatrix);//先设置成初始矩阵，然后在初始矩阵的基础上变化
//                                matrix.postScale(scale,scale,midPoint.x,midPoint.y);
////                            }else {
////                                //mode=DRAG;
////                            }
//                        }
//
//                        break;
//                    case MotionEvent.ACTION_POINTER_DOWN://第二个手指按上屏幕的时候调用
//                        currentMatrix.set(imgPicOperate.getImageMatrix());
//                        mode=ZOOM;
//                        firstDistance =getDistance(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
//
//                        break;
//                    case MotionEvent.ACTION_POINTER_UP://某个手指离开屏幕的时候调用
//                        mode=DRAG;
//                        break;
//                    case  MotionEvent.ACTION_UP://手指都离开屏幕
//                        mode=INIT;
//                        break;
//
//                }
//                imgPicOperate.setImageMatrix(matrix);
//                return true;
//            }
//
//            /**
//             * 计算2个点之间的距离
//             */
//        public float getDistance(float x1,float y1,float x2,float y2){
//            return (float) Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
//        }
//
//            /**
//             * 计算2个点之间的中点
//             */
//        public PointF getMidPoint(float x1,float y1,float x2,float y2){
//            return new PointF((x1+x2)/2,(y1+y2)/2);
//        }
//
//        });



    }

    @OnClick(R.id.img_fangda) void onImgFangdaClick() {
        //TODO implement
        imgPicOperate.setImageMatrix(getMatrix((float) (FANGD_DA+0.1*(i++))));
    }



    @OnClick(R.id.img_suoxiao) void onImgSuoxiaoClick() {
        //TODO implement
        imgPicOperate.setImageMatrix(getMatrix((float) (SUO_XIAO - 0.1 * (i++))));
    }

    /**
     * 获取matrix
     * @param ratio 缩放的比例
     */
    public Matrix getMatrix(float ratio){
        Matrix matrix=new Matrix();
        matrix.postScale(ratio, ratio);//注意这里的写法
        return matrix;
    }

}

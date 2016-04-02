package com.example.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.Util.FileUtil;
import com.example.Util.HttpDownLoader;
import com.example.asus_cp.activity.LoginActivity;
import com.example.asus_cp.activity.WBAuthActivity;
import com.example.auth.AuthConstants;
import com.example.biz.UserInfoManager;
import com.example.contants.ActivityConstant;
import com.example.contants.DBConstant;
import com.example.modle.Task;
import com.example.modle.UserInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus-cp on 2016-03-19.
 * 主服务类，活动向他发送任务和活动本身，他接收任务并处理，处理完成后刷新界面
 */
public class MainService extends Service implements Runnable {
    private String tag="MainService";
    private Task task;
    private static List<Activity>activities=new ArrayList<Activity>();
    private MyHandler myHandler;
    /**
     * 这里使用静态内部类，静态内部类不会保持对外部内的引用
     */
    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Task.SLEEP:
                    Bundle bundle=msg.getData();
                    String name=bundle.getString(ActivityConstant.URL);
                    WBAuthActivity activity= (WBAuthActivity) getActivityByName("com.example.asus_cp.activity.WBAuthActivity");
                    activity.refresh(name);
                    activities.remove(activity);
                    break;
                case Task.GET_LOGIN_USER_INFO://获取用户名和头像并刷新ui，并将获取到的用户信息存到数据库
                    Bundle bundle1=msg.getData();
                    String userId=bundle1.getString(DBConstant.UserTable.USER_ID);
                    String userName=bundle1.getString(DBConstant.UserTable.USER_NAME);
                    String accessToken=bundle1.getString(DBConstant.UserTable.ACCESS_TOKEN);
                    String refreshToken=bundle1.getString(DBConstant.UserTable.REFRESH_TOKEN);
                    long authTime=bundle1.getLong(DBConstant.UserTable.AUTH_TIME);
                    long expireninTime=bundle1.getLong(DBConstant.UserTable.EXPIREIN);
                    File file=new File(Environment.getExternalStorageDirectory(),ActivityConstant.TEMP_FILE_NAME);
                    Bitmap bitmap=FileUtil.getBitMapFromFile(file);
                    byte[] userImage=FileUtil.getByteArrayFromBitmap(bitmap);
                    Log.d(tag, "currentTime=" + authTime);
                    Log.d(tag, "expiresTime=" + expireninTime);
                    UserInfo userInfo=new UserInfo(userId,userName,userImage,accessToken,refreshToken,
                            authTime,expireninTime);
                    UserInfoManager manager=new UserInfoManager();
                    manager.addUserInfo(userInfo);
                    LoginActivity loginActivity= (LoginActivity) getActivityByName("com.example.asus_cp." +
                            "activity.LoginActivity");
                    loginActivity.refresh(Task.GET_LOGIN_USER_INFO);
                    activities.remove(loginActivity);//从集合中移除，避免内存泄漏
            }
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        myHandler=new MyHandler();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task=intent.getParcelableExtra(ActivityConstant.TASK);
        new Thread(this).start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void run() {
        doTask(task);

    }



    /**
     * 任务的具体执行
     */
    public void doTask(Task task1)  {
        switch (task1.getTaskId()){
            case Task.DAYIN:
                for(int i=0;i<100000;i++){
                    Log.d(tag,"i="+i+Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Task.SLEEP:
                Bundle bundle=task1.getTaskParams();
                String url=bundle.getString(ActivityConstant.URL);
                int time=bundle.getInt(ActivityConstant.OUT_TIME);
                Log.d(tag,"url="+url);
                Log.d(tag, "time=" + time);
                Message message=Message.obtain();
                message.what=Task.SLEEP;
                message.setData(bundle);
                myHandler.sendMessage(message);
                break;
            case Task.GET_LOGIN_USER_INFO://获取用户名和头像并刷新ui，并将获取到的用户信息存到数据库
                Bundle bundle1=task1.getTaskParams();
                Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(bundle1);
                final String accessToken=mAccessToken.getToken();
                final String refreshToken=mAccessToken.getRefreshToken();
                final long userId=Long.parseLong(mAccessToken.getUid());//将字符串转换成long型
                final long expiresTime= mAccessToken.getExpiresTime();
                final long currentTime = bundle1.getLong(ActivityConstant.CURRENT_TIME);
                Log.d(tag,"accessToken="+mAccessToken.toString());
                Log.d(tag, "currentTime=" + currentTime);
                Log.d(tag, "expiresTime=" + expiresTime);
                Log.d(tag, "线程名称=" + Thread.currentThread().getName());


                UsersAPI usersAPI=new UsersAPI(this, AuthConstants.APP_KEY,mAccessToken);
                usersAPI.show(userId, new RequestListener() {
                    @Override
                    public void onComplete(String s) {//show()方法返回的数据在s里面，是josn类型的数据
                        Log.d(tag,s);
                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            final String userName =jsonObject.getString(AuthConstants.USER_NAME);
                            String userImageUrl =jsonObject.getString(AuthConstants.USER_IMAGE_URL);
                            Log.d(tag, "userNmae=" + userName);
                            Log.d(tag, "userImageUrl=" + userImageUrl);
                            Log.d(tag, "try线程名称=" + Thread.currentThread().getName());
                            final URL url1=new URL(userImageUrl);
                            final File file=new File(Environment.getExternalStorageDirectory(),ActivityConstant.TEMP_FILE_NAME);
                            if(file.exists()){
                                file.delete();
                            }
                            file.createNewFile();
                            new Thread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpDownLoader.download(url1, file);
                                            Message message1=new Message();
                                            Bundle bundle2=new Bundle();
                                            bundle2.putString(DBConstant.UserTable.USER_ID,userId+"");
                                            bundle2.putString(DBConstant.UserTable.USER_NAME,userName);
                                            bundle2.putString(DBConstant.UserTable.ACCESS_TOKEN,accessToken);
                                            bundle2.putString(DBConstant.UserTable.REFRESH_TOKEN,refreshToken);
                                            bundle2.putLong(DBConstant.UserTable.AUTH_TIME, currentTime);
                                            bundle2.putLong(DBConstant.UserTable.EXPIREIN, expiresTime);
                                            message1.setData(bundle2);
                                            message1.what=Task.GET_LOGIN_USER_INFO;
                                            myHandler.sendMessage(message1);
                                        }
                                    }
                            ).start();

                        }catch (Exception e){
                            Log.d(tag,e.toString());
                            e.printStackTrace();

                        }


                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Log.d(tag,e.toString());
                    }
                });
                break;


        }

    }

    /**
     * 添加活动到集合
     */
    public static void addActivity(Activity activity){
        activities.add(activity);

    }

    /**
     * 根据名字查找集合中的活动
     */
    public  Activity getActivityByName(String name){
        Activity mActivity=null;
        for(Activity activity:activities){
            Log.d(tag,activity.getClass().getName());
            if(name.equals(activity.getClass().getName())){
                mActivity=activity;
            }
        }
        return  mActivity;
    }






}

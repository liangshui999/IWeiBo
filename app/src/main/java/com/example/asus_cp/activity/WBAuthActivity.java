package com.example.asus_cp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.Util.FileUtil;
import com.example.Util.MyDateUtil;
import com.example.asus_cp.iweibo.R;
import com.example.auth.AuthConstants;
import com.example.contants.ActivityConstant;
import com.example.daoiml.UserInfoService;
import com.example.db.DBCreateHelper;
import com.example.modle.Task;
import com.example.modle.UserInfo;
import com.example.service.MainService;
import com.example.wbinterfase.IWeiBoActivity;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * Created by asus-cp on 2016-03-19.
 */
public class WBAuthActivity extends Activity implements IWeiBoActivity{
    private AuthInfo authInfo;
    private SsoHandler ssoHandler;
    private Oauth2AccessToken mAccessToken;
    private String TAG="WBAuthActivity";
    private Button sleepButton;
    private Button sqliteButton;
    private ImageView sqliteimageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_layout);
        Button authButton= (Button) findViewById(R.id.btn_auth_ceshi);
        authInfo = new AuthInfo(this, AuthConstants.APP_KEY,
                AuthConstants.REDIRECT_URL, AuthConstants.SCOPE);
        ssoHandler = new SsoHandler(WBAuthActivity.this, authInfo);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        // 从 Bundle 中解析 Token
                        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        Log.d(TAG, "mAccessToken=" + mAccessToken.toString());
                        //从这里获取用户输入的 电话号码信息
                        String phoneNum = mAccessToken.getPhoneNum();
                        Log.d(TAG, "phoneNum=" + phoneNum);
                        if (mAccessToken.isSessionValid()) {

                        } else {
                            // 以下几种情况，您会收到 Code：
                            // 1. 当您未在平台上注册的应用程序的包名与签名时；
                            // 2. 当您注册的应用程序包名与签名不正确时；
                            // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                            String code = bundle.getString("code");
                            Log.d(TAG, "code=" + code);

                        }

                    }

                    @Override
                    public void onWeiboException(WeiboException e) {

                    }

                    @Override
                    public void onCancel() {

                    }


                });
            }
        });


        Button dayinButton= (Button) findViewById(R.id.btn_dayin_ceshi);
        sleepButton= (Button) findViewById(R.id.btn_slepp_ceshi);
        dayinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task=new Task(Task.DAYIN,null);
                Bundle bundle=new Bundle();
                bundle.putParcelable(ActivityConstant.TASK, task);
                Intent intent=new Intent(WBAuthActivity.this, MainService.class);
                intent.putExtras(bundle);
                startService(intent);


            }
        });
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle taskBundle=new Bundle();
                taskBundle.putString(ActivityConstant.URL, ActivityConstant.BAIDU);
                taskBundle.putInt(ActivityConstant.OUT_TIME, 2000);
                Task task=new Task(Task.SLEEP,taskBundle);
                Bundle bundle=new Bundle();
                bundle.putParcelable(ActivityConstant.TASK, task);
                Intent intent=new Intent(WBAuthActivity.this,MainService.class);
                intent.putExtras(bundle);
                MainService.addActivity(WBAuthActivity.this);
                startService(intent);

            }
        });

        sqliteButton=(Button)findViewById(R.id.btn_sqlit_ceshi);
        sqliteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBCreateHelper helper=DBCreateHelper.getDBCreateHelper();
                helper.getReadableDatabase();
            }
        });

        Button addButton= (Button) findViewById(R.id.btn_add_ceshi);
        Button deleteButton= (Button) findViewById(R.id.btn_delete_ceshi);
        Button updateButton= (Button) findViewById(R.id.btn_update_ceshi);
        Button queryButton= (Button) findViewById(R.id.btn_query_ceshi);
        Button queryAllButton= (Button) findViewById(R.id.btn_query_all_ceshi);
        sqliteimageView=(ImageView)findViewById(R.id.img_ceshi_slite);
        final Button dateCesi= (Button) findViewById(R.id.btn_date_ceshi);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"点击了吗");
                File file=new File(Environment.getExternalStorageDirectory(),"1.jpg");
                byte[] b= FileUtil.getByteArrayFromBitmap(FileUtil.getBitMapFromFile(file));
                UserInfo userInfo=new UserInfo("1","张三",b,"123456","000000",100,5000);
                UserInfoService userInfoService=new UserInfoService();
                userInfoService.insert(userInfo);

                sqliteimageView.setImageBitmap(FileUtil.getBitmapFromByteArray(b));
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoService userInfoService=new UserInfoService();
                userInfoService.delete("1");

            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoService userInfoService=new UserInfoService();
                File file=new File(Environment.getExternalStorageDirectory(),"1.jpg");
                byte[] b= FileUtil.getByteArrayFromBitmap(FileUtil.getBitMapFromFile(file));
                UserInfo userInfo=new UserInfo("1","李四",b,"2222","2222",200,2000);
                userInfoService.update(userInfo.getUserId(), userInfo);

            }
        });
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoService userInfoService=new UserInfoService();
                UserInfo userInfo=userInfoService.query("1");
                Log.d(TAG,userInfo.getUserName());
                sqliteimageView.setImageBitmap(FileUtil.getBitmapFromByteArray(userInfo.getUserImage()));

            }
        });
        queryAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoService userInfoService=new UserInfoService();
                List<UserInfo>userInfos= userInfoService.query();
                UserInfo userInfo=userInfos.get(0);
                Log.d(TAG,userInfo.getUserName());
                sqliteimageView.setImageBitmap(FileUtil.getBitmapFromByteArray(userInfo.getUserImage()));

            }
        });

        dateCesi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date="Sat Mar 25 23:55:50 +0800 2016";
                Calendar calendar=MyDateUtil.transformStringToDate(date);
                String jieguo= MyDateUtil.getFormatString(calendar);
                dateCesi.setText(jieguo);
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... object) {
        String name= (String) object[0];
        sleepButton.setText(name);
    }
}

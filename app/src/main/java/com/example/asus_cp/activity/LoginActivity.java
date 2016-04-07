package com.example.asus_cp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.Util.FileUtil;
import com.example.adapter.UserSelectSpinnerAdapter;
import com.example.asus_cp.iweibo.R;
import com.example.auth.AuthConstants;
import com.example.biz.UserInfoManager;
import com.example.contants.ActivityConstant;
import com.example.modle.Task;
import com.example.modle.UserInfo;
import com.example.service.MainService;
import com.example.wbinterfase.IWeiBoActivity;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录界面的activity
 */
public class LoginActivity extends Activity  implements IWeiBoActivity{
    private AuthInfo authInfo;
    private SsoHandler ssoHandler;
    private Oauth2AccessToken mAccessToken;
    private String tag="LoginActivity";
    private AlertDialog authDialog;//授权对话框
    private List<UserInfo>userInfos;//从数据库中查询到的用户信息集合
    private UserInfo userInfo;//用户从下拉列表中选中的某项的用户信息,当为空的时候，说明没选择
    private UserSelectSpinnerAdapter adapter;
    private UserInfoManager manager;

    @Bind(R.id.btn_add_account)
    Button btnAddAccount;
    @Bind(R.id.img_user_head) ImageView imgUserHead;
    @Bind(R.id.spin_select_user) Spinner spinSelectUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        init();


    }


    /**
     * 登录按钮的点击事件
     */
    @OnClick(R.id.btn_login) void onBtnLoginClick() {
        //TODO implement
        if(userInfo==null){
            Toast.makeText(this,"请选择一个用户",Toast.LENGTH_LONG).show();
        }else{
            if(isHaveNet()){
                Log.d(tag,userInfo.getAuthTime()+"");
                Log.d(tag,userInfo.getExpiresIn()+"");
                Bundle bundle=new Bundle();
                bundle.putParcelable(ActivityConstant.USER_INFO_KEY,userInfo);
                Intent intent=new Intent(this,HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }else{
                Toast.makeText(this,"网络不可用",Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * 判断手机是否处于联网状态,有返回true，没有返回false
     */
    public boolean isHaveNet(){
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }


    /**
     * 添加账户的点击事件
     */
    @OnClick(R.id.btn_add_account) void onBtnAddAccountClick(){
        LayoutInflater inflater=LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.auth_dialog_layout, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        authDialog=builder.show();
        Button beginAuthButton= (Button) view.findViewById(R.id.btn_begin_auth);
        beginAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        authDialog.dismiss();//授权结束后关闭对话框
                        // 从 Bundle 中解析 Token
                        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        Log.d(tag, "mAccessToken=" + mAccessToken.toString());
                        if (mAccessToken.isSessionValid()) {
                            long currentTime=System.currentTimeMillis();
                            bundle.putLong(ActivityConstant.CURRENT_TIME,currentTime);
                            Task task=new Task(Task.GET_LOGIN_USER_INFO,bundle);
                            Bundle taskBundle=new Bundle();
                            taskBundle.putParcelable(ActivityConstant.TASK,task);
                            Intent intent=new Intent(LoginActivity.this, MainService.class);
                            intent.putExtras(taskBundle);
                            startService(intent);
                            MainService.addActivity(LoginActivity.this);

                        } else {
                            String code = bundle.getString("code");
                            Log.d(tag, "code=" + code);

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

    }

    /**
     * 初始化方法
     */
    @Override
    public void init() {
        authInfo = new AuthInfo(this, AuthConstants.APP_KEY,
                AuthConstants.REDIRECT_URL, AuthConstants.SCOPE);
        ssoHandler = new SsoHandler(this, authInfo);
        manager=new UserInfoManager();
        userInfos=manager.getAllUserInfo();
        adapter=new UserSelectSpinnerAdapter(this,userInfos);
        spinSelectUser.setAdapter(adapter);
        spinSelectUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bitmap bitmap = FileUtil.getBitmapFromByteArray(userInfos.get(position).getUserImage());
                imgUserHead.setImageBitmap(bitmap);//点击下拉列表中的某一项时，刷新用户头像
                userInfo=userInfos.get(position);//得到选择的用户信息
                /*if (view != null) {
                    isSelected = true;
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void refresh(Object... object) {
        int bj=(int)object[0];
        switch (bj){
            case Task.GET_LOGIN_USER_INFO:
                userInfos=manager.getAllUserInfo();
                Log.d(tag,userInfos.get(0).getAuthTime()+"");
                Log.d(tag,userInfos.get(0).getExpiresIn()+"");
                adapter=new UserSelectSpinnerAdapter(this,userInfos);
                spinSelectUser.setAdapter(adapter);


    }
}
}

package com.example.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.adapter.WeiBoListAdapter;
import com.example.asus_cp.activity.HomeActivity;
import com.example.asus_cp.iweibo.R;
import com.example.auth.AuthConstants;
import com.example.contants.ActivityConstant;
import com.example.modle.UserInfo;
import com.example.wbinterfase.IWeiBoActivity;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus-cp on 2016-03-23.
 */
public class HomeFragment extends android.support.v4.app.Fragment implements IWeiBoActivity{
    private List<Status> statuses;
    private StatusesAPI statusesAPI;
    private String tag="HomeFragment";
    private WeiBoListAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.home_fragment_layout,null);
        listView= (ListView) v.findViewById(R.id.listview_home);

        return v;
    }

    @Override
    public void init() {
        statuses=new ArrayList<Status>();
        HomeActivity homeActivity= (HomeActivity) getActivity();//获取到和本碎片相关联的活动
        UserInfo userInfo=homeActivity.getUserInfo();//获取到登录用户的信息
        Log.d(tag,"userName="+userInfo.getUserName());
        Log.d(tag,"AccessToken="+userInfo.getAccessToken());
        Log.d(tag, "ExpiresIn=" + userInfo.getExpiresIn());
        Oauth2AccessToken oauth2AccessToken=new Oauth2AccessToken(userInfo.getAccessToken(),
                userInfo.getExpiresIn()+"");
        statusesAPI=new StatusesAPI(getActivity(), AuthConstants.APP_KEY,oauth2AccessToken);
        statusesAPI.friendsTimeline(0, 0, 50, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                try {
                    Log.d(tag, "s=" + s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray(ActivityConstant.STATUES_KEY);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Status status = Status.parse(jsonArray.getJSONObject(i));
                        Log.d(tag, " status=" + status.user.screen_name+status.text);
                        statuses.add(status);
                    }
                    adapter = new WeiBoListAdapter(getActivity(), statuses);
                    listView.setAdapter(adapter);//得数据返回来之后才设置adapter，而不是在oncreateView()里面设置
                    Log.d(tag, "size=" + statuses.size());
                } catch (JSONException e) {
                    Log.d(tag, "发生了异常");
                    e.printStackTrace();

                }

            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
    }

    @Override
    public void refresh(Object... params) {

    }
}

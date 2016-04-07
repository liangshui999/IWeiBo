package com.example.fragment;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.WeiBoListAdapter;
import com.example.asus_cp.activity.HomeActivity;
import com.example.asus_cp.iweibo.R;
import com.example.auth.AuthConstants;
import com.example.contants.ActivityConstant;
import com.example.modle.UserInfo;
import com.example.wbinterfase.IWeiBoActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asus-cp on 2016-03-23.
 */
public class HomeFragment extends android.support.v4.app.Fragment implements IWeiBoActivity{
    private List<Status> statuses;
    private StatusesAPI statusesAPI;
    private String tag="HomeFragment";
    private WeiBoListAdapter adapter;
    private PullToRefreshListView mListview;
    private ProgressDialog progressDialog;//加载微博数据时，显示的正在加载的对话框


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        View v=inflater.inflate(R.layout.home_fragment_layout,null);
        mListview = (PullToRefreshListView) v.findViewById(R.id.listview_home);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListview.setMode(PullToRefreshBase.Mode.BOTH);
        mListview.setOnRefreshListener(new MyOnrefreshListener());
    }

    /**
     * 初始化的方法
     */
    @Override
    public void init() {
        showProgressDialog();
        statuses=new ArrayList<Status>();
        HomeActivity homeActivity= (HomeActivity) getActivity();//获取到和本碎片相关联的活动
        //获取屏幕宽和高
        final int screenWidth=homeActivity.getScreenWidth();
        final int screenHeight=homeActivity.getScreenHeight();

        UserInfo userInfo=homeActivity.getUserInfo();//获取到登录用户的信息
        Log.d(tag,"userName="+userInfo.getUserName());
        Log.d(tag,"AccessToken="+userInfo.getAccessToken());
        Log.d(tag, "ExpiresIn=" + userInfo.getExpiresIn());
        Oauth2AccessToken oauth2AccessToken=new Oauth2AccessToken(userInfo.getAccessToken(),
                userInfo.getExpiresIn()+"");
        statusesAPI=new StatusesAPI(getActivity(), AuthConstants.APP_KEY,oauth2AccessToken);

        statusesAPI.friendsTimeline(0, 0, 20, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                try {
                    Log.d(tag, "s=" + s);
                    addToStatuses(s);
                    adapter = new WeiBoListAdapter(getActivity(), statuses, statusesAPI, screenWidth, screenHeight);
                    mListview.setAdapter(adapter);//得数据返回来之后才设置adapter，而不是在oncreateView()里面设置
                    Log.d(tag, "size=" + statuses.size());
                    progressDialog.dismiss();
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

    /**
     * 显示正在加载的progressDialog
     */
    public void showProgressDialog(){
        progressDialog= ProgressDialog.show(getActivity(), "", "正在加载...");
        progressDialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.mipmap.progress,null));
        }else{
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.mipmap.progress));
        }
        progressDialog.show();
    }

    /**
     * 将返回的微博数据添加到集合里面
     * @param s
     * @throws JSONException
     */
    private void addToStatuses(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = jsonObject.getJSONArray(ActivityConstant.STATUES_KEY);
        for (int i = 0; i < jsonArray.length(); i++) {
            Status status = Status.parse(jsonArray.getJSONObject(i));
            Log.d(tag, " status=" + status.user.screen_name + status.text);
            statuses.add(status);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    /**
     * 自定义的刷新监听器
     */
    class MyOnrefreshListener implements PullToRefreshBase.OnRefreshListener {
        private int count=1;//记录向上加载的次数
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatedDate=simpleDateFormat.format(new Date());
            // Update the LastUpdatedLabel
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(formatedDate);
           PullToRefreshBase.Mode mode= mListview.getCurrentMode();//注意是currentmode，不是mode
            Log.d(tag, "mode=" + mode);
            Log.d(tag, "mode=" + (mode==PullToRefreshBase.Mode.PULL_FROM_START));
            Log.d(tag, "mode=" + (mode==PullToRefreshBase.Mode.PULL_FROM_END));
            if(mode== PullToRefreshBase.Mode.PULL_FROM_START){      //下拉刷新
                refreshView.getLoadingLayoutProxy().setPullLabel("下拉可以刷新");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放刷新");
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新...");
                statusesAPI.friendsTimeline(0, 0, 20, 1, false, 0, false, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        try {
                            statuses.clear();
                            addToStatuses(s);
                            adapter.notifyDataSetChanged();
                            mListview.onRefreshComplete();//调用这个方法表明刷新已经完了，会重置ui，将刷新的部分隐藏
                        } catch (JSONException e) {
                            Log.d(tag, "发生了异常");
                            e.printStackTrace();

                        }

                    }
                    @Override
                    public void onWeiboException(WeiboException e) {

                    }
                });
            }else if(mode== PullToRefreshBase.Mode.PULL_FROM_END){      //向上加载
                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载...");
                refreshView.getLoadingLayoutProxy().setPullLabel("上拉可以加载");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放刷新");
                statusesAPI.friendsTimeline(0, 0, 20, ++count, false, 0, false, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        try {
                            addToStatuses(s);
                            adapter.notifyDataSetChanged();
                            mListview.onRefreshComplete();//调用这个方法表明刷新已经完了，会重置ui，将刷新的部分隐藏
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


        }
    }


}

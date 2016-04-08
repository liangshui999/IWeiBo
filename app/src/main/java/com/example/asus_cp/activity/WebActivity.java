package com.example.asus_cp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.Util.MyStringUtil;
import com.example.asus_cp.iweibo.R;

/**
 * 点击url后跳转到该页面显示内容
 * Created by asus-cp on 2016-04-08.
 */
public class WebActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        String url=getIntent().getStringExtra(MyStringUtil.URL_KEY);
        WebView webView= (WebView) findViewById(R.id.web_view);
        webView.loadUrl(url);
        webView.setWebViewClient(new MyWebViewClient());
    }

    class MyWebViewClient extends WebViewClient{
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

package com.example.Util;

import android.app.Application;
import android.content.Context;

/**
 * Created by asus-cp on 2016-03-21.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

    }

    public static Context getContext(){
        return context;
    }
}

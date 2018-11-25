package com.example.elvis.reportviolation.Activity;
/**
 * Elvis Gu, May 2018
 */
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.example.elvis.reportviolation.bean.MyUser;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2018/4/4.
 */

public class MyApplication extends Application {

    private String AppID = "984f97aa8d278bb7409828b7a61cebfb";

    public static MyUser mMyUser;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        Bmob.initialize(this,AppID);
    }
}

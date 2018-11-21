package com.example.elvis.reportviolation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;


import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.MyUser;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/4.
 */

public class WelcomeActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        Handler handler = new Handler(Looper.getMainLooper());
        final MyUser myUser = BmobUser.getCurrentUser(MyUser.class);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myUser != null){
                        intent = new Intent(WelcomeActivity.this,MainActivity.class);
                }else{
                    intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },1000);

    }

}

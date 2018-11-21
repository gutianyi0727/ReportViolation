package com.example.elvis.reportviolation.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.MyUser;
import com.example.elvis.reportviolation.util.ToastUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by Administrator on 2018/4/4.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText username, password;
    private RadioButton isstu;
    private Button login;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findById();
        initView();
        setData();
    }

    public void findById(){
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        isstu = (RadioButton) findViewById(R.id.rb_stu);
        login = (Button) findViewById(R.id.bt_log);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    public void initView() {
        login.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    public void setData(){

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_log:
                if (TextUtils.isEmpty(username.getText().toString())){
                    ToastUtil.showText(this,"用户名为空!");
                    break;
                }
                if (TextUtils.isEmpty(password.getText().toString())){
                    ToastUtil.showText(this,"密码为空!");
                    break;
                }
                userLogin(username.getText().toString(),password.getText().toString(),isstu.isChecked());
                break;
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    ActivityOptions options =  ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
//                    startActivity(new Intent(this, RegistActivity.class), options.toBundle());
//                } else {
                    startActivity(new Intent(this, RegistActivity.class));
//                }
                break;
        }
    }

    /**
     * 用户登陆
     * @param username 用户账号
     * @param password  密码
     */
    private void userLogin(String username, String password, final Boolean isReporter){
        MyUser myUser = new MyUser();
        myUser.setUsername(username);
        myUser.setPassword(password);
        myUser.setReporter(isReporter);
        myUser.login(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null){
                    if (getCurrentUser(isReporter)){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("isthePolice",isReporter);
                        startActivity(intent);
                        finish();
                    }else {
                        BmobUser.logOut();
                        ToastUtil.showText(LoginActivity.this,"您的身份有误");
                    }
                }else {
                    ToastUtil.showText(LoginActivity.this,e.getMessage());
                }
            }
        });
    }

    /**
     * 获取当前用户信息
     */
    private Boolean getCurrentUser(Boolean isReporter){
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        Log.d("--username--",myUser.getUsername());
        Log.d("--isstudent--",myUser.getReporter() ? "student" : "teacher");
        return myUser.getReporter() == isReporter;
    }
}

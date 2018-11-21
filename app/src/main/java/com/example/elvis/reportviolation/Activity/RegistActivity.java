package com.example.elvis.reportviolation.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.MyUser;
import com.example.elvis.reportviolation.util.ToastUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by Administrator on 2018/4/4.
 */

public class RegistActivity extends BaseActivity implements View.OnClickListener{

    private CardView cv_add;
    private EditText username,password,repeatpassword;
    private RadioButton isstu;
    private Button regist;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        findById();
        initView();
        setData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    public void findById() {
        cv_add = (CardView) findViewById(R.id.cv_add);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        repeatpassword = (EditText) findViewById(R.id.et_repeatpassword);
        isstu = (RadioButton) findViewById(R.id.rb_stu);
        regist = (Button) findViewById(R.id.bt_reg);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    public void initView(){
        regist.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    public void setData(){

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cv_add.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealShow() {
        Animator animator = ViewAnimationUtils.createCircularReveal(cv_add,cv_add.getWidth()/2,0,fab.getWidth()/2,cv_add.getHeight());
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                cv_add.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        animator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cv_add, cv_add.getWidth() / 2, 0, cv_add.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cv_add.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegistActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_reg:
                if (TextUtils.isEmpty(username.getText().toString())){
                    ToastUtil.showText(this,"用户名为空!");
                    break;
                }
                if (TextUtils.isEmpty(password.getText().toString())){
                    ToastUtil.showText(this,"密码为空!");
                    break;
                }
                if (TextUtils.isEmpty(repeatpassword.getText().toString())){
                    ToastUtil.showText(this,"请再次输入密码!");
                    break;
                }
                if (!password.getText().toString().equals(repeatpassword.getText().toString())){
                    ToastUtil.showText(this,"两次密码不一致!");
                    break;
                }
                userRegist(username.getText().toString(),password.getText().toString(),isstu.isChecked());
                break;
            case R.id.fab:
                animateRevealClose();
                break;
        }

    }

    /**
     * 用户注册
     * @param username 账号
     * @param password 密码
     * @param isReporter 是否为学生
     */
    private void userRegist(final String username, String password, final Boolean isReporter) {
        MyUser myUser = new MyUser();
        myUser.setUsername(username);
        myUser.setPassword(password);
        myUser.setReporter(isReporter);
        myUser.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser user, BmobException e) {
                if (e == null) {
                    ToastUtil.showText(RegistActivity.this,"注册成功");
                    Intent intent = new Intent(RegistActivity.this,MainActivity.class);
                    intent.putExtra("isReporter",isReporter);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtil.showText(RegistActivity.this,e.getMessage());
                }
            }
        });
    }
}

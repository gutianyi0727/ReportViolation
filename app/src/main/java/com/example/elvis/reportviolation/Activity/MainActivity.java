package com.example.elvis.reportviolation.Activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.elvis.reportviolation.Adapter.GridAdapter;
import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.MessageEvent;
import com.example.elvis.reportviolation.bean.MyUser;
import com.example.elvis.reportviolation.ui.SlidingMenu;
import com.example.elvis.reportviolation.util.ToastUtil;
import com.example.elvis.reportviolation.util.ToolsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bmob.v3.BmobUser;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private SlidingMenu mSlidingMenu;
    private RelativeLayout mMenu, mContext;
    private ImageView menu_image;
    private TextView menu_name;
    private Button menu_out;
    private ImageButton show_menu;
    private GridView gridView;

    private boolean isReporter;
    private GridAdapter gridAdapter;
    private MyUser myUser;

    private Long exitTime = 1L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        MyApplication.mMyUser = BmobUser.getCurrentUser(MyUser.class);
        isReporter = BmobUser.getCurrentUser(MyUser.class).getReporter();

        //获取权限
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        findById();
        initView();
        setData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void findById() {
        mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
        mMenu = (RelativeLayout) findViewById(R.id.menu);
        mContext = (RelativeLayout) findViewById(R.id.context);
        menu_image = (ImageView) findViewById(R.id.main_menu_image);
        menu_name = (TextView) findViewById(R.id.main_menu_name);
        menu_out = (Button) findViewById(R.id.main_menu_out);
        show_menu = (ImageButton) findViewById(R.id.show_main_menu);
        gridView = (GridView) findViewById(R.id.gridview);
    }

    public void initView() {
        //默认头像
        Glide.with(this).load(R.mipmap.pic).bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(menu_image);

        menu_image.setOnClickListener(this);
        menu_name.setOnClickListener(this);
        menu_out.setOnClickListener(this);
        show_menu.setOnClickListener(this);
    }

    public void setData() {
        //设置主页图标
        gridAdapter = new GridAdapter(this,isReporter);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);

        //设置左侧头像姓名
        myUser = BmobUser.getCurrentUser(MyUser.class);
        if (myUser == null){
            ToastUtil.showText(MainActivity.this,"程序异常，用户未登陆");
            finish();
        }else {
            if (myUser.getAvatar() != null){
                Glide.with(this).load(myUser.getAvatar().getUrl()).bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(menu_image);
            }
            if (ToolsUtil.isNotNull(myUser.getName())){
                menu_name.setText(myUser.getName());
            }else {
                menu_name.setText(myUser.getUsername());
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.main_menu_image:
            case R.id.main_menu_name:
                intent = new Intent(MainActivity.this,UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.main_menu_out:
                BmobUser.logOut();
                intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.show_main_menu:
                mSlidingMenu.showMenu();
                break;


        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position){
            case 0:
                intent = new Intent(MainActivity.this,UserInfoActivity.class);
                startActivity(intent);
                break;

            case 1:
                if(isReporter){
                    intent = new Intent(MainActivity.this,ReporterMapActivity.class);
                }else{
                    intent = new Intent(MainActivity.this,TransferData.class);
                }
                startActivity(intent);
                break;

            case 2:
                intent = new Intent(MainActivity.this,HeatMap.class);
                startActivity(intent);
                break;

            case 3:
                intent = new Intent(MainActivity.this,ReporterHistoryTable.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if((System.currentTimeMillis()-exitTime) > 2000){
            ToastUtil.showText(getApplicationContext(),"再次点击返回退出");
            exitTime = System.currentTimeMillis();
        }
        else {
            finish();
            System.exit(0);
        }
    }

    /**
     * EventBus 处理事件
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMes(MessageEvent messageEvent){
        if (messageEvent.getWhat() != null){
            switch (messageEvent.getWhat()){
                case 0:
                    if (messageEvent.getMessage() != null){
                        Glide.with(this).load(messageEvent.getMessage())
                                .bitmapTransform(new CropCircleTransformation(this))
                                .crossFade(1000)
                                .into(menu_image);
                    }
                    break;
                case 1:
                    if (messageEvent.getMessage() != null){
                        menu_name.setText(messageEvent.getMessage());
                    }
                    break;
            }
        }

    }
}

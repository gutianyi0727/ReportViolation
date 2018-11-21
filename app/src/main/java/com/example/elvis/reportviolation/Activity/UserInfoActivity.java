package com.example.elvis.reportviolation.Activity;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.MessageEvent;
import com.example.elvis.reportviolation.bean.MyUser;
import com.example.elvis.reportviolation.util.ToastUtil;
import com.example.elvis.reportviolation.util.ToolsUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener{

    private ImageButton back;
    private ImageView pic;
    private RelativeLayout name_relative,password_relative;
    private TextView name_content;

    private MyUser mMyUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mMyUser = MyApplication.mMyUser;

        findById();
        initView();
        setData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void findById() {
        back = (ImageButton) findViewById(R.id.back_user_info);
        pic = (ImageView) findViewById(R.id.image_user_info);
        name_relative = (RelativeLayout) findViewById(R.id.namerel_user_info);
        password_relative = (RelativeLayout) findViewById(R.id.passwordrel_user_info);
        name_content = (TextView) findViewById(R.id.name_user_info);
    }

    public void initView() {
        Glide.with(this).load(R.mipmap.pic).bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(pic);

        back.setOnClickListener(this);
        pic.setOnClickListener(this);
        name_relative.setOnClickListener(this);
        password_relative.setOnClickListener(this);

    }

    public void setData() {
        if (mMyUser.getPicpath() != null){
            Glide.with(this).load(mMyUser.getPicpath()).bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(pic);
        }else if (mMyUser.getAvatar() != null){
            Glide.with(this).load(mMyUser.getAvatar().getUrl()).bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(pic);
        }
        if (mMyUser.getName() != null){
            name_content.setText(mMyUser.getName());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_user_info:
                finish();
                break;
            case R.id.image_user_info:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
                break;
            case R.id.namerel_user_info:
                showAlertDialog("姓名");
                break;
            case R.id.passwordrel_user_info:
                showAlertDialog("密码");
                break;
        }
    }

    /**
     * 用dialog显示修改内容
     * @param s 区分标签
     */
    public void showAlertDialog(final String s){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.dialog_user_info,null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_user_info);
        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        Button add = (Button) dialog.findViewById(R.id.dialog_add);
        final Button cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
        final EditText dialog_content = (EditText) dialog.findViewById(R.id.dialog_content);
        dialog_title.setText("请输入你的"+s);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = dialog_content.getText().toString();
                if (ToolsUtil.isNotNull(str)){
                    dialog.dismiss();
                    MyUser myUser = new MyUser();
                    switch(s){
                        case "姓名":
                            name_content.setText(str);
                            myUser.setName(str);
                            EventBus.getDefault().post(new MessageEvent(1,str));
                            MyApplication.mMyUser.setName(str);
                            break;
                        case "密码":
                            myUser.setPassword(str);
                            MyApplication.mMyUser.setPassword(str);
                            break;
                    }
                    myUser.update(mMyUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null){
                                ToastUtil.showText(UserInfoActivity.this,e.getMessage());
                            }else{
                                ToastUtil.showText(UserInfoActivity.this,"修改成功");
                            }
                        }
                    });
                }else {
                    ToastUtil.showText(UserInfoActivity.this,"输入不能为空");
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片的返回值
        if (requestCode == 1 && resultCode == RESULT_OK ){
            //获取返回的数据，这里是android自定义的Uri地址
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //获取选择照片的数据视图
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            //从数据视图中获取已选择图片的路径
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //将图片显示到界面上
            Glide.with(this).load(picturePath).bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(pic);
            MyApplication.mMyUser.setPicpath(picturePath);
            //消息传递，修改主界面的头像
            EventBus.getDefault().post(new MessageEvent(0,picturePath));
            final BmobFile bmobFile = new BmobFile(new File(picturePath));
            bmobFile.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null){
                        MyUser myUser = new MyUser();
                        myUser.setAvatar(bmobFile);
                        myUser.update(mMyUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null){
                                    ToastUtil.showText(UserInfoActivity.this,e.getMessage());
                                }
                            }
                        });
                    }else {
                        ToastUtil.showText(UserInfoActivity.this,e.getMessage());
                    }
                }
            });
        }
    }
}

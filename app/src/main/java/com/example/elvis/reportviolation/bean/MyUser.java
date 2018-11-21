package com.example.elvis.reportviolation.bean;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2018/4/28.
 */

public class MyUser extends BmobUser {

    private BmobFile avatar;  //头像
    private String name;      //姓名
    private String picpath;   //图片本地地址
    private Boolean isReporter; //是否user



    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }


    public Boolean getReporter() {
        return isReporter;
    }

    public void setReporter(Boolean reporter) {
        isReporter = reporter;
    }
}

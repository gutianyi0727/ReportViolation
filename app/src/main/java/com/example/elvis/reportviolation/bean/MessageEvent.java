package com.example.elvis.reportviolation.bean;

/**
 * Created by Administrator on 2018/5/4.
 */

public class MessageEvent {

    private Integer what;
    private String message;

    public MessageEvent(int what, String message){
        this.what = what;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Integer getWhat() {
        return what;
    }

    public void setWhat(Integer what) {
        this.what = what;
    }
}

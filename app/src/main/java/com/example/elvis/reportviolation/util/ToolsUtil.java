package com.example.elvis.reportviolation.util;


/**
 * Created by Administrator on 2018/4/5.
 */

public class ToolsUtil {

    //  字符串是否为null
    public  static boolean isNotNull(String value){
        if(value==null){
            return false;
        }
        if("".equals(value)){
            return false;
        }
        if("null".equals(value)){
            return false;
        }
        if (" ".equals(value)) {
            return false;
        }
        return true;
    }

}

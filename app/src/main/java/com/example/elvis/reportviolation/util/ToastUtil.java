package com.example.elvis.reportviolation.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/4/5.
 */

public class ToastUtil {

    // Toast对象
    private static Toast toast = null;

    private ToastUtil(){}

    /**
     * 显示Toast
     */
    public static void showText(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }
}

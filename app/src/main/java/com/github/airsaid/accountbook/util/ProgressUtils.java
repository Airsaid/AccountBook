package com.github.airsaid.accountbook.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.github.airsaid.accountbook.R;


/**
 * Created by zhouyou on 2016/7/29.
 * Class desc:
 *
 * ProgressDialog封装工具类
 */
public class ProgressUtils {

    private static ProgressDialog dialog = null;

    public static void show(Context context){
        show(context, null);
    }

    public static void show(Context context, String msg){
        dialog = new ProgressDialog(context);
        dialog.setMessage(msg == null ? UiUtils.getString(R.string.load_msg) : msg);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void dismiss(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}

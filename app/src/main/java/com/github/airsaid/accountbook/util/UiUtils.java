package com.github.airsaid.accountbook.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.airsaid.accountbook.mvp.main.MainActivity;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseApplication;
import com.github.airsaid.accountbook.mvp.login.LoginActivity;


/**
 * Created by zhouyou on 2016/6/27.
 * Class desc: ui 操作相关封装
 */
public class UiUtils {

    /**
     * 获取上下文
     */
    public static Context getContext() {
        return BaseApplication.getContext();
    }

    /**
     * 获取资源操作类
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取字符串资源
     *
     * @param id 资源id
     * @return 字符串
     */
    public static String getString(int id) {
        return getResources().getString(id);
    }

    /**
     * 获取字符串数组资源
     *
     * @param id 资源id
     * @return 字符串数组
     */
    public static String[] getStringArray(int id) {
        return getResources().getStringArray(id);
    }

    /**
     * 获取颜色资源
     */
    public static int getColor(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    /**
     * 获取颜色资源
     *
     * @param id    资源id
     * @param theme 样式
     * @return
     */
    public static int getColor(int id, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(id, theme);
        }
        return getResources().getColor(id);
    }

    /**
     * 获取drawable资源
     *
     * @param id 资源id
     * @return
     */
    public static Drawable getDrawable(int id) {
        return ContextCompat.getDrawable(getContext(), id);
    }

    /**
     * 通过图片名称获取图片资源 id
     * @param imageName 图片名称
     * @return 图片资源 id
     */
    public static int getImageResIdByName(String imageName){
        return getResources().getIdentifier(imageName, "mipmap"
                , AppUtils.getPackageName());
    }

    /**
     * 加载布局（使用View方式）
     *
     * @param resource 布局资源id
     * @return View
     */
    public static View inflate(int resource) {
        return View.inflate(getContext(), resource, null);
    }

    /**
     * 检查输入的内容是否为空
     */
    public static boolean checkEmpty(EditText editText) {
        if(TextUtils.isEmpty(editText.getText().toString())){
            ToastUtils.show(UiUtils.getContext(), UiUtils.getString(R.string.hint_empty));
            return true;
        }
        return false;
    }

    /**
     * 设置透明状态栏
     * @param activity
     */
    public static  void setStatusBar(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            // 对于4.4以上5.0以下版本，设置透明状态栏
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            // 5.0及以上版本，设置透明状态栏
            Window window = activity.getWindow();
            // 清理4.4Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            // 添加标志位
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 设置为透明
            window.setStatusBarColor(0);
        }
    }

    /**
     * 进入首页
     */
    public static void enterHomePage(Context context){
        ActivityManager.getInstance().popAllActivity();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        if(context instanceof  Activity){
            ((Activity)context).finish();
        }
    }

    /**
     * 进入登录页
     * @param context 上下文
     */
    public static void enterLoginPage(Context context){
        enterLoginPage(context, false);
    }

    /**
     * 进入登录页
     * @param context  上下文
     * @param isFinish 是否关闭当前 Activity
     */
    public static void enterLoginPage(Context context, boolean isFinish){
        ActivityManager.getInstance().popAllActivity();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(!(context instanceof Activity)){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if(isFinish && context instanceof Activity){
            ((Activity)context).finish();
        }
    }

    public static ColorStateList getColorList(int resId) {
        return ContextCompat.getColorStateList(UiUtils.getContext(), resId);
    }

    public static void setCompoundDrawables(TextView textView, Drawable left, Drawable top, Drawable right, Drawable bottom){
        textView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * 获取列表为空时显示的 Empty View
     * @return 默认 Empty View
     */
    public static View getEmptyView(Context context, RecyclerView recyclerView){
        return getEmptyView(context, recyclerView, null, -1);
    }

    /**
     * 获取列表为空时显示的 Empty View
     * @param emptyText 提示文字
     * @return Empty View
     */
    public static View getEmptyView(Context context, RecyclerView recyclerView, String emptyText){
        return getEmptyView(context, recyclerView, emptyText, -1);
    }

    /**
     * 获取列表为空时显示的 Empty View
     * @param emptyText  提示文字
     * @param emptyImgId 图片
     * @return Empty View
     */
    public static View getEmptyView(Context context, RecyclerView recyclerView, String emptyText, int emptyImgId){
        View emptyView = LayoutInflater.from(context).inflate(R.layout.view_empty, (ViewGroup) recyclerView.getParent(), false);
        if(emptyText != null){
            ((TextView)emptyView.findViewById(R.id.txt_empty)).setText(emptyText);
        }
        if(emptyImgId != -1){
            ((ImageView)emptyView.findViewById(R.id.img_empty)).setImageResource(emptyImgId);
        }
        return emptyView;
    }

    /**
     * 通过场景名获取场景资源图片 id
     * @param scene 场景名称
     * @return 场景资源图片 id
     */
    public static int getSceneImageResIdByName(String scene){
        switch (scene){
            case "日常":
                return R.mipmap.book_scene1;
            case "校园":
                return R.mipmap.book_scene2;
            case "生意":
                return R.mipmap.book_scene3;
            case "家庭":
                return R.mipmap.book_scene4;
            case "旅行":
                return R.mipmap.book_scene5;
            case "装修":
                return R.mipmap.book_scene6;
            case "结婚":
                return R.mipmap.book_scene7;
            default:
                return R.mipmap.book_scene1;
        }
    }

    /** 显示不带 null 的字符 */
    public static String show(String text){
        return text != null ? text : "";
    }

}

package com.github.airsaid.accountbook.util;

import android.content.Context;
import android.widget.Toast;

import java.util.Locale;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/5
 * @desc 退出应用帮助类。
 */
public class ExitAppHelper {

    public static String DEFAULT_HINT_MESSAGE_CHINA = "再按一次退出程序";
    public static String DEFAULT_HINT_MESSAGE_OTHER = "Press again to exit the program";
    private int effectiveIntervalTime;	// 有效的间隔时间，单位毫秒
    private long lastClickTime;	// 上次点击时间
    private String hintMessage;	// 提示消息
    private Context context;

    /**
     * 创建一个双击退出识别器
     * @param context 上下文
     * @param hintMessage 提示消息
     * @param effectiveIntervalTime 有效间隔时间
     */
    public ExitAppHelper(Context context, String hintMessage, int effectiveIntervalTime) {
        this.context = context;
        this.hintMessage = hintMessage;
        this.effectiveIntervalTime = effectiveIntervalTime;
    }

    /**
     * 创建一个双击退出识别器，有效间隔时间默认为 2000 毫秒
     * @param context 上下文
     * @param hintContent 提示消息
     */
    public ExitAppHelper(Context context, String hintContent) {
        this(context, hintContent, 2000);
    }

    /**
     * 创建一个双击退出识别器，中国环境下默认提示消息为“再按一次退出程序”，其它环境下默认提示消息为“Press again to exit the program”；有效间隔时间默认为2000毫秒
     * @param context 上下文
     */
    public ExitAppHelper(Context context) {
        this(context, isZh(context) ? DEFAULT_HINT_MESSAGE_CHINA : DEFAULT_HINT_MESSAGE_OTHER, 2000);
    }

    /**
     * 点击，你需要重写 Activity 的 onBackPressed() 方法，先调用此方法，
     * 如果返回 true 就执行父类的 onBackPressed() 方法来关闭 Activity 否则不执行
     * @return 当两次点击时间间隔小于有效间隔时间时就会返回 true，否则返回 false
     */
    public boolean click(){
        long currentTime = System.currentTimeMillis();
        boolean result = (currentTime-lastClickTime) < effectiveIntervalTime;
        lastClickTime = currentTime;
        if(!result){
            Toast.makeText(context, hintMessage, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    /**
     * 设置有效间隔时间，单位毫秒
     * @param effectiveIntervalTime 效间隔时间，单位毫秒。当两次点击时间间隔小于有效间隔时间 click() 方法就会返回true
     */
    public void setEffectiveIntervalTime(int effectiveIntervalTime) {
        this.effectiveIntervalTime = effectiveIntervalTime;
    }

    /**
     * 设置提示消息
     * @param hintMessage 当前点击同上次点击时间间隔大于有效间隔时间 click() 方法就会返回 false，并且显示提示消息
     */
    public void setHintMessage(String hintMessage) {
        this.hintMessage = hintMessage;
    }

    /**
     * 判断是否是中文环境
     */
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.startsWith("zh"))
            return true;
        else
            return false;
    }
}

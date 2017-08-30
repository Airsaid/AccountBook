package com.github.airsaid.accountbook.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.AppConstants;

/**
 * @author airsaid
 * @github https://github.com/airsaid
 * @date 2017/8/29
 * @desc 主题管理类
 */
public class ThemeManager {

    private static final String KEY_THEME = "key_theme";

    private String[] mThemes = {"少女粉", "酷炫黑", "原谅绿", "胖次蓝", "基佬紫", "活力橙", "大地棕"};

    private static ThemeManager instance;

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public String[] getThemes(){
        return mThemes;
    }

    /**
     * 设置主题色
     * @param context   activity
     * @param theme     主题名称
     */
    public void setTheme(Activity context, String theme){
        String curTheme = (String) SPUtils.getSP(context, KEY_THEME, mThemes[0]);
        if(curTheme != null && curTheme.equals(theme)){
            return;
        }

        SPUtils.setSP(context, KEY_THEME, theme);

        context.finish();
        Intent intent = context.getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstants.EXTRA_IS_UPDATE_THEME, true);
        context.startActivity(intent);
    }

    /**
     * 获取当前主题名
     * @param context 上下文
     * @return 如: 少女粉
     */
    public String getCurThemeName(Context context){
        return (String) SPUtils.getSP(context, KEY_THEME, mThemes[0]);
    }

    public void init(Context context) {
        String theme = (String) SPUtils.getSP(context, KEY_THEME, mThemes[0]);
        if(theme.equals(mThemes[0])){
            context.setTheme(R.style.AppTheme);
        }else if(theme.equals(mThemes[1])){
            context.setTheme(R.style.AppTheme_Black);
        }else if(theme.equals(mThemes[2])){
            context.setTheme(R.style.AppTheme_Green);
        }else if(theme.equals(mThemes[3])){
            context.setTheme(R.style.AppTheme_Blue);
        }else if(theme.equals(mThemes[4])){
            context.setTheme(R.style.AppTheme_Purple);
        }else if(theme.equals(mThemes[5])){
            context.setTheme(R.style.AppTheme_Orange);
        }else if(theme.equals(mThemes[6])){
            context.setTheme(R.style.AppTheme_Brown);
        }
    }
}

package com.github.airsaid.accountbook.base;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;

/**
 * @author Airsaid
 * @Date 2017/2/19 23:15
 * @Blog http://blog.csdn.net/airsaid
 * @Desc Applicatoin 基础类
 */
public class BaseApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"66o7GyT16nMJtPOFP4TVBim3-gzGzoHsz","MqA1AU779Nd3OpTAQBkWUWXu");
        // 开启调试日志，放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
    }

    /**
     * 获取上下文
     * @return
     */
    public static Context getContext(){
        return mContext;
    }

}

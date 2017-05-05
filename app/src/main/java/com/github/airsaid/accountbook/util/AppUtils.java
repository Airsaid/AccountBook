package com.github.airsaid.accountbook.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/3/14
 * @desc App 相关操作工具类.
 */
public class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static Context getContext(){
        return UiUtils.getContext();
    }

    /**
     * 获取当前应用包名
     * @return 如: com.airsaid.app
     */
    public static String getPackageName(){
        return UiUtils.getContext().getPackageName();
    }

    /**
     * 获取当前应用版本名称
     * @return 如: 1.0.0
     */
    public static String getAppVersionName(){
        String versionName = "";
        try {
            PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取当前应用最后一次更新的时间
     * @return 时间戳
     */
    public static long getLastUpdateTime(){
        try {
            PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            return packageInfo.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

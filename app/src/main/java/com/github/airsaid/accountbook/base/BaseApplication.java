package com.github.airsaid.accountbook.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.github.airsaid.accountbook.BuildConfig;
import com.github.airsaid.accountbook.data.AboutApp;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.DaoMaster;
import com.github.airsaid.accountbook.data.DaoSession;
import com.github.airsaid.accountbook.data.Msg;
import com.tencent.bugly.Bugly;

import org.greenrobot.greendao.database.Database;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/13
 * @desc Application 基础类
 *
 * 【注意事项：】
 *
 * 由于应用当中用到了 leancloud 和 bugly 服务，其 appid、appkey 由于私密原因已经隐藏。
 * 请在 local.properties 文件配置如下信息，配置后即可正常运行项目：
 *
 * leancloud.appid=你在 leancloud 平台申请的 appid
 * leancloud.appkey=你在 leancloud 平台申请的 appkey
 * bugly.appid=你在 bugly 平台申请的 appid
 */
public class BaseApplication extends Application{

    private static BaseApplication mInstance;
    private static Context mContext;
    private DaoSession mSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;
        initGreenDao();
        registSubClass();
        initLeancloud();
        initCrashReport();
    }

    /**
     * 获取 Application 实例
     * @return BaseApplication
     */
    public static BaseApplication getInstance(){
        return mInstance;
    }

    /**
     * 初始化 greenDAO
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "account-db");
        Database db = helper.getWritableDb();
        mSession = new DaoMaster(db).newSession();
    }

    /**
     * 获取 DaoSession 对象
     * @return DaoSession
     */
    public DaoSession getSession(){
        return mSession;
    }

    /**
     * 注册子类
     */
    private void registSubClass() {
        AVObject.registerSubclass(AccountBook.class);
        AVObject.registerSubclass(Account.class);
        AVObject.registerSubclass(AboutApp.class);
        AVObject.registerSubclass(Msg.class);
    }

    /**
     * 初始化 leancloud
     */
    private void initLeancloud() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            String leancloudAppid = appInfo.metaData.getString("LEANCLOUD_APPID");
            String leancloudAppkey = appInfo.metaData.getString("LEANCLOUD_APPKEY");
            // 初始化参数依次为 this, AppId, AppKey
            AVOSCloud.initialize(this, leancloudAppid, leancloudAppkey);
            // 开启调试日志，放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
            AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 Bugly
     */
    private void initCrashReport() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            String appId = appInfo.metaData.getString("BUGLY_APPID");
            Bugly.init(getContext(), appId, BuildConfig.DEBUG);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取上下文
     * @return
     */
    public static Context getContext(){
        return mContext;
    }

}

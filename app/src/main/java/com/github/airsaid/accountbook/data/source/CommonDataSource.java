package com.github.airsaid.accountbook.data.source;

import com.github.airsaid.accountbook.data.AboutApp;
import com.github.airsaid.accountbook.data.Error;

/**
 * @author Airsaid
 * @Date 2017/4/24 21:43
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public interface CommonDataSource {

    /** 关于 APP */
    void aboutApp(GetAboutAppInfoCallback callback);

    interface GetAboutAppInfoCallback{
        void getSuccess(AboutApp about);
        void getFail(Error e);
    }

}

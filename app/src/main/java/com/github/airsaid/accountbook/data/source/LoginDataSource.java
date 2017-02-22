package com.github.airsaid.accountbook.data.source;

import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:50
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public interface LoginDataSource {

    interface LoginCallback{
        void loginSuccess();
        void loginFail(String msg);
    }

    void login(User user, LoginCallback callback);

}

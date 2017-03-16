package com.github.airsaid.accountbook.data.source;

import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:50
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public interface RegisterDataSource {

    interface RegisterCallback{
        void registerSuccess(User user);
        void registerFail(Error e);
    }

    interface GetCodeCallback{
        void getSuccess();
        void getFail(Error e);
    }

    void register(User user, RegisterCallback callback);

    void getCode(String phone, GetCodeCallback callback);
}

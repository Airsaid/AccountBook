package com.github.airsaid.accountbook.login;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:35
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 登录契约类,用于统一约定接口
 */
public interface LoginContract {

    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);
        void showLoginSuccess();
        void showLoginFail(Error e);
    }

    interface Presenter extends BasePresenter{
        void login(User user);
        boolean checkUserInfo(User user);
    }

}

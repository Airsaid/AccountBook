package com.github.airsaid.accountbook.mvp.login;

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
        void showLoginSuccess();
        void showLoginFail(Error e);
        void showSendVerifyCodeSuccess();
        void showSendVerifyCodeFail(Error e);
        void showVerifyPhoneSuccess();
        void showVerifyPhoneFail(Error e);
        void createDefaultType(String uid);
    }

    interface Presenter extends BasePresenter{
        boolean checkUserInfo(User user);
        void login(User user);
        void requestPhoneVerify(String phone);
        void verifyPhone(String code);
    }

}

package com.github.airsaid.accountbook.mvp.register;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @Date 2017/2/22 23:07
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public interface RegisterContract {

    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);
        void showRegisterSuccess();
        void showRegisterFail(Error e);
        void showVerifyPhoneSuccess();
        void showVerifyPhoneFail(Error e);
    }

    interface Presenter extends BasePresenter{
        void register(User user);
        void verifyPhone(String code);
    }

}

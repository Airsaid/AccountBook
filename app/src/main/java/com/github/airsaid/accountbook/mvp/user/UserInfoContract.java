package com.github.airsaid.accountbook.mvp.user;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/10
 * @desc
 */
public interface UserInfoContract {

    interface View extends BaseView<Presenter> {
        void showUpdateIcon();
        void showUpdateUsernameDialog();
        void showUpdateSexDialog();
        void showUpdateAgeDialog();
        void saveUserInfoSuccess();
        void saveUserInfoFail(Error e);
    }

    interface Presenter extends BasePresenter {
        void saveUserInfo(User user);
    }
}

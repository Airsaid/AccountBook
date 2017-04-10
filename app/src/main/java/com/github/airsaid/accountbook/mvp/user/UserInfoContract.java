package com.github.airsaid.accountbook.mvp.user;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/10
 * @desc
 */
public interface UserInfoContract {

    interface View extends BaseView<Presenter> {
        void showUpdateIconDialog();
        void showUpdateUsernameDialog();
        void showUpdateSexDialog();
        void showUpdateAgeDialog();
    }

    interface Presenter extends BasePresenter {

    }
}

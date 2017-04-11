package com.github.airsaid.accountbook.mvp.user;

import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.UserDataSource;
import com.github.airsaid.accountbook.data.source.UserRepository;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/10
 * @desc
 */
public class UserInfoPresenter implements UserInfoContract.Presenter {

    private final UserRepository mRepository;
    private final UserInfoContract.View mView;

    public UserInfoPresenter(UserRepository repository, UserInfoContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void saveUserInfo(User user) {
        mRepository.saveUserInfo(user, new UserDataSource.SaveUserInfoCallback() {
            @Override
            public void saveSuccess() {
                mView.saveUserInfoSuccess();
            }

            @Override
            public void saveFail(Error e) {
                mView.saveUserInfoFail(e);
            }
        });
    }
}

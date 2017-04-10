package com.github.airsaid.accountbook.mvp.register;

import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.UserDataSource;
import com.github.airsaid.accountbook.data.source.UserRepository;

/**
 * @author Airsaid
 * @Date 2017/2/22 23:10
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class RegisterPresenter implements RegisterContract.Presenter {

    private final UserRepository mRepository;
    private final RegisterContract.View mView;

    public RegisterPresenter(UserRepository repository, RegisterContract.View view){
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void register(User user) {
        mView.setLoadingIndicator(true);
        mRepository.register(user, new UserDataSource.RegisterCallback() {
            @Override
            public void registerSuccess() {
                mView.setLoadingIndicator(false);
                mView.showRegisterSuccess();
            }

            @Override
            public void registerFail(Error e) {
                mView.setLoadingIndicator(false);
                mView.showRegisterFail(e);
            }
        });
    }

    @Override
    public void verifyPhone(String code) {
        mView.setLoadingIndicator(true);
        mRepository.verifyPhone(code, new UserDataSource.VerifyPhoneCallback() {
            @Override
            public void verifySuccess() {
                mView.setLoadingIndicator(false);
                mView.showVerifyPhoneSuccess();
            }

            @Override
            public void verifyFail(Error e) {
                mView.setLoadingIndicator(false);
                mView.showVerifyPhoneFail(e);
            }
        });
    }

    @Override
    public void start() {

    }
}

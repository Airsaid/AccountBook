package com.github.airsaid.accountbook.login;

import android.text.TextUtils;

import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.UserDataSource;
import com.github.airsaid.accountbook.data.source.UserRepository;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:38
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class LoginPresenter implements LoginContract.Presenter {

    private final UserRepository mRepository;
    private final LoginContract.View mView;

    public LoginPresenter(UserRepository repository, LoginContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void login(User user) {
        mView.setLoadingIndicator(true);
        mRepository.login(user, new UserDataSource.LoginCallback() {
            @Override
            public void loginSuccess() {
                mView.setLoadingIndicator(false);
                mView.showLoginSuccess();
            }

            @Override
            public void loginFail(Error e) {
                mView.setLoadingIndicator(false);
                mView.showLoginFail(e);
            }
        });
    }

    @Override
    public void requestPhoneVerify(String phone) {
        mView.setLoadingIndicator(true);
        mRepository.requestPhoneVerify(phone, new UserDataSource.SendVerifyCodeCallback() {
            @Override
            public void sendVerifyCodeSuccess() {
                mView.setLoadingIndicator(false);
                mView.showSendVerifyCodeSuccess();
            }

            @Override
            public void sendVerifyCodeFail(Error e) {
                mView.setLoadingIndicator(false);
                mView.showSendVerifyCodeFail(e);
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
    public boolean checkUserInfo(User user) {
        String username = user.phone;
        String password = user.password;
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            return false;
        }
        return true;
    }

    @Override
    public void start() {

    }
}

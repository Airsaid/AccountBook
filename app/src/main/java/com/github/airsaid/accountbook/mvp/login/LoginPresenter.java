package com.github.airsaid.accountbook.mvp.login;

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
        mRepository.login(user, new UserDataSource.LoginCallback() {
            @Override
            public void loginSuccess() {
                mView.showLoginSuccess();
            }

            @Override
            public void loginFail(Error e) {
                mView.showLoginFail(e);
            }
        });
    }

    @Override
    public void requestPhoneVerify(String phone) {
        mRepository.requestPhoneVerify(phone, new UserDataSource.SendVerifyCodeCallback() {
            @Override
            public void sendVerifyCodeSuccess() {
                mView.showSendVerifyCodeSuccess();
            }

            @Override
            public void sendVerifyCodeFail(Error e) {
                mView.showSendVerifyCodeFail(e);
            }
        });
    }

    @Override
    public void verifyPhone(String code) {
        mRepository.verifyPhone(code, new UserDataSource.VerifyPhoneCallback() {
            @Override
            public void verifySuccess() {
                mView.showVerifyPhoneSuccess();
            }

            @Override
            public void verifyFail(Error e) {
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

package com.github.airsaid.accountbook.login;

import android.text.TextUtils;

import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.LoginDataSource;
import com.github.airsaid.accountbook.data.source.LoginRepository;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:38
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class LoginPresenter implements LoginContract.Presenter {

    private final LoginRepository mRepository;
    private final LoginContract.View mView;

    public LoginPresenter(LoginRepository repository, LoginContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void login(User user) {
        mView.setLoadingIndicator(true);
        mRepository.login(user, new LoginDataSource.LoginCallback() {
            @Override
            public void loginSuccess() {
                mView.setLoadingIndicator(false);
                mView.showLoginSuccess();
            }

            @Override
            public void loginFail(String msg) {
                mView.setLoadingIndicator(false);
                mView.showLoginFail(msg);
            }
        });
    }

    @Override
    public boolean checkUserInfo(User user) {
        String username = user.username;
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

package com.github.airsaid.accountbook.register;

import android.text.TextUtils;

import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.RegisterDataSource;
import com.github.airsaid.accountbook.data.source.RegisterRepository;

/**
 * @author Airsaid
 * @Date 2017/2/22 23:10
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class RegisterPersenter implements RegisterContract.Presenter {

    private final RegisterRepository mRepository;
    private final RegisterContract.View mView;

    public RegisterPersenter(RegisterRepository repository, RegisterContract.View view){
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void register(User user) {
        mView.setLoadingIndicator(true);
        mRepository.register(user, new RegisterDataSource.RegisterCallback() {
            @Override
            public void registerSuccess() {
                mView.setLoadingIndicator(false);
                mView.showRegisterSuccess();
            }

            @Override
            public void registerFail(String msg) {
                mView.setLoadingIndicator(false);
                mView.showRegisterFail(msg);
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

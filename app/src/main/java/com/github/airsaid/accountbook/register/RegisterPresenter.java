package com.github.airsaid.accountbook.register;

import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.RegisterDataSource;
import com.github.airsaid.accountbook.data.source.RegisterRepository;

/**
 * @author Airsaid
 * @Date 2017/2/22 23:10
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class RegisterPresenter implements RegisterContract.Presenter {

    private final RegisterRepository mRepository;
    private final RegisterContract.View mView;

    public RegisterPresenter(RegisterRepository repository, RegisterContract.View view){
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void getCode(String phone) {
        mView.setLoadingIndicator(true);
        mRepository.getCode(phone, new RegisterDataSource.GetCodeCallback() {
            @Override
            public void getSuccess() {
                mView.setLoadingIndicator(false);
                mView.showGetCodeSuccess();
            }

            @Override
            public void getFail(Error e) {
                mView.setLoadingIndicator(false);
                mView.showGetCodeFail(e);
            }
        });
    }

    @Override
    public void register(User user) {
        mView.setLoadingIndicator(true);
        mRepository.register(user, new RegisterDataSource.RegisterCallback() {

            @Override
            public void registerSuccess(User user) {
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
    public void start() {

    }
}

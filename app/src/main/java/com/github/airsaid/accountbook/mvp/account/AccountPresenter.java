package com.github.airsaid.accountbook.mvp.account;

import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.data.source.AccountRepository;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc
 */
public class AccountPresenter implements AccountContract.Presenter {

    private final AccountRepository mRepository;
    private final AccountContract.View mView;

    public AccountPresenter(AccountRepository repository, AccountContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void saveAccount(User user, Account account) {
        mRepository.saveAccount(user, account, new Callback() {
            @Override
            public void requestSuccess() {
                mView.saveSuccess();
            }

            @Override
            public void requestFail(Error e) {
                mView.saveFail(e);
            }
        });
    }
}

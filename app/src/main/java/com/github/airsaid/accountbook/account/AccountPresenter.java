package com.github.airsaid.accountbook.account;

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
}

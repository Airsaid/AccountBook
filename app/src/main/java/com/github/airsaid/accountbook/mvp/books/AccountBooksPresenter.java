package com.github.airsaid.accountbook.mvp.books;

import com.github.airsaid.accountbook.data.source.AccountRepository;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄 Presenter 层
 */
public class AccountBooksPresenter implements AccountBooksContract.Presenter {

    private final AccountRepository mRepository;
    private final AccountBooksContract.View mView;

    public AccountBooksPresenter(AccountRepository repository, AccountBooksContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

}

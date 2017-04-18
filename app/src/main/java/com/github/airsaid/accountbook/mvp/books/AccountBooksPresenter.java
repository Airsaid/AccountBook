package com.github.airsaid.accountbook.mvp.books;

import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.AccountDataSource;
import com.github.airsaid.accountbook.data.source.AccountRepository;

import java.util.List;

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

    @Override
    public void queryBooks(User user) {
        mRepository.queryBooks(user, new AccountDataSource.QueryBooksCallback() {
            @Override
            public void querySuccess(List<AccountBook> list) {
                mView.queryBooksSuccess(list);
            }

            @Override
            public void queryFail(Error e) {
                mView.queryBooksFail(e);
            }
        });
    }
}

package com.github.airsaid.accountbook.mvp.books;

import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;
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

    @Override
    public void setCurrentBook(User user, long bid) {
        mRepository.setCurrentBook(user, bid, new Callback() {
            @Override
            public void requestSuccess() {
                mView.setCurrentBookSuccess();
            }

            @Override
            public void requestFail(Error e) {
                mView.setCurrentBookFail(e);
            }
        });
    }

    @Override
    public void addShareBook(User user, long bid) {
        mRepository.addShareBook(user, bid, new Callback() {
            @Override
            public void requestSuccess() {
                mView.addShareBookSuccess();
            }

            @Override
            public void requestFail(Error e) {
                mView.addShareBookFail(e);
            }
        });
    }

    @Override
    public void exitBook(User user, AccountBook book) {
        mRepository.exitBook(user, book, new Callback() {
            @Override
            public void requestSuccess() {
                mView.exitBookSuccess();
            }

            @Override
            public void requestFail(Error e) {
                mView.exitBookFail(e);
            }
        });
    }

    @Override
    public void deleteBook(long bid) {
        mRepository.deleteBook(bid, new Callback() {
            @Override
            public void requestSuccess() {
                mView.deleteBookSuccess();
            }

            @Override
            public void requestFail(Error e) {
                mView.deleteBookFail(e);
            }
        });
    }


}

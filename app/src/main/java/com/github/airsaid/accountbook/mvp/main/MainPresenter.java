package com.github.airsaid.accountbook.mvp.main;

import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.AccountDataSource;
import com.github.airsaid.accountbook.data.source.AccountRepository;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc
 */
public class MainPresenter implements MainContract.Presenter {

    private final AccountRepository mRepository;
    private final MainContract.View mView;

    public MainPresenter(AccountRepository repository, MainContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void queryAccount(User user, String startDate, String endDate, int page) {
        mRepository.queryAccounts(user, startDate, endDate, page, new AccountDataSource.QueryAccountListCallback() {
            @Override
            public void querySuccess(List<Account> list) {
                mView.querySuccess(list);
            }

            @Override
            public void queryFail(Error e) {
                mView.queryFail(e);
            }
        });
    }
}

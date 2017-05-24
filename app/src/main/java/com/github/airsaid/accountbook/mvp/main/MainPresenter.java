package com.github.airsaid.accountbook.mvp.main;

import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;
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
        mRepository.queryDefBookAccounts(user, startDate, endDate, -1, page, false, new AccountDataSource.QueryAccountListCallback() {
            @Override
            public void querySuccess(List<Account> list) {
                mView.querySuccess(list);
            }

            @Override
            public void shareUsers(int count) {
                mView.shareUsers(count);
            }

            @Override
            public void queryFail(Error e) {
                mView.queryFail(e);
            }
        });
    }

    @Override
    public void queryAccountTotalMoney(User user, String startDate, String endDate) {
        mRepository.queryDefBookTotalMoney(user, startDate, endDate, new AccountDataSource.QueryBookTotalMoneyCallback() {
            @Override
            public void querySuccess(double totalCost, double totalIncome) {
                mView.queryTotalMoneySuccess(totalCost, totalIncome);
            }

            @Override
            public void queryFail(Error e) {
                mView.queryTotalMoneyFail(e);
            }
        });
    }

    @Override
    public void deleteAccount(Account account) {
        mRepository.deleteAccount(account, new Callback() {
            @Override
            public void requestSuccess() {
                mView.deleteSuccess();
            }

            @Override
            public void requestFail(Error e) {
                mView.deleteFail(e);
            }
        });
    }
}

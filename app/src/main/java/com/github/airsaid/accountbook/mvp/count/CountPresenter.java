package com.github.airsaid.accountbook.mvp.count;

import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.AccountDataSource;
import com.github.airsaid.accountbook.data.source.AccountRepository;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/12
 * @desc 统计 Presenter 层
 */
public class CountPresenter implements CountContract.Presenter {

    private final AccountRepository mRepository;
    private final CountContract.View mView;

    public CountPresenter(AccountRepository repository, CountContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void queryAccounts(User user, String startDate, String endDate, int queryType, int type) {
        mRepository.queryCountAccounts(user, startDate, endDate, queryType, type, new AccountDataSource.QueryAccountsCallback() {
            @Override
            public void querySuccess(List<Account> list) {
                mView.queryAccountsSuccess(list);
            }

            @Override
            public void queryFail(Error e) {
                mView.queryAccountsFail(e);
            }
        });
    }
}

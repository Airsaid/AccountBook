package com.github.airsaid.accountbook.mvp.count;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/12
 * @desc 统计 契约类
 */
public interface CountContract {

    interface View extends BaseView<Presenter>{
        void queryAccounts(String startDate, String endDate, int queryType, int type);
        void queryAccountsSuccess(List<Account> accounts);
        void queryAccountsFail(Error e);
        void setChartData();
        double getTotalMoney();
        void setQueryType(int type);
    }

    interface Presenter extends BasePresenter{
        void queryAccounts(User user, String startDate, String endDate, int queryType, int type);
    }

}

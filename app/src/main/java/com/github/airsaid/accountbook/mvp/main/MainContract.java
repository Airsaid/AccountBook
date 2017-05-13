package com.github.airsaid.accountbook.mvp.main;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc 首页契约类，用于统一约定接口。
 */
public interface MainContract {

    interface View extends BaseView<Presenter>{
        void querySuccess(List<Account> list);
        void shareUsers(int count);
        void queryFail(Error e);
        void queryTotalMoneySuccess(double totalCost, double totalIncome);
        void queryTotalMoneyFail(Error e);
        void deleteSuccess();
        void deleteFail(Error e);
        void showOperateAccountDialog(Account account);
        void showDeleteAccountDialog(Account account);
    }

    interface Presenter extends BasePresenter{
        void queryAccount(User user, String startDate, String endDate, int page);
        void queryAccountTotalMoney(User user, String startDate, String endDate);
        void deleteAccount(Account account);
    }

}

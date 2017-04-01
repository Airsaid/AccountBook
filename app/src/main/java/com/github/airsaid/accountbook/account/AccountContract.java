package com.github.airsaid.accountbook.account;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc 记账契约类，用于统一约定接口。
 */
public class AccountContract {

    interface View extends BaseView<Presenter>{
        void save();
        void selectCost();
        void selectIncome();
        void showSelectDate();
    }

    interface Presenter extends BasePresenter{
    }

}

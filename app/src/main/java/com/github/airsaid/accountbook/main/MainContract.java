package com.github.airsaid.accountbook.main;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc 首页契约类，用于统一约定接口。
 */
public class MainContract {

    interface View extends BaseView<Presenter>{
        void querySuccess(List<Account> list);
        void queryFail(Error e);
    }

    interface Presenter extends BasePresenter{
        void queryAccount(String startDate, String endDate, int page);
    }

}

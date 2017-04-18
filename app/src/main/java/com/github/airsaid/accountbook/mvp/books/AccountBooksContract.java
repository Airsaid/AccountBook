package com.github.airsaid.accountbook.mvp.books;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄契约类
 */
public interface AccountBooksContract {

    interface View extends BaseView<Presenter>{
        void queryBooksSuccess(List<AccountBook> books);
        void queryBooksFail(Error e);
    }

    interface Presenter extends BasePresenter{
        void queryBooks(User user);
    }

}

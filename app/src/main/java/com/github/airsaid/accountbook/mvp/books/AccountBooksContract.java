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
        void setCurrentBookSuccess();
        void setCurrentBookFail(Error e);
        void addShareBook();
        void addShareBookSuccess();
        void addShareBookFail(Error e);
        void showOperateBookDialog(AccountBook book);
        void showExitBookDialog(AccountBook book);
        void showDeleteBookDialog(long bid);
        void exitBookSuccess();
        void exitBookFail(Error e);
        void deleteBookSuccess();
        void deleteBookFail(Error e);
    }

    interface Presenter extends BasePresenter{
        void queryBooks(User user);
        void setCurrentBook(User user, long bid);
        void addShareBook(User user, long bid);
        void exitBook(User user, AccountBook book);
        void deleteBook(long bid);
    }

}

package com.github.airsaid.accountbook.data.source;

import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc
 */
public interface AccountDataSource {

    /** 保存账目信息 */
    void saveAccount(User user, Account account, Callback callback);

    /** 查找指定日期内的账目信息 */
    void queryAccounts(User user, String startDate, String endDate, int page, QueryAccountListCallback callback);

    /** 查询默认帐薄 */
    void queryDefaultBook(User user, QueryDefaultBookCallback callback);

    /** 创建默认帐薄 */
    void createDefaultBook(User user, Callback callback);

    /** 查询指定用户下所有的帐薄 */
    void queryBooks(User user, QueryBooksCallback callback);

    /** 根据帐薄 ID 加入共享帐薄*/
    void addShareBook(User user, long bid, Callback callback);

    /** 添加一条帐薄数据 */
    void addBook(AccountBook book, Callback callback);

    interface QueryAccountListCallback{
        void querySuccess(List<Account> list);
        void queryFail(Error e);
    }

    interface QueryDefaultBookCallback{
        void querySuccess(AccountBook book);
        void queryFail(Error e);
    }

    interface QueryBooksCallback{
        void querySuccess(List<AccountBook> list);
        void queryFail(Error e);
    }

}

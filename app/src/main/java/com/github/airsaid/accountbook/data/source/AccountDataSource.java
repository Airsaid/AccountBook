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

    /** 删除一条账目数据 */
    void deleteAccount(Account account, Callback callback);

    /**
     * 查找指定用户指定日期内所有的账目信息
     * @param user       用户
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @param type       类型 1、支出 2、收入 （-1 不分类型查找）
     * @param page       页码 （-1 为不分页）
     * @param callback   回调
     */
    void queryAccounts(User user, String startDate, String endDate, int type, int page, QueryAccountsCallback callback);

    /**
     * 查找默认帐薄里指定日期内的账目信息
     * @param user      用户
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param type      类型 1、支出 2、收入 （-1 不分类型查找）
     * @param page      页码 （-1 为不分页）
     * @param isQueryMe 是否只查询指定用户账目信息
     * @param callback  回调
     */
    void queryDefBookAccounts(User user, String startDate, String endDate, int type, int page, boolean isQueryMe,  QueryAccountListCallback callback);

    /**
     * 按条件查询默认帐薄内信息
     * @param user      用户
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param queryType 查询类型 1、查询指定用户所有账目 2、查询指定用户当前账目所有账目 3、查询指定用户当前帐薄中指定用户账目
     * @param type      类型 1、支出 2、收入 （-1 不分类型查找）
     * @param callback  回调
     */
    void queryCountAccounts(User user, String startDate, String endDate, int queryType, int type, QueryAccountsCallback callback);

    /** 查找指定日期内默认帐薄内账目总收入、支出 */
    void queryDefBookTotalMoney(User user, String startDate, String endDate, QueryBookTotalMoneyCallback callback);

    /** 查询默认帐薄 */
    void queryDefaultBook(User user, QueryDefaultBookCallback callback);

    /** 创建默认帐薄 */
    void createDefaultBook(User user, CreateBookCallback callback);

    /** 查询指定用户下所有的帐薄 */
    void queryBooks(User user, QueryBooksCallback callback);

    /** 根据帐薄 ID 加入共享帐薄*/
    void addShareBook(User user, long bid, Callback callback);

    /** 添加一条帐薄数据 */
    void addBook(AccountBook book, Callback callback);

    /** 设置当前帐薄 */
    void setCurrentBook(User user, long bid, Callback callback);

    /** 修改一条帐薄数据 */
    void editBook(AccountBook book, Callback callback);

    /** 删除指定帐薄 ID 对应的所有账簿数据 */
    void deleteBook(long bid, Callback callback);

    /** 退出帐薄 */
    void exitBook(User user, AccountBook book, Callback callback);

    /** 查询帐薄 id 对应帐薄下所有账目的总支出、收入 */
    void queryBookTotalMoney(long bid, QueryBookTotalMoneyCallback callback);

    interface CreateBookCallback{
        void createSuccess(AccountBook book);
        void createFail(Error e);
    }

    interface QueryAccountListCallback{
        void querySuccess(List<Account> list);
        void shareUsers(int count);
        void queryFail(Error e);
    }

    interface QueryAccountsCallback{
        void querySuccess(List<Account> list);
        void queryFail(Error e);
    }

    interface QueryBookTotalMoneyCallback{
        void querySuccess(double totalCost, double totalIncome);
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

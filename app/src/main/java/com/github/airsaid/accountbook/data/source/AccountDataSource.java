package com.github.airsaid.accountbook.data.source;

import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
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
    void saveAccount(Account account, Callback callback);

    /** 查找指定日期内的账目信息 */
    void queryAccount(String startDate, String endDate, int page, QueryAccountListCallback callback);

    interface QueryAccountListCallback{
        void querySuccess(List<Account> list);
        void queryFail(Error e);
    }
}

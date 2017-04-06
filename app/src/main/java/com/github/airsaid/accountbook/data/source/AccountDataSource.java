package com.github.airsaid.accountbook.data.source;

import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.i.Callback;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc
 */
public interface AccountDataSource {

    /** 保存账目信息 */
    void saveAccount(Account account, Callback callback);
}

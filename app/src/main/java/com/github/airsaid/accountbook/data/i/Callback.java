package com.github.airsaid.accountbook.data.i;

import com.github.airsaid.accountbook.data.Error;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc 网络请求公共回调接口。
 */
public interface Callback {
    /** 请求成功 */
    void requestSuccess();
    /** 请求失败 */
    void requestFail(Error e);
}

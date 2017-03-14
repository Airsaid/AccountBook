package com.github.airsaid.accountbook.util;

import com.avos.avoscloud.AVUser;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/3/14
 * @desc 用户操作相关封装类.
 */
public class UserUtils {

    private UserUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 校验用户是否已经登录
     * @return true: 已登录 false: 未登录
     */
    public static boolean checkLogin(){
        return null != AVUser.getCurrentUser();
    }

}

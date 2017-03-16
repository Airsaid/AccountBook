package com.github.airsaid.accountbook.data;

import com.avos.avoscloud.AVUser;

/**
 * @author Airsaid
 * @Date 2017/2/20 22:49
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 用户信息 Bean
 */
public class User {

    public String phone;
    public String password;
    public AVUser user;

    public User(){}

    public User(AVUser user) {
        this.user = user;
    }
}

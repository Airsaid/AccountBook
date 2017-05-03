package com.github.airsaid.accountbook.util;

import com.avos.avoscloud.AVUser;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.User;

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
     * 校验用户是否已经登录。
     * @return true: 已登录 false: 未登录
     */
    public static boolean checkLogin(){
        return null != AVUser.getCurrentUser(User.class);
    }

    /**
     * 获取当前登录 User 对象。
     * @return User 对象
     */
    public static User getUser(){
        return AVUser.getCurrentUser(User.class);
    }

    /**
     * 获取当前登录用户 id。
     * @return String 用户 id
     */
    public static String getUid(){
        User user = AVUser.getCurrentUser(User.class);
        return user != null ? user.getObjectId() : "";
    }

    /**
     * 根据类型获取性别字符串。
     * @param sex 性别类型，0：未设置、1：男、2：女。
     * @return
     */
    public static String getSexText(int sex){
        if(sex == 1){
            return UiUtils.getString(R.string.man);
        }else if(sex == 2){
            return UiUtils.getString(R.string.woman);
        }else{
            return UiUtils.getString(R.string.not_set);
        }
    }

}

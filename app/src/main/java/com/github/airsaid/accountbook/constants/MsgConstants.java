package com.github.airsaid.accountbook.constants;

/**
 * Created by zhouyou on 2016/6/29.
 * Class desc:
 *
 * 该类用于定义Eventbus的消息常量。
 */
public class MsgConstants {

    public static final int CUSTOM_MSG_BASE = 1;

    /** 账目保存成功 */
    public static final int MSG_SAVE_ACCOUNT_SUCCESS = CUSTOM_MSG_BASE << 1;

    /** 设置当前帐薄成功 */
    public static final int MSG_SET_CUR_BOOK_SUCCESS = CUSTOM_MSG_BASE << 2;


}

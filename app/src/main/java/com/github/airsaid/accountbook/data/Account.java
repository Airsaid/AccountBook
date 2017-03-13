package com.github.airsaid.accountbook.data;

import java.util.Date;

/**
 * @author Airsaid
 * @Date 2017/2/27 21:58
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class Account {
    // 账目类型 支出：1 收入：2
    public int type;
    // 消费金额
    public String money;
    // 消费类型
    public String ctype;
    // 时间
    public Date date;
    // 备注
    public String note;
}

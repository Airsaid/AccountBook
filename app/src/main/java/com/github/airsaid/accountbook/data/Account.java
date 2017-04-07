package com.github.airsaid.accountbook.data;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.util.UiUtils;

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
    public String cType;
    // 时间
    public Date date;
    // 备注
    public String note;

    public Account(){

    }

    public Account(int type, String money, String cType, Date date, String note) {
        this.type = type;
        this.money = money;
        this.cType = cType;
        this.date = date;
        this.note = note;
    }

    public String getMoneyStr(){
        return UiUtils.getString(R.string.rmb).concat(" ").concat(money);
    }

    @Override
    public String toString() {
        return "Account{" +
                "type=" + type +
                ", money='" + money + '\'' +
                ", cType='" + cType + '\'' +
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
    }
}

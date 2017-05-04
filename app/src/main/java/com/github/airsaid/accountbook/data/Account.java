package com.github.airsaid.accountbook.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.Api;
import com.github.airsaid.accountbook.util.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Airsaid
 * @Date 2017/2/27 21:58
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 账目类
 */
@AVClassName(Api.TAB_ACCOUNT)
public class Account extends AVObject implements MultiItemEntity {

    public static final Parcelable.Creator CREATOR = AVObject.AVObjectCreator.instance;

    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_DATE    = 2;
    public int itemType;

    public Account(){
        super();
    }

    public Account(Parcel in){
        super(in);
    }

    /** 设置记账人 */
    public void setOwner(User user){
        put(Api.OWNER, user);
    }

    /** 获取记账人 */
    public User getOwner(){
        return getAVUser(Api.OWNER, User.class);
    }

    /** 设置账目类型 支出：1 收入：2 */
    public void setType(int type){
        put(Api.TYPE, type);
    }

    /** 获取账目类型 */
    public int getType(){
        return getInt(Api.TYPE);
    }

    /** 设置支出、收入金额 */
    public void setMoney(String money){
        put(Api.MONEY, money);
    }

    /** 获取支出、收入金额 */
    public String getMoney(){
        return getString(Api.MONEY);
    }

    /** 设置支出、收入类型 */
    public void setCType(String cType){
        put(Api.CTYPE, cType);
    }

    /** 获取支出、收入类型 */
    public String getCType(){
        return getString(Api.CTYPE);
    }

    /** 设置支出、收入类型图标 */
    public void setTypeIcon(String typeIcon){
        put(Api.TYPE_ICON, typeIcon);
    }

    /** 获取支出、收入类型图标 */
    public String getTypeIcon(){
        return getString(Api.TYPE_ICON);
    }

    /** 设置支出、收入日期 */
    public void setDate(Date date){
        put(Api.DATE, date);
    }

    /** 获取支出、收入日期 */
    public Date getDate(){
        return getDate(Api.DATE);
    }

    /** 获取格式化后的支出、收入日期 */
    public String getDateFormat(String format){
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.CHINA);
        return f.format(getDate());
    }

    /** 设置备注 */
    public void setNote(String note){
        put(Api.NOTE, note);
    }

    /** 获取备注 */
    public String getNote(){
        return getString(Api.NOTE);
    }

    /** 设置帐薄 id */
    public void setBid(long bid){
        put(Api.BID, bid);
    }

    /** 获取帐薄 id */
    public long getBid(){
        return getLong(Api.BID);
    }

    /**
     * 获取金额字符
     */
    public String getMoneyStr(){
        return UiUtils.getString(R.string.rmb).concat(" ").concat(getMoney());
    }

    /** 获取条目类型 */
    @Override
    public int getItemType() {
        return itemType;
    }

    /** 设置条目类型 */
    public void setItemType(int type){
        this.itemType = type;
    }
}

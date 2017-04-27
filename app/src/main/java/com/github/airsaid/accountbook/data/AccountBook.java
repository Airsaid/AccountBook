package com.github.airsaid.accountbook.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.Api;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄类
 */
@AVClassName(Api.TAB_BOOK)
public class AccountBook extends AVObject {

    public static final Parcelable.Creator CREATOR = AVObject.AVObjectCreator.instance;

    public AccountBook(){
        super();
    }

    public AccountBook(Parcel in){
        super(in);
    }

    /** 该帐薄下所有账目总支出 */
    public double totalCost = 0.00;
    /** 该帐薄下所有账目总收入 */
    public double totalIncome = 0.00;

    /** 设置帐薄 id */
    public void setBid(long bid){
        put(Api.BID, bid);
    }

    /** 获取帐薄 id */
    public long getBid(){
        return getLong(Api.BID);
    }

    /** 设置帐薄所属用户 */
    public void setOwner(User user){
        put(Api.OWNER, user);
    }

    /** 获取帐薄所属用户 */
    public User getOwner(){
        return getAVUser(Api.OWNER, User.class);
    }

    /** 设置帐薄名称 */
    public void setName(String name){
        put(Api.NAME, name);
    }

    /** 获取帐薄名称 */
    public String getName(){
        return getString(Api.NAME);
    }

    /** 设置帐薄场景 */
    public void setScene(String scene){
        put(Api.SCENE, scene);
    }

    /** 获取帐薄场景 */
    public String getScene(){
        return getString(Api.SCENE);
    }

    /** 设置封面图名称 */
    public void setCover(String cover){
        put(Api.COVER, cover);
    }

    /** 获取封面图名称 */
    public String getCover(){
        return getString(Api.COVER);
    }

    /** 获取是否是当前帐薄 */
    public boolean isCurrent(){
        return getBoolean(Api.IS_CURRENT);
    }

    /** 设置是否是当前帐薄 */
    public void setCurrent(boolean isCur){
        put(Api.IS_CURRENT, isCur);
    }

    /** 添加共享用户 */
    public void addShare(User user){
        add(Api.SHARES, user);
    }

    /** 设置共享用户 */
    public void setShare(List<User> users){
        put(Api.SHARES, users);
    }

    /** 获取所有共享用户 */
    public List<User> getShares(){
        return getList(Api.SHARES, User.class);
    }

    /** 获取帐薄下所有账目总支出 */
    public String getTotalCost(){
        return UiUtils.getString(R.string.total_cost_sign).concat(String.valueOf(totalCost));
    }

    /** 获取帐薄下所有账目总收入 */
    public String getTotalIncome(){
        return UiUtils.getString(R.string.total_income_sign).concat(String.valueOf(totalIncome));
    }
}

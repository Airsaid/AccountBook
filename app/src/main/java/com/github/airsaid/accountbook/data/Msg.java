package com.github.airsaid.accountbook.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.github.airsaid.accountbook.constants.Api;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/25
 * @desc 消息
 */
@AVClassName(Api.TAB_MSG)
public class Msg extends AVObject{

    public static final Parcelable.Creator CREATOR = AVObject.AVObjectCreator.instance;

    public Msg(){
        super();
    }

    public Msg(Parcel in){
        super(in);
    }

    /** 获取消息 id */
    public long getId(){
        return getLong(Api.ID);
    }

    /** 设置消息所属用户 */
    public void setOwner(User user){
        put(Api.OWNER, user);
    }

    /** 获取消息所属用户 */
    public User getOwner(){
        return getAVUser(Api.OWNER, User.class);
    }

    /** 设置消息类型 */
    public void setType(int type){
        put(Api.TYPE, type);
    }

    /** 获取消息类型 1,申请加入帐薄消息 2,系统提示消息 */
    public int getType(){
        return getInt(Api.TYPE);
    }

    /** 设置提示内容 */
    public void setContent(String content){
        put(Api.CONTENT, content);
    }

    /** 获取提示内容 */
    public String getContent(){
        return getString(Api.CONTENT);
    }

    /** 设置申请用户 */
    public void setApplyUser(User user){
        put(Api.APPLY_USER, user);
    }

    /** 获取申请用户 */
    public User getApplyUser(){
        return getAVUser(Api.APPLY_USER, User.class);
    }

    /** 设置申请的帐薄 */
    public void setApplyBook(AccountBook book){
        put(Api.APPLY_BOOK, book);
    }

    /** 获取申请的帐薄 */
    public AccountBook getApplyBook(){
        try {
            return getAVObject(Api.APPLY_BOOK, AccountBook.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 设置消息是否读取 */
    public void setRead(boolean isRead){
        put(Api.IS_READ, isRead);
    }

    /** 获取消息是否读取 */
    public boolean isRead(){
        return getBoolean(Api.IS_READ);
    }

}

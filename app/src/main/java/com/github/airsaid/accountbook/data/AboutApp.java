package com.github.airsaid.accountbook.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.github.airsaid.accountbook.constants.Api;

/**
 * @author Airsaid
 * @Date 2017/4/24 21:45
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
@AVClassName(Api.TAB_ABOUT)
public class AboutApp extends AVObject {

    public static final Parcelable.Creator CREATOR = AVObject.AVObjectCreator.instance;

    public AboutApp(){
        super();
    }

    public AboutApp(Parcel in){
        super(in);
    }

    /** 获取内容 */
    public String getContent(){
        return getString(Api.CONTENT);
    }

}

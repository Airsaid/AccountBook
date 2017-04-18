package com.github.airsaid.accountbook.data;

import android.os.Parcel;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.github.airsaid.accountbook.constants.Api;

/**
 * @author Airsaid
 * @Date 2017/2/20 22:49
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 用户信息 Bean
 */
public class User extends AVUser{

    public String phone;
    public String password;

    /** 年龄 */
    private int age = 0;
    /** 性别，0：未设置、1：男 、2：女 */
    private int sex = 0;

    public User(){
        super();
    }

    public User(Parcel in){
        super(in);
    }

    public static final Creator CREATOR = AVObjectCreator.instance;

    public void setAge(int age){
        put(Api.AGE, age);
        this.age = age;
    }

    public int getAge(){
        age = getInt(Api.AGE);
        return age;
    }

    public void setSex(int sex){
        put(Api.SEX, sex);
        this.sex = sex;
    }

    public int getSex(){
        sex = getInt(Api.SEX);
        return sex;
    }

    public void setAvatar(AVFile avatar){
        put(Api.AVATAR, avatar);
    }

    public AVFile getAvatar(){
        return getAVFile(Api.AVATAR);
    }
}

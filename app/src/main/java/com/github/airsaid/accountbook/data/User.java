package com.github.airsaid.accountbook.data;

import android.os.Parcel;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.github.airsaid.accountbook.constants.ApiConstant;

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
        put(ApiConstant.AGE, age);
        this.age = age;
    }

    public int getAge(){
        age = getInt(ApiConstant.AGE);
        return age;
    }

    public void setSex(int sex){
        put(ApiConstant.SEX, sex);
        this.sex = sex;
    }

    public int getSex(){
        sex = getInt(ApiConstant.SEX);
        return sex;
    }

    public void setAvatar(AVFile avator){
        put(ApiConstant.AVATAR, avator);
//            AVFile avator = AVFile.withAbsoluteLocalPath(file.getName(), file.getPath());
    }

    public AVFile getAvatar(){
        return getAVFile(ApiConstant.AVATAR);
    }
}

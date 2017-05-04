package com.github.airsaid.accountbook.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/3
 * @desc 支出、收入分类。
 */
@Entity
public class Type implements Parcelable {

    @Id
    /** 分类 id */
    private Long id;
    /** 用户 id */
    private String uid;
    /** 分类类型（1，支出 2，收入） */
    private int type;
    /** 位置索引 */
    private int index;
    /** 分类名称 */
    private String name;
    /** 分类图片名称 */
    private String icon;

    protected Type(Parcel in) {
        id = in.readLong();
        uid = in.readString();
        type = in.readInt();
        index = in.readInt();
        name = in.readString();
        icon = in.readString();
    }
    @Generated(hash = 623657528)
    public Type(Long id, String uid, int type, int index, String name,
            String icon) {
        this.id = id;
        this.uid = uid;
        this.type = type;
        this.index = index;
        this.name = name;
        this.icon = icon;
    }
    @Generated(hash = 1782799822)
    public Type() {
    }

    public static final Creator<Type> CREATOR = new Creator<Type>() {
        @Override
        public Type createFromParcel(Parcel in) {
            return new Type(in);
        }

        @Override
        public Type[] newArray(int size) {
            return new Type[size];
        }
    };

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(uid);
        parcel.writeInt(type);
        parcel.writeInt(index);
        parcel.writeString(name);
        parcel.writeString(icon);
    }
}

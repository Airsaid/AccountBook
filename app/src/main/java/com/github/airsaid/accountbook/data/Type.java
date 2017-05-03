package com.github.airsaid.accountbook.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/3
 * @desc 支出、收入分类。
 */
@Entity
public class Type {

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
    private String image;

    @Generated(hash = 560857445)
    public Type(Long id, String uid, int type, int index, String name,
            String image) {
        this.id = id;
        this.uid = uid;
        this.type = type;
        this.index = index;
        this.name = name;
        this.image = image;
    }
    @Generated(hash = 1782799822)
    public Type() {
    }
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
    public String getImage() {
        return this.image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}

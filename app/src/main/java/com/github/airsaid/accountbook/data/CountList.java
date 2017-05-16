package com.github.airsaid.accountbook.data;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/16
 * @desc 统计类
 */
public class CountList {

    /** 分类图标名字 */
    private String iconName;
    /** 分类名称 */
    private String typeName;
    /** 总金额 */
    private double totalMoney;
    /** 百分比 */
    private float percent;

    public String getIconName() {
        return iconName;
    }

    public CountList setIconName(String iconName) {
        this.iconName = iconName;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public CountList setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public CountList setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
        return this;
    }

    public float getPercent() {
        return percent;
    }

    public CountList setPercent(float percent) {
        this.percent = percent;
        return this;
    }
}

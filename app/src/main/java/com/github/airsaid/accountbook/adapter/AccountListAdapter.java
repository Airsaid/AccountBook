package com.github.airsaid.accountbook.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.util.ArithUtils;
import com.github.airsaid.accountbook.util.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/7
 * @desc 账目列表 Adapter
 */
public class AccountListAdapter extends BaseMultiItemQuickAdapter<Account, BaseViewHolder> {

    private final AbsoluteSizeSpan mSizeMinSpan;
    private List<Account> data;

    public AccountListAdapter(List<Account> data) {
        super(data);
        mSizeMinSpan = new AbsoluteSizeSpan(14, true);
        addItemType(Account.TYPE_DEFAULT, R.layout.item_account_list);
        addItemType(Account.TYPE_DATE, R.layout.item_account_list_date);
    }

    @Override
    protected void convert(BaseViewHolder helper, Account item) {
        SpannableString moneySpan = new SpannableString(item.getMoneyStr());
        moneySpan.setSpan(mSizeMinSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        // 设置类型
        helper.setImageResource(R.id.img_type, AppConfig.TYPE_COST == item.type ?
                R.mipmap.ic_type_cost : R.mipmap.ic_type_income)
                // 设置金额
                .setText(R.id.txt_money, moneySpan)
                // 设置分类
                .setVisible(R.id.txt_type, !TextUtils.isEmpty(item.cType))
                .setText(R.id.txt_type, item.cType)
                // 设置备注
                .setVisible(R.id.txt_note, !TextUtils.isEmpty(item.note))
                .setText(R.id.txt_note, item.note)
                // 设置日期
                .setText(R.id.txt_date, DateUtils.getDateText(item.date, DateUtils.FORMAT));

        if(item.getItemType() == Account.TYPE_DATE){
            // 设置当天日期
            helper.setText(R.id.txt_day_date,
                    DateUtils.getWeekDate(item.date, DateUtils.FORMAT_MONTH_DAY));
            // 设置当天总收入、支出
            helper.setText(R.id.txt_day_money, getDayMoney(data, item.date));
        }
    }

    @Override
    public void setNewData(List<Account> data) {
        super.setNewData(data);
        this.data = data;
    }

    /**
     * 获取当天的收入、支出金额。
     */
    private String getDayMoney(List<Account> list, Date date){
        double totalCostMoney = 0;
        double totalIncomeMoney = 0;
        for (Account account : list) {
            // 判断如果是指定天
            Calendar c = Calendar.getInstance(Locale.CHINA);
            c.setTime(account.date);
            int day = c.get(Calendar.DAY_OF_MONTH);
            c.setTime(date);
            int day2 = c.get(Calendar.DAY_OF_MONTH);
            if(day == day2){
                if(AppConfig.TYPE_COST == account.type){
                    totalCostMoney = ArithUtils.add(totalCostMoney, Double.parseDouble(account.money));
                }else{
                    totalIncomeMoney = ArithUtils.add(totalIncomeMoney, Double.parseDouble(account.money));
                }
            }
        }
        String dayTotalCostStr = totalCostMoney <= 0 ? "" : "支出: " + totalCostMoney;
        String dayTotalIncomeStr = totalIncomeMoney <= 0 ? "" : "     收入: " + totalIncomeMoney;
        return dayTotalCostStr.concat(dayTotalIncomeStr);
    }

    /**
     * 设置条目类型，根据是否是同一天来区别展示是否带时间条目。
     */
    public List<Account> setItemType(List<Account> list){
        String preDay = "";
        for (Account account : list) {
            // 获取天
            String day = DateUtils.getDateText(account.date, DateUtils.FORMAT_DAY);
            // 根据是否是同一天设置条目类型
            account.setItemType(day.equals(preDay) ? Account.TYPE_DEFAULT : Account.TYPE_DATE);
            // 赋值给上一天
            preDay = day;
        }
        return list;
    }

    /**
     * 设置当月总支出、收入数据。
     */
    public void setTotalData(TextView totalCost, TextView totalIncome) {
        double costTotalMoney = 0;
        double incomeTotalMoney = 0;
        for (Account account : data) {
            double money = Double.parseDouble(account.money);
            if(AppConfig.TYPE_COST == account.type){
                costTotalMoney = ArithUtils.add(costTotalMoney, money);
            }else{
                incomeTotalMoney = ArithUtils.add(incomeTotalMoney, money);
            }
        }
        totalCost.setText(String.valueOf(costTotalMoney));
        totalIncome.setText(String.valueOf(incomeTotalMoney));
    }
}

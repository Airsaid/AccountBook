package com.github.airsaid.accountbook.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.util.ArithUtils;
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.ImageLoader;

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
    private boolean isShowAvatar = false; // 是否显示头像

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
        helper.setImageResource(R.id.img_type, AppConfig.TYPE_COST == item.getType() ?
                R.mipmap.ic_type_cost : R.mipmap.ic_type_income)
                // 设置金额
                .setText(R.id.txt_money, moneySpan)
                // 设置分类
                .setVisible(R.id.txt_type, !TextUtils.isEmpty(item.getCType()))
                .setText(R.id.txt_type, item.getCType())
                // 设置日期
                .setText(R.id.txt_date, DateUtils.getDateText(item.getDate(), DateUtils.FORMAT));

        // 设置备注
        TextView txtNote = helper.getView(R.id.txt_note);
        String note = item.getNote();
        if(TextUtils.isEmpty(note)){
            txtNote.setVisibility(View.INVISIBLE);
        }else{
            txtNote.setVisibility(View.VISIBLE);
            txtNote.setText(note);
        }

        // 设置记账用户信息
        ImageView imgAvatar = helper.getView(R.id.img_avatar);
        User owner = item.getOwner();
        if(owner != null && isShowAvatar){
            // 设置头像
            imgAvatar.setVisibility(View.VISIBLE);
            AVFile avatar = owner.getAvatar();
            if(avatar != null){
                ImageLoader.getIns(mContext).loadIcon(avatar.getUrl(), imgAvatar);
            }else{
                imgAvatar.setImageResource(R.mipmap.ic_def_icon);
            }
        }else{
            imgAvatar.setVisibility(View.GONE);
        }

        if(item.getItemType() == Account.TYPE_DATE){
            // 设置当天日期
            helper.setText(R.id.txt_day_date,
                    DateUtils.getWeekDate(item.getDate(), DateUtils.FORMAT_MONTH_DAY));
            // 设置当天总收入、支出
            helper.setText(R.id.txt_day_money, getDayMoney(data, item.getDate()));
        }
    }

    @Override
    public void setNewData(List<Account> data) {
        super.setNewData(data);
        this.data = data;
    }

    /**
     * 获取当天的收入、支出金额
     */
    private String getDayMoney(List<Account> list, Date date){
        double totalCostMoney = 0;
        double totalIncomeMoney = 0;
        for (Account account : list) {
            // 判断如果是指定天
            Calendar c = Calendar.getInstance(Locale.CHINA);
            c.setTime(account.getDate());
            int day = c.get(Calendar.DAY_OF_MONTH);
            c.setTime(date);
            int day2 = c.get(Calendar.DAY_OF_MONTH);
            if(day == day2){
                if(AppConfig.TYPE_COST == account.getType()){
                    totalCostMoney = ArithUtils.add(totalCostMoney, Double.parseDouble(account.getMoney()));
                }else{
                    totalIncomeMoney = ArithUtils.add(totalIncomeMoney, Double.parseDouble(account.getMoney()));
                }
            }
        }
        String dayTotalCostStr = totalCostMoney <= 0 ? "" : "支出: " + totalCostMoney;
        String dayTotalIncomeStr = totalIncomeMoney <= 0 ? "" : "     收入: " + totalIncomeMoney;
        return dayTotalCostStr.concat(dayTotalIncomeStr);
    }

    /**
     * 设置条目类型，根据是否是同一天来区别展示是否带时间条目
     */
    public List<Account> setItemType(List<Account> list){
        String preDay = "";
        for (Account account : list) {
            // 获取天
            String day = DateUtils.getDateText(account.getDate(), DateUtils.FORMAT_DAY);
            // 根据是否是同一天设置条目类型
            account.setItemType(day.equals(preDay) ? Account.TYPE_DEFAULT : Account.TYPE_DATE);
            // 赋值给上一天
            preDay = day;
        }
        return list;
    }

    /**
     * 设置是否显示头像
     */
    public void setIsShowAvatar(boolean isShowAvatar) {
        this.isShowAvatar = isShowAvatar;
    }

}

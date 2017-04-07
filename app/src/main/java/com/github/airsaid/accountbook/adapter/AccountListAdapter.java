package com.github.airsaid.accountbook.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.util.DateUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/7
 * @desc 账目列表 Adapter
 */
public class AccountListAdapter extends BaseQuickAdapter<Account, BaseViewHolder>{

    private final AbsoluteSizeSpan mSizeMinSpan;

    public AccountListAdapter(int layoutResId, List<Account> data) {
        super(layoutResId, data);
        mSizeMinSpan = new AbsoluteSizeSpan(14, true);
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
                // 设置备注
                .setVisible(R.id.txt_note, !TextUtils.isEmpty(item.note))
                .setText(R.id.txt_note, item.note)
                // 设置日期
                .setText(R.id.txt_date, DateUtils.getDateText(item.date, DateUtils.FORMAT));
    }
}

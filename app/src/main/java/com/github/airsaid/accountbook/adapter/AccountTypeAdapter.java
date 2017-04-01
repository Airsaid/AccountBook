package com.github.airsaid.accountbook.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.AccountType;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc 记账分类 Adapter
 */
public class AccountTypeAdapter extends BaseQuickAdapter<AccountType, BaseViewHolder>{

    public AccountTypeAdapter(int layoutResId, List<AccountType> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AccountType item) {
        TextView txtType = helper.getView(R.id.txt_type);
        txtType.setText(item.type);
        UiUtils.setCompoundDrawables(txtType, null, item.drawable, null, null);
    }
}

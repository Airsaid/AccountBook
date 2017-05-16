package com.github.airsaid.accountbook.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.CountList;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/16
 * @desc 统计列表 Adapter
 */
public class CountListAdapter extends BaseQuickAdapter<CountList, BaseViewHolder>{

    public CountListAdapter(@LayoutRes int layoutResId, @Nullable List<CountList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CountList item) {
        helper.setImageResource(R.id.img_icon, UiUtils.getImageResIdByName(item.getIconName()))
                .setText(R.id.txt_type_name, item.getTypeName())
                .setText(R.id.txt_total_money, String.valueOf(item.getTotalMoney()));
    }
}

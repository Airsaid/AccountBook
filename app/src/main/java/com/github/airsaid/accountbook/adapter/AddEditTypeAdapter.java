package com.github.airsaid.accountbook.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.AddEditType;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/4
 * @desc 添加/编辑 分类 Adapter
 */
public class AddEditTypeAdapter extends BaseQuickAdapter<AddEditType, BaseViewHolder>{

    public AddEditTypeAdapter(@LayoutRes int layoutResId, @Nullable List<AddEditType> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddEditType item) {
        helper.setImageResource(R.id.img_icon, UiUtils.getImageResIdByName(item.icon))
            .setVisible(R.id.img_select, item.isSelect);
    }
}

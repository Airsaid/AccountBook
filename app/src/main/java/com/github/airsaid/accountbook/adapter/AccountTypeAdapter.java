package com.github.airsaid.accountbook.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.Type;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc 记账分类 Adapter
 */
public class AccountTypeAdapter extends BaseQuickAdapter<Type, BaseViewHolder>{

    public AccountTypeAdapter(int layoutResId, List<Type> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Type item) {
        TextView txtType = helper.getView(R.id.txt_type);
        txtType.setText(item.getName());
        Drawable image = UiUtils.getDrawable(UiUtils.getImageResIdByName(item.getIcon()));
        UiUtils.setCompoundDrawables(txtType, null, image, null, null);
    }

    @Override
    public void setNewData(@Nullable List<Type> data) {
        if(data != null && data.size() > 0 &&
                !data.get(data.size()-1).getIcon().equals("ic_cost_type_custom")){
            Type type = new Type();
            type.setName(UiUtils.getString(R.string.custom));
            type.setIcon("ic_cost_type_custom");
            type.setIndex(data.size());
            data.add(type);
        }
        super.setNewData(data);
    }
}

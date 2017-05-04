package com.github.airsaid.accountbook.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.Type;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/3
 * @desc 编辑类型 Adapter
 */
public class TypeEditAdapter extends BaseItemDraggableAdapter<Type, BaseViewHolder>{

    public TypeEditAdapter(List<Type> data) {
        super(R.layout.item_type_edit, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Type item) {
        helper.setText(R.id.txt_name, item.getName())
                .setImageResource(R.id.img_type,  UiUtils.getImageResIdByName(item.getIcon()));
    }
}

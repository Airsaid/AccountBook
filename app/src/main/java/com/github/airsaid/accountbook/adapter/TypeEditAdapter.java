package com.github.airsaid.accountbook.adapter;

import android.support.annotation.Nullable;

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

    /**
     * 根据是否是最后一条数据而禁止侧滑
     * @param data 分类数据
     */
    public void isDisableSwipe(List<Type> data){
        if(data != null && data.size() > 1){
            enableSwipeItem();
        }else{
            disableSwipeItem();
        }
    }

    @Override
    public void setNewData(@Nullable List<Type> data) {
        isDisableSwipe(data);
        super.setNewData(data);
    }

}

package com.github.airsaid.accountbook.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.AccountBooks;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄 Adapter
 */
public class AccountBooksAdapter extends BaseQuickAdapter<AccountBooks, BaseViewHolder>{

    public AccountBooksAdapter(int layoutResId, List<AccountBooks> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AccountBooks item) {
        helper.setImageResource(R.id.img_cover, item.cover)
                .setImageResource(R.id.img_scene, item.sceneImg)
                .setText(R.id.txt_name, item.name)
                .setVisible(R.id.txt_current, item.isCur);
    }
}

package com.github.airsaid.accountbook.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.BookCover;
import com.github.airsaid.accountbook.util.ImageLoader;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/20
 * @desc
 */
public class BookCoverAdapter extends BaseQuickAdapter<BookCover, BaseViewHolder>{

    public BookCoverAdapter(int layoutResId, List<BookCover> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookCover item) {
        ImageLoader.getIns(mContext).load(UiUtils.getImageResIdByName(item.cover)
                , (ImageView) helper.getView(R.id.img_cover));
        helper.setVisible(R.id.img_select, item.isSelect);
    }
}

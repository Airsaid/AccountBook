package com.github.airsaid.accountbook.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVFile;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.ImageLoader;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄 Adapter
 */
public class AccountBooksAdapter extends BaseQuickAdapter<AccountBook, BaseViewHolder>{

    public AccountBooksAdapter(int layoutResId, List<AccountBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AccountBook item) {
        int coverId = UiUtils.getImageResIdByName(item.getCover() != null
                ? item.getCover() : UiUtils.getString(R.string.def_book_cover));

        int sceneId = UiUtils.getSceneImageResIdByName(item.getScene());

        helper.setImageResource(R.id.img_cover, coverId)
                .setImageResource(R.id.img_scene, sceneId)
                .setText(R.id.txt_name, item.getName())
                .setText(R.id.txt_total_cost, item.getTotalCost())
                .setText(R.id.txt_total_income, item.getTotalIncome())
                .setVisible(R.id.txt_current, item.isCurrent());

        LinearLayout lltIcons = helper.getView(R.id.llt_icons);
        lltIcons.removeAllViews();
        for (User share : item.getShares()) {
            ImageView imgIcon = new ImageView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DimenUtils.dp2px(25), DimenUtils.dp2px(25));
            lp.leftMargin = DimenUtils.dp2px(5);
            imgIcon.setLayoutParams(lp);

            AVFile avatar = share.getAvatar();
            if(avatar != null){
                ImageLoader.getIns(mContext).loadIcon(avatar.getUrl(), imgIcon);
            }else{
                imgIcon.setImageResource(R.mipmap.ic_def_icon);
            }
            lltIcons.addView(imgIcon);
        }

        helper.addOnClickListener(R.id.img_add_user);
    }
}

package com.github.airsaid.accountbook.adapter;

import android.widget.ImageView;

import com.avos.avoscloud.AVFile;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Msg;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.util.ImageLoader;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/25
 * @desc 消息 Adapter
 */
public class MsgAdapter extends BaseQuickAdapter<Msg, BaseViewHolder>{
    
    public MsgAdapter(int layoutResId, List<Msg> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Msg item) {
        ImageView imgAvatar = helper.getView(R.id.img_avatar);

        // 判断消息类型
        if(item.getType() == AppConfig.TYPE_MSG_APPLY_BOOK){// 申请加入帐薄
            // 设置头像
            User applyUser = item.getApplyUser();
            AVFile avatar = applyUser.getAvatar();
            if(avatar != null){
                ImageLoader.getIns(mContext).loadIcon(avatar.getUrl(), imgAvatar);
            }else{
                imgAvatar.setImageResource(R.mipmap.ic_def_icon);
            }
            // 设置昵称
            helper.setText(R.id.txt_username, applyUser.getUsername());
            // 设置帐薄名
            AccountBook applyBook = item.getApplyBook();
            String applyBookSign = UiUtils.getString(R.string.apply_book_sign);
            if(applyBook != null){
                helper.setText(R.id.btn_agree, UiUtils.getString(R.string.agree))
                        .setText(R.id.txt_book, String.format(applyBookSign, applyBook.getName()));
            }else{
                helper.setText(R.id.btn_agree, UiUtils.getString(R.string.delete_msg))
                        .setText(R.id.txt_book, String.format(applyBookSign, UiUtils.getString(R.string.booK_has_delete)));
            }

            // 添加点击事件
            helper.setVisible(R.id.btn_agree, true)
                    .addOnClickListener(R.id.btn_agree);
        }else{
            imgAvatar.setImageResource(R.mipmap.ic_sys_msg);
            helper.setText(R.id.txt_username, UiUtils.getString(R.string.system_message))
                    .setText(R.id.txt_book, UiUtils.show(item.getContent()))
                    .setVisible(R.id.btn_agree, false);
        }
    }
}

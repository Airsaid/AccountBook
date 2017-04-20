package com.github.airsaid.accountbook.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/18
 * @desc 添加共享用户 (邀请好友一起记账)
 */
public class AddShareUserActivity extends BaseActivity {

    @BindView(R.id.img_cover)
    ImageView mImgCover;
    @BindView(R.id.img_scene)
    ImageView mImgScene;
    @BindView(R.id.txt_name)
    TextView mTxtName;
    @BindView(R.id.txt_bid)
    TextView mTxtBid;

    private AccountBook mBook;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_add_share_user;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_add_share_user));

        mBook = getIntent().getParcelableExtra(AppConstants.EXTRA_DATA);
        setData(mBook);
    }

    public void setData(AccountBook book) {
        int coverId = UiUtils.getImageResIdByName(book.getCover() != null
                ? book.getCover() : UiUtils.getString(R.string.def_book_cover));
        int sceneId = UiUtils.getSceneImageResIdByName(book.getScene());
        mImgCover.setImageResource(coverId);
        mImgScene.setImageResource(sceneId);
        mTxtName.setText(book.getName());
        mTxtBid.setText(UiUtils.getString(R.string.book_id_sign) + book.getBid());
    }

    @OnClick(R.id.btn_copy_bid)
    public void onClick() {
        // 复制账簿 ID
        String bid = String.valueOf(mBook.getBid());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(bid);
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_copy_success_send_friend));
    }
}

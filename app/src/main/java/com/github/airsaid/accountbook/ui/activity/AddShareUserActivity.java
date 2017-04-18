package com.github.airsaid.accountbook.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.util.UiUtils;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/18
 * @desc 添加共享用户 (邀请好友一起记账)
 */
public class AddShareUserActivity extends BaseActivity{
    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_add_share_user));


    }
}

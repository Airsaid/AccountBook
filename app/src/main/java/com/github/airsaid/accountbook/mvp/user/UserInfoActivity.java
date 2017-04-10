package com.github.airsaid.accountbook.mvp.user;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.source.UserRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.UiUtils;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/10
 * @desc 个人资料 Activity
 */
public class UserInfoActivity extends BaseActivity{

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_user_info));

        // set fragment
        UserInfoFragment fragment =
                (UserInfoFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = UserInfoFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        // create the presenter
        new UserInfoPresenter(new UserRepository(), fragment);
    }
}

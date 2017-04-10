package com.github.airsaid.accountbook.mvp.register;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.source.UserRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.UiUtils;

/**
 * @author Airsaid
 * @Date 2017/2/21 22:01
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 注册 Activity
 */
public class RegisterActivity extends BaseActivity{

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_register));

        // set fragment
        RegisterFragment fragment =
                (RegisterFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = RegisterFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        // create the presenter
        new RegisterPresenter(new UserRepository(), fragment);
    }
}

package com.github.airsaid.accountbook.mvp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.source.UserRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;

/**
 * @author Airsaid
 * @Date 2017/2/20 22:02
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 登录 Activity
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setSlideable(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = initToolbar("");
        toolbar.setNavigationIcon(null);

        // set fragment
        LoginFragment fragment =
                (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = LoginFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        // create the presenter
        new LoginPresenter(new UserRepository(), fragment);
    }


}

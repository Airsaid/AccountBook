package com.github.airsaid.accountbook.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.source.LoginRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.UiUtils;

/**
 * @author Airsaid
 * @Date 2017/2/20 22:02
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 登录 Activity
 */
public class LoginActivity extends BaseActivity {

    @Override
    public int getLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_login));

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
        new LoginPresenter(new LoginRepository(), fragment);
    }


}

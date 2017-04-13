package com.github.airsaid.accountbook.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.util.AppUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/13
 * @desc 关于 App Activity
 */
public class AboutPageActivity extends BaseActivity {

    @BindView(R.id.txt_slogan)
    TextView mTxtSlogan;
    @BindView(R.id.txt_version)
    TextView mTxtVersion;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about_page;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_about));
        setData();
    }

    private void setData() {
        mTxtSlogan.setText(UiUtils.getString(R.string.app_name));
        mTxtVersion.setText("V".concat(AppUtils.getAppVersionName()));
    }

    @OnClick(R.id.cav_developer)
    public void onClick() {
        startActivity(new Intent()
                .setAction("android.intent.action.VIEW")
                .setData(Uri.parse(UiUtils.getString(R.string.github_link))));
    }
}

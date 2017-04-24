package com.github.airsaid.accountbook.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.AboutApp;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.source.CommonDataSource;
import com.github.airsaid.accountbook.data.source.CommonRepository;
import com.github.airsaid.accountbook.util.AppUtils;
import com.github.airsaid.accountbook.util.ProgressUtils;
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
    @BindView(R.id.txt_content)
    TextView mTxtContent;

    private CommonRepository mRepository;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about_page;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_about));
        mRepository = new CommonRepository();
        setData();
    }

    private void setData() {
        mTxtSlogan.setText(UiUtils.getString(R.string.app_name));
        mTxtVersion.setText("V".concat(AppUtils.getAppVersionName()));

        ProgressUtils.show(mContext);
        mRepository.aboutApp(new CommonDataSource.GetAboutAppInfoCallback() {
            @Override
            public void getSuccess(AboutApp about) {
                ProgressUtils.dismiss();
                if(about != null){
                    mTxtContent.setText(UiUtils.show(about.getContent()));
                }
            }

            @Override
            public void getFail(Error e) {
                ProgressUtils.dismiss();
            }
        });
    }

    @OnClick(R.id.cav_developer)
    public void onClick() {
        startActivity(new Intent()
                .setAction("android.intent.action.VIEW")
                .setData(Uri.parse(UiUtils.getString(R.string.github_link))));
    }
}

package com.github.airsaid.accountbook.mvp.count;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/12
 * @desc 统计 Activity
 */
public class CountActivity extends BaseActivity {

    @BindView(R.id.txt_title_left)
    TextView mTxtTitleLeft;
    @BindView(R.id.txt_title_right)
    TextView mTxtTitleRight;
    private CountFragment mFragment;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_count;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initTitle();

        // set fragment
        mFragment = (CountFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mFragment == null) {
            // Create the fragment
            mFragment = CountFragment.newInstance(null);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment, R.id.contentFrame);
        }

        // create the presenter
        new CountPresenter(new AccountRepository(), mFragment);
    }

    private void initTitle() {
        initToolbar(null);
        mTxtTitleLeft.setText(UiUtils.getString(R.string.pie_chart));
        mTxtTitleRight.setText(UiUtils.getString(R.string.line_chart));
    }


    @OnClick({R.id.txt_title_left, R.id.txt_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_title_left:
                break;
            case R.id.txt_title_right:
                break;
        }
    }
}

package com.github.airsaid.accountbook.mvp.count;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

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
    
    private List<CountFragment> mFragments;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_count;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initTitle();
        initFragment();

        CountFragment fragment = (CountFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = mFragments.get(0);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
    }

    private void initTitle() {
        initToolbar(null);
        mTxtTitleLeft.setText(UiUtils.getString(R.string.cost));
        mTxtTitleRight.setText(UiUtils.getString(R.string.income));
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.EXTRA_TYPE, i == 0 ? AppConfig.TYPE_COST : AppConfig.TYPE_INCOME);
            CountFragment fragment = CountFragment.newInstance(bundle);
            new CountPresenter(new AccountRepository(), fragment);
            mFragments.add(fragment);
        }
    }

    @OnClick({R.id.txt_title_left, R.id.txt_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_title_left:   // 支出
                ActivityUtils.switchFragment(getSupportFragmentManager(), mFragments.get(0), R.id.contentFrame);
                mTxtTitleLeft.setBackgroundResource(R.drawable.bg_tb_select);
                mTxtTitleRight.setBackgroundResource(0);
                break;
            case R.id.txt_title_right:  // 收入
                ActivityUtils.switchFragment(getSupportFragmentManager(), mFragments.get(1), R.id.contentFrame);
                mTxtTitleRight.setBackgroundResource(R.drawable.bg_tb_select);
                mTxtTitleLeft.setBackgroundResource(0);
                break;
        }
    }
}

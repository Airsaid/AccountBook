package com.github.airsaid.accountbook.mvp.account;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.AnimUtils;

import butterknife.BindView;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;
import immortalz.me.library.bean.InfoBean;
import immortalz.me.library.method.ColorShowMethod;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc 记账 Activity
 */
public class AccountActivity extends BaseActivity {

    @BindView(R.id.txt_title_left)
    TextView mTxtTitleLeft;
    @BindView(R.id.txt_title_right)
    TextView mTxtTitleRight;

    private AccountFragment mFragment;

    // 记账类型，默认支出
    public int mType = AppConfig.TYPE_COST;
    // 是否是编辑帐薄
    private boolean mIsEdit = false;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_account;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        Account account = getIntent().getParcelableExtra(AppConstants.EXTRA_DATA);
        Bundle bundle = null;
        if(account != null){
            mIsEdit = true;
            bundle = new Bundle();
            bundle.putParcelable(AppConstants.EXTRA_DATA, account);
        }

        showAnim();
        Toolbar toolbar = initToolbar(null);
        toolbar.setNavigationIcon(R.mipmap.ic_title_close);

        // set fragment
        mFragment = (AccountFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mFragment == null) {
            // Create the fragment
            mFragment = AccountFragment.newInstance(bundle);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment, R.id.contentFrame);
        }

        // create the presenter
        new AccountPresenter(new AccountRepository(), mFragment);
    }

    private void showAnim() {
        if(mIsEdit) return;

        TransitionsHeleper.getInstance().setShowMethod(new ColorShowMethod(R.color.white, R.color.colorPrimary) {
            @Override
            public void loadCopyView(InfoBean bean, ImageView copyView) {
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(copyView, AnimUtils.ROTATION, 0, 180),
                        ObjectAnimator.ofFloat(copyView, AnimUtils.SCALE_X, 1, 0),
                        ObjectAnimator.ofFloat(copyView, AnimUtils.SCALE_Y, 1, 0)
                );
                set.setInterpolator(new AccelerateInterpolator());
                set.setDuration(duration / 4 * 5).start();
            }

            @Override
            public void loadTargetView(InfoBean bean, ImageView targetView) {

            }
        }).show(this, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_account_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_finish:
                mFragment.save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.txt_title_left, R.id.txt_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_title_left:
                if(mType != AppConfig.TYPE_COST){
                    mType = AppConfig.TYPE_COST;
                    setCostType();
                }
                break;
            case R.id.txt_title_right:
                if(mType != AppConfig.TYPE_INCOME){
                    mType = AppConfig.TYPE_INCOME;
                    setCostType();
                }
                break;
        }
    }

    public void setCostType(){
        // 判断收入还是支出
        switch (mType){
            case AppConfig.TYPE_COST:
                mTxtTitleLeft.setBackgroundResource(R.drawable.bg_tb_select);
                mTxtTitleRight.setBackgroundResource(0);
                mFragment.selectCost();
                break;
            case AppConfig.TYPE_INCOME:
                mTxtTitleRight.setBackgroundResource(R.drawable.bg_tb_select);
                mTxtTitleLeft.setBackgroundResource(0);
                mFragment.selectIncome();
                break;
        }
    }

}

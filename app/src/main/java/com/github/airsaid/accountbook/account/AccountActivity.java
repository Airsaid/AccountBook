package com.github.airsaid.accountbook.account;

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
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;

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
    private int mType = AppConfig.TYPE_COST;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_account;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        showAnim();
        Toolbar toolbar = initToolbar(null);
        toolbar.setNavigationIcon(R.mipmap.ic_title_close);

        // set fragment
        mFragment = (AccountFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mFragment == null) {
            // Create the fragment
            mFragment = AccountFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment, R.id.contentFrame);
        }

        // create the presenter
        new AccountPresenter(new AccountRepository(), mFragment);
    }

    private void showAnim() {
        TransitionsHeleper.getInstance().setShowMethod(new ColorShowMethod(R.color.colorBg, R.color.colorPrimary) {
            @Override
            public void loadCopyView(InfoBean bean, ImageView copyView) {
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(copyView, "rotation", 0, 180),
                        ObjectAnimator.ofFloat(copyView, "scaleX", 1, 0),
                        ObjectAnimator.ofFloat(copyView, "scaleY", 1, 0)
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

    private void setCostType(){
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

    /* @OnClick({R.id.txt_type, R.id.txt_consume_type, R.id.txt_date})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_type:
                final String[] types = {"支出", "收入"};
                new AlertDialog.Builder(this)
                        .setTitle("选择类别")
                        .setItems(types, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTxtType.setText(types[which]);
                                mType = which + 1;
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.txt_consume_type:
                final String[] ctypes = {"一般", "学习", "餐饮", "交通", "购物", "医疗"};
                new AlertDialog.Builder(this)
                        .setTitle("选择类别")
                        .setItems(ctypes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTxtConsumeType.setText(ctypes[which]);
                                mCType = ctypes[which];
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.txt_date:

                break;
        }
    }

    public void save(View v){
        if(mType == -1){
            ToastUtils.show(this, "请选择类型");
            return;
        }

        if(mCType == null){
            ToastUtils.show(this, "请选择消费类型");
            return;
        }

        String price = mEdtPrice.getText().toString();
        if(TextUtils.isEmpty(price)){
            ToastUtils.show(this, "请输入金额");
            return;
        }

        String remarks = mEdtRemarks.getText().toString();
        if(TextUtils.isEmpty(remarks)){
            ToastUtils.show(this, "请输入备注");
            return;
        }

        Account account = new Account();
        account.type = mType;
        account.ctype = mCType;
        account.money = price;
        account.note = remarks;
        account.date = mDate;
        saveAccount(account);
    }

    private void saveAccount(Account account){
        AVObject a = new AVObject("Account");
        a.put("type", account.type);
        a.put("ctype", account.ctype);
        a.put("money", account.money);
        a.put("date", account.date);
        a.put("note", account.note);
        a.put("uid", AVUser.getCurrentUser());
        a.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    ToastUtils.show(mContext, "保存成功！");
                }else{
                    ToastUtils.show(mContext, e.getMessage());
                }
            }
        });
    }*/
}

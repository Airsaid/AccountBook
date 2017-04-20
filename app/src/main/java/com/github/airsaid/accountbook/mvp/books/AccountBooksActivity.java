package com.github.airsaid.accountbook.mvp.books;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.ui.activity.AddEditBookActivity;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.UiUtils;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/13
 * @desc 账薄 Activity
 */
public class AccountBooksActivity extends BaseActivity{

    private AccountBooksFragment mFragment;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_account_books));

        // set fragment
        mFragment = (AccountBooksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mFragment == null) {
            // Create the fragment
            mFragment = AccountBooksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment, R.id.contentFrame);
        }

        // create the presenter
        new AccountBooksPresenter(new AccountRepository(), mFragment);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_account_books_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_add:         // 添加帐薄
                startActivity(new Intent(mContext, AddEditBookActivity.class));
                break;
            case R.id.menu_title_add_share:   // 加入记账
                mFragment.addShareBook();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

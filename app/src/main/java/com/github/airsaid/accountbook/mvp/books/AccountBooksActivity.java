package com.github.airsaid.accountbook.mvp.books;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.util.ActivityUtils;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/13
 * @desc 账薄 Activity
 */
public class AccountBooksActivity extends BaseActivity{

    private AccountBooksPresenter mPresenter;
    private AccountRepository mRepository;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_account_books));

        // set fragment
        AccountBooksFragment fragment = (AccountBooksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = AccountBooksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        // create the presenter
        mRepository = new AccountRepository();
        mPresenter = new AccountBooksPresenter(mRepository, fragment);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_account_books_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_add:         // 添加帐薄

                break;
            case R.id.menu_title_add_share:   // 加入记账
                showInputBookIdDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示输入帐薄 ID Dialog
     */
    private void showInputBookIdDialog() {
        final AppCompatEditText edtBookId = new AppCompatEditText(mContext);
        edtBookId.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtBookId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_input_bid))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String bid = edtBookId.getText().toString();
                        if(TextUtils.isEmpty(bid)){
                            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_bid_error));
                        }else{
                            mRepository.addShareBook(UserUtils.getUser(), Long.valueOf(bid), new Callback() {
                                @Override
                                public void requestSuccess() {
                                    ToastUtils.show(mContext, UiUtils.getString(R.string.toast_add_share_book_success));
                                }

                                @Override
                                public void requestFail(Error e) {
                                    ToastUtils.show(mContext, e.getMessage());
                                }
                            });
                        }
                    }
                })
                .create();
        dialog.setView(edtBookId, DimenUtils.dp2px(16), DimenUtils.dp2px(16), DimenUtils.dp2px(16), DimenUtils.dp2px(16));
        dialog.show();
    }
}

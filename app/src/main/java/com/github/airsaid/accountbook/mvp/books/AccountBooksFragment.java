package com.github.airsaid.accountbook.mvp.books;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.AccountBooksAdapter;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.constants.MsgConstants;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.ui.activity.AddEditBookActivity;
import com.github.airsaid.accountbook.ui.activity.AddShareUserActivity;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.recycler.OnSimpleClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄 Fragment
 */
public class AccountBooksFragment extends BaseFragment implements AccountBooksContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private AccountBooksContract.Presenter mPresenter;
    private AccountBooksAdapter mAdapter;

    public static AccountBooksFragment newInstance() {
        return new AccountBooksFragment();
    }

    @Override
    public void setPresenter(AccountBooksContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_books, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        initAdapter();
        onRefresh();
    }

    @Override
    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onStart();
    }

    private void initAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccountBooksAdapter(R.layout.item_account_books_list, new ArrayList<AccountBook>());
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnSimpleClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                // 设置当前帐薄
                AccountBook book = (AccountBook) baseQuickAdapter.getData().get(i);
                ProgressUtils.show(mContext, UiUtils.getString(R.string.load_switch));
                mPresenter.setCurrentBook(UserUtils.getUser(), book.getBid());
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                switch (view.getId()){
                    case R.id.img_add_user: // 进入邀请好友页
                        AccountBook book = (AccountBook) baseQuickAdapter.getData().get(i);
                        Intent intent = new Intent(mContext, AddShareUserActivity.class);
                        intent.putExtra(AppConstants.EXTRA_DATA, book);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                final AccountBook book = (AccountBook) baseQuickAdapter.getData().get(i);
                showOperateBookDialog(book);
            }
        });
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPresenter.queryBooks(UserUtils.getUser());
            }
        });
    }

    @Override
    public void queryBooksSuccess(List<AccountBook> books) {
        mRefreshLayout.setRefreshing(false);
        mAdapter.setNewData(books);
    }

    @Override
    public void queryBooksFail(Error e) {
        mRefreshLayout.setRefreshing(false);
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void setCurrentBookSuccess() {
        ProgressUtils.dismiss();
        Message msg = new Message();
        msg.what = MsgConstants.MSG_SET_CUR_BOOK_SUCCESS;
        EventBus.getDefault().post(msg);
        finish();
    }

    @Override
    public void setCurrentBookFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void addShareBook() {
        showInputBookIdDialog();
    }

    @Override
    public void addShareBookSuccess() {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_apply_success));
    }

    @Override
    public void addShareBookFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showOperateBookDialog(final AccountBook book) {
        final String editBookStr = UiUtils.getString(R.string.dialog_item_edit_book);
        final String exitBookStr = UiUtils.getString(R.string.dialog_item_exit_book);
        final String deleteBookStr = UiUtils.getString(R.string.dialog_item_delete_book);
        // 判断是否是当前帐薄，是则不可删除
        final String[] items;
        if(book.isCurrent()){
            items = new String[]{editBookStr};
        }else{
            // 判断是否有其他共享用户
            if(book.getShares().size() <= 1){
                // 没有，可编辑、删除帐薄
                items = new String[]{editBookStr, deleteBookStr};
            }else{
                // 有，可编辑、退出帐薄
                items = new String[]{editBookStr, exitBookStr};
            }
        }
        new AlertDialog.Builder(mContext)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = items[which];
                        if(item.equals(editBookStr)){
                            Intent intent = new Intent(mContext, AddEditBookActivity.class);
                            intent.putExtra(AppConstants.EXTRA_DATA, book);
                            startActivity(intent);
                        }else if(item.equals(exitBookStr)){
                            showExitBookDialog(book);
                        }else if(item.equals(deleteBookStr)){
                            showDeleteBookDialog(book.getBid());
                        }
                    }
                }).create().show();
    }

    @Override
    public void showExitBookDialog(final AccountBook book) {
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_content_exit_book))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressUtils.show(mContext);
                        mPresenter.exitBook(UserUtils.getUser(), book);
                    }
                }).create().show();
    }

    @Override
    public void showDeleteBookDialog(final long bid) {
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_content_delete_book))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel_delete_book), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm_delete_book), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressUtils.show(mContext, UiUtils.getString(R.string.load_delete));
                        mPresenter.deleteBook(bid);
                    }
                }).create().show();
    }

    @Override
    public void exitBookSuccess() {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_exit_success));
        onRefresh();
    }

    @Override
    public void exitBookFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void deleteBookSuccess() {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_delete_success));
        onRefresh();
    }

    @Override
    public void deleteBookFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    /**
     * 显示输入帐薄 ID Dialog
     */
    private void showInputBookIdDialog() {
        final AppCompatEditText editText = new AppCompatEditText(mContext);
        editText.setHint(UiUtils.getString(R.string.hint_input_book_id));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_input_bid))
                .setCancelable(false)
                .setView(editText, DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_add), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String bid = editText.getText().toString();
                        if(TextUtils.isEmpty(bid)){
                            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_bid_error));
                        }else{
                            dialog.dismiss();
                            ProgressUtils.show(mContext);
                            mPresenter.addShareBook(UserUtils.getUser(), Long.valueOf(bid));
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message msg) {
        switch (msg.what) {
            case MsgConstants.MSG_ADD_BOOK_SUCCESS:
            case MsgConstants.MSG_EDIT_BOOK_SUCCESS:
            case MsgConstants.MSG_DELETE_BOOK_SUCCESS:
                onRefresh();
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}

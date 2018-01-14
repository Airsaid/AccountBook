package com.github.airsaid.accountbook.mvp.count;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.AccountListAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.constants.MsgConstants;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.data.source.AccountDataSource;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.mvp.account.AccountActivity;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.widget.recycler.OnSimpleClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author airsaid
 *
 * 分类收支详情.
 */
public class TypeCountDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    private AccountListAdapter mAdapter;
    private String mStartDate, mEndDate;
    private AccountRepository mRepository;
    private String mTypeName;
    private int mPage = 1;
    private int mType;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_type_count_detail;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        mTypeName   = getIntent().getStringExtra(AppConstants.EXTRA_TYPE_NAME);
        mStartDate  = getIntent().getStringExtra(AppConstants.EXTRA_START_DATE);
        mEndDate    = getIntent().getStringExtra(AppConstants.EXTRA_END_DATE);
        mType       = getIntent().getIntExtra(AppConstants.EXTRA_TYPE, -1);
        mRepository = new AccountRepository();
        initToolbar(mTypeName);
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
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(
                UiUtils.getResourceId(mContext, R.attr.colorAccent, R.color.colorAccent));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccountListAdapter(new ArrayList<Account>());
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.setEmptyView(UiUtils.getEmptyView(mContext, mRecyclerView
                , UiUtils.getString(R.string.empty_account_data)));
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnSimpleClickListener(){
            @Override
            public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Account account = (Account) baseQuickAdapter.getData().get(i);
                showOperateAccountDialog(account);
            }
        });
    }

    private void showOperateAccountDialog(final Account account) {
        final String[] items = new String[]{UiUtils.getString(R.string.dialog_item_edit_account)
                , UiUtils.getString(R.string.dialog_item_delete_account)};
        new AlertDialog.Builder(mContext)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = items[i];
                        if(item.equals(UiUtils.getString(R.string.dialog_item_edit_account))){// 编辑账目
                            Intent intent = new Intent(mContext, AccountActivity.class);
                            intent.putExtra(AppConstants.EXTRA_DATA, account);
                            startActivity(intent);
                        }else if(item.equals(UiUtils.getString(R.string.dialog_item_delete_account))){// 删除账目
                            showDeleteAccountDialog(account);
                        }
                    }
                }).create().show();
    }

    public void showDeleteAccountDialog(final Account account) {
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_content_delete_account))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount(account);
                    }
                }).create().show();
    }

    private void deleteAccount(Account account) {
        ProgressUtils.show(mContext, getString(R.string.load_delete));
        mRepository.deleteAccount(account, new Callback() {
            @Override
            public void requestSuccess() {
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_delete_success));
                ProgressUtils.dismiss();
                onRefresh();
            }

            @Override
            public void requestFail(Error e) {
                ToastUtils.show(mContext, e.getMessage());
                ProgressUtils.dismiss();
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        requestData();
    }

    @Override
    public void onLoadMoreRequested() {
        mPage ++;
        requestData();
    }

    private void requestData() {
        mRepository.queryTypeDetailAccount(mStartDate, mEndDate, mTypeName, mType, mPage, new AccountDataSource.QueryAccountsCallback() {
            @Override
            public void querySuccess(List<Account> list) {
                mRefreshLayout.setRefreshing(false);
                if(mPage <= 1){
                    mAdapter.setNewData(mAdapter.setItemType(list));
                }else{
                    mAdapter.addData(mAdapter.setItemType(list));
                    if(list.size() < AppConfig.LIMIT){
                        mAdapter.loadMoreEnd();
                    }else{
                        mAdapter.loadMoreComplete();
                    }
                }
            }

            @Override
            public void queryFail(Error e) {
                mRefreshLayout.setRefreshing(false);
                ToastUtils.show(mContext, e.getMessage());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message msg) {
        switch (msg.what) {
            case MsgConstants.MSG_SAVE_ACCOUNT_SUCCESS:
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

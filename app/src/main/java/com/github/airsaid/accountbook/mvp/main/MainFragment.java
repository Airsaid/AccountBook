package com.github.airsaid.accountbook.mvp.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.AccountListAdapter;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.constants.MsgConstants;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.mvp.account.AccountActivity;
import com.github.airsaid.accountbook.util.DateUtils;
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
 * @date 2017/4/6
 * @desc 首页 Fragment
 */
public class MainFragment extends BaseFragment implements MainContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    private MainContract.Presenter mPresenter;
    private String mStartDate;
    private String mEndDate;

    private AccountListAdapter mAdapter;
    private int mPage = 1;

    private View mHeadView;
    private TextView mTxtTotalCost;
    private TextView mTxtTotalIncome;

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public static MainFragment newInstance(Bundle args) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        initHeadView();
        initAdapter();

        Bundle arguments = getArguments();
        if (arguments != null) {
            mStartDate = arguments.getString(AppConstants.EXTRA_POSITION);
            mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
            onRefresh();
        }
    }

    private void initHeadView() {
        mHeadView = LayoutInflater.from(mContext).inflate(R.layout.rlv_header_main, null);
        mTxtTotalCost = (TextView) mHeadView.findViewById(R.id.txt_total_cost);
        mTxtTotalIncome = (TextView) mHeadView.findViewById(R.id.txt_total_income);
    }

    private void initAdapter() {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccountListAdapter(new ArrayList<Account>());
        mAdapter.setHeaderView(mHeadView);
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

    @Override
    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onStart();
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPage = 1;
                requestData();
                mPresenter.queryAccountTotalMoney(UserUtils.getUser(), mStartDate, mEndDate);
            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        mPage ++;
        requestData();
    }

    /**
     * 请求数据
     */
    private void requestData() {
        mPresenter.queryAccount(UserUtils.getUser(), mStartDate, mEndDate, mPage);
    }

    @Override
    public void querySuccess(List<Account> list) {
        mRefreshLayout.setRefreshing(false);
        if(mPage <= 1){
            mAdapter.setNewData(mAdapter.setItemType(list));
        }else{
            mAdapter.addData(mAdapter.setItemType(list));
            if(list.size() < AppConfig.LIMIT){
                mAdapter.loadMoreEnd();// 隐藏加载更多
            }else{
                mAdapter.loadMoreComplete();
            }
        }
    }

    @Override
    public void shareUsers(int count) {
        // 当共享人数大于 1 时才去显示头像
        mAdapter.setIsShowAvatar(count > 1);
    }

    @Override
    public void queryFail(Error e) {
        mRefreshLayout.setRefreshing(false);
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void queryTotalMoneySuccess(double totalCost, double totalIncome) {
        mTxtTotalCost.setText(String.valueOf(totalCost));
        mTxtTotalIncome.setText(String.valueOf(totalIncome));
    }

    @Override
    public void queryTotalMoneyFail(Error e) {
        mTxtTotalCost.setText(UiUtils.getString(R.string.money_normal));
        mTxtTotalIncome.setText(UiUtils.getString(R.string.money_normal));
    }

    @Override
    public void deleteSuccess() {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_delete_success));
        onRefresh();
    }

    @Override
    public void deleteFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showOperateAccountDialog(final Account account) {
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

    @Override
    public void showDeleteAccountDialog(final Account account) {
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_content_delete_account))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProgressUtils.show(mContext, UiUtils.getString(R.string.load_delete));
                        mPresenter.deleteAccount(account);
                    }
                }).create().show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message msg) {
        switch (msg.what) {
            case MsgConstants.MSG_SAVE_ACCOUNT_SUCCESS:
            case MsgConstants.MSG_SET_CUR_BOOK_SUCCESS:
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

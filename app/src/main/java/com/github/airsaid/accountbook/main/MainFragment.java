package com.github.airsaid.accountbook.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.AccountListAdapter;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.LogUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.widget.recycler.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc 首页 Fragment
 */
public class MainFragment extends BaseFragment implements MainContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    private MainContract.Presenter mPresenter;
    private String mStartDate;
    private String mEndDate;

    private List<Account> mLists = new ArrayList<>();
    private AccountListAdapter mAdapter;
    private int mPage = 1;

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public static MainFragment newInstance(Bundle args) {
        LogUtils.e("test", "newInstance");
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
        initAdapter();

        Bundle arguments = getArguments();
        if (arguments != null) {
            LogUtils.e("test", "onCreateFragment");
            mStartDate = arguments.getString(AppConstants.EXTRA_POSITION);
            mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
            requestData();

        }
    }

    private void initAdapter() {
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .size(DimenUtils.dp2px(10f))
                .color(R.color.transparent)
                .showLastDivider()
                .build());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccountListAdapter(R.layout.item_account_list, mLists);
        mAdapter.setEmptyView(UiUtils.getEmptyView(mContext, mRecyclerView
                , UiUtils.getString(R.string.empty_account_data)));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onLazyLoadData() {
        super.onLazyLoadData();
        if (mIsVisiable) {
            requestData();
        }
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        requestData();
    }

    /**
     * 请求数据。
     */
    private void requestData(){
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mPresenter.queryAccount(mStartDate, mEndDate, mPage);
            }
        });
    }

    @Override
    public void querySuccess(List<Account> list) {
        mRefreshLayout.setRefreshing(false);
        if(mPage == 1){
            mAdapter.setNewData(list);
        }else{
            mAdapter.addData(list);
        }
    }

    @Override
    public void queryFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

}

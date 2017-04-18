package com.github.airsaid.accountbook.mvp.books;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.AccountBooksAdapter;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.ui.activity.AddShareUserActivity;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.recycler.OnSimpleClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄 Fragment
 */
public class AccountBooksFragment extends BaseFragment implements AccountBooksContract.View {

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
        initAdapter();
        mPresenter.queryBooks(UserUtils.getUser());
    }

    private void initAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccountBooksAdapter(R.layout.item_account_books_list, new ArrayList<AccountBook>());
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnSimpleClickListener() {
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
        });
    }

    @Override
    public void queryBooksSuccess(List<AccountBook> books) {
        mAdapter.setNewData(books);
    }

    @Override
    public void queryBooksFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

}

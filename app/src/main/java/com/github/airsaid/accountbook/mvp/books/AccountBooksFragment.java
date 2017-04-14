package com.github.airsaid.accountbook.mvp.books;

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
import com.github.airsaid.accountbook.data.AccountBooks;

import java.util.ArrayList;

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
        initData();
    }

    private void initData() {
        AccountBooks books = new AccountBooks();
        books.cover = R.mipmap.book_icon1;
        books.name = "日常帐薄";
        books.isCur = true;
        books.scene = "日常";
        books.sceneImg = R.mipmap.book_scene1;
        mAdapter.addData(books);
    }

    private void initAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccountBooksAdapter(R.layout.item_account_books_list, new ArrayList<AccountBooks>());
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setAdapter(mAdapter);
    }


}

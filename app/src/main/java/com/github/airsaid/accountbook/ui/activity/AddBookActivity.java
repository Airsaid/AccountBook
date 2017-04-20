package com.github.airsaid.accountbook.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.BookCoverAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.BookCover;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/20
 * @desc 添加帐薄 Activity
 */
public class AddBookActivity extends BaseActivity {

    @BindView(R.id.txt_book_name_size)
    TextView mTxtBookNameSize;
    @BindView(R.id.edt_book_name)
    AppCompatEditText mEdtBookName;
    @BindView(R.id.txt_book_scene)
    TextView mTxtBookScene;
    @BindView(R.id.rlv_cover)
    RecyclerView mRlvCover;

    private List<BookCover> mCovers = new ArrayList<>();
    private BookCoverAdapter mAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_add_book;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_add_book));
        initAdapter();
        initData();
        mEdtBookName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = 0;
                setBookNameSize(charSequence != null ? charSequence.length() : length);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        setBookNameSize(0);
    }

    private void initAdapter() {
        mRlvCover.setLayoutManager(new LinearLayoutManager(mContext
                , LinearLayoutManager.HORIZONTAL, true));
        mAdapter = new BookCoverAdapter(R.layout.item_book_cover, mCovers);
        mRlvCover.setAdapter(mAdapter);
    }

    private void initData() {
        mCovers.clear();
        for (int i = 6; i >= 1; i--) {
            BookCover cover = new BookCover();
            cover.cover = "book_icon" + i;
            cover.isSelect = i == 1;
            mCovers.add(cover);
        }
        mAdapter.setNewData(mCovers);
        mRlvCover.scrollToPosition(0);
    }

    /**
     * 设置帐薄名称字符长度
     */
    private void setBookNameSize(int length){
        mTxtBookNameSize.setText("(".concat(String.valueOf(length)).concat("/10)"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_book_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_add:   // 添加帐薄

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.llt_select_book_scene)
    public void onClick() {
        // 选择场景

    }
}

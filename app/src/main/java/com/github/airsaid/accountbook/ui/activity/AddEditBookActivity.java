package com.github.airsaid.accountbook.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.BookCoverAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.constants.MsgConstants;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.BookCover;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.recycler.OnSimpleClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/20
 * @desc 添加/编辑帐薄 Activity
 */
public class AddEditBookActivity extends BaseActivity {

    @BindView(R.id.txt_book_name_size)
    TextView mTxtBookNameSize;
    @BindView(R.id.edt_book_name)
    AppCompatEditText mEdtBookName;
    @BindView(R.id.txt_book_scene)
    TextView mTxtBookScene;
    @BindView(R.id.rlv_cover)
    RecyclerView mRlvCover;
    @BindView(R.id.btn_delete_book)
    AppCompatButton mBtnDeleteBook;

    private List<BookCover> mCovers = new ArrayList<>();
    private AccountRepository mRepository;
    private BookCoverAdapter mAdapter;
    private AccountBook mBook;
    private boolean mIsEdit = false;// 是否是编辑账簿

    @Override
    public int getLayoutRes() {
        return R.layout.activity_add_book;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        mBook = getIntent().getParcelableExtra(AppConstants.EXTRA_DATA);
        if(mBook != null){
            mIsEdit = true;
        }

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

        initToolbar(UiUtils.getString(mIsEdit ? R.string.title_edit_book : R.string.title_add_book));
        initAdapter();
        initData();
        echoData();
        mRepository = new AccountRepository();
    }

    private void echoData(){
        if(!mIsEdit) return;

        mEdtBookName.setText(mBook.getName());
        mTxtBookScene.setText(mBook.getScene());
        if(!mBook.isCurrent())
            mBtnDeleteBook.setVisibility(View.VISIBLE);
        List<BookCover> covers = mAdapter.getData();
        for (int p = 0; p < covers.size(); p++) {
            BookCover cover = covers.get(p);
            cover.isSelect = mBook.getCover().equals(cover.cover);
            if(cover.isSelect){
                mRlvCover.scrollToPosition(p);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        mRlvCover.setLayoutManager(new LinearLayoutManager(mContext
                , LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new BookCoverAdapter(R.layout.item_book_cover, mCovers);
        mRlvCover.setAdapter(mAdapter);
        mRlvCover.addOnItemTouchListener(new OnSimpleClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                setSelectPosition(i);
            }
        });
    }

    private void initData() {
        mCovers.clear();
        for (int i = 1; i <= 6; i++) {
            BookCover cover = new BookCover();
            cover.cover = "book_icon" + i;
            cover.isSelect = i == 1;
            mCovers.add(cover);
        }
        mAdapter.setNewData(mCovers);
    }

    private void setBookNameSize(int length){
        mTxtBookNameSize.setText("(".concat(String.valueOf(length)).concat("/10)"));
    }

    private void setSelectPosition(int position){
        List<BookCover> covers = mAdapter.getData();
        for (int p = 0; p < covers.size(); p++) {
            covers.get(p).isSelect = p == position;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_book_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_add:   // 添加/修改帐薄
                addOrEditBook();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addOrEditBook() {
        String name = mEdtBookName.getText().toString();
        String scene = mTxtBookScene.getText().toString();
        String cover = null;
        for (BookCover bookCover : mAdapter.getData()) {
            if(bookCover.isSelect){
                cover = bookCover.cover;
            }
        }
        if(TextUtils.isEmpty(name)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_book_name));
        }else if(TextUtils.isEmpty(scene)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_select_book_scene));
        }else if(TextUtils.isEmpty(cover)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_select_book_scene));
        }else{
            ProgressUtils.show(mContext);
            if(mIsEdit){
                mBook.setName(name);
                mBook.setScene(scene);
                mBook.setCover(cover);
                mRepository.editBook(mBook, new Callback() {
                    @Override
                    public void requestSuccess() {
                        ProgressUtils.dismiss();
                        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_edit_success));

                        Message msg = new Message();
                        msg.what = MsgConstants.MSG_EDIT_BOOK_SUCCESS;
                        EventBus.getDefault().post(msg);

                        finish();
                    }

                    @Override
                    public void requestFail(Error e) {
                        ProgressUtils.dismiss();
                        ToastUtils.show(mContext, e.getMessage());
                    }
                });
            }else{
                AccountBook book = new AccountBook();
                book.setName(name);
                book.setScene(scene);
                book.setCover(cover);
                book.setCurrent(false);
                book.setOwner(UserUtils.getUser());
                book.setShare(Arrays.asList(UserUtils.getUser()));
                mRepository.addBook(book, new Callback() {
                    @Override
                    public void requestSuccess() {
                        ProgressUtils.dismiss();
                        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_add_success));

                        Message msg = new Message();
                        msg.what = MsgConstants.MSG_ADD_BOOK_SUCCESS;
                        EventBus.getDefault().post(msg);

                        finish();
                    }

                    @Override
                    public void requestFail(Error e) {
                        ProgressUtils.dismiss();
                        ToastUtils.show(mContext, e.getMessage());
                    }
                });
            }
        }
    }

    @OnClick({R.id.llt_select_book_scene, R.id.btn_delete_book})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llt_select_book_scene: // 选择场景
                final String[] scenes = UiUtils.getStringArray(R.array.book_scene_name);
                new AlertDialog.Builder(mContext)
                        .setTitle(UiUtils.getString(R.string.dialog_title_select_scene))
                        .setItems(scenes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String scene = scenes[which];
                                mTxtBookScene.setText(scene);
                            }
                        }).create().show();
                break;
            case R.id.btn_delete_book:      // 删除帐薄
                if(mIsEdit && !mBook.isCurrent()){
                    showDeleteBookDialog();
                }
                break;
        }

    }

    private void showDeleteBookDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_content_delete_book))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel_delete_book), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm_delete_book), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBook();
                    }
                }).create().show();
    }

    private void deleteBook() {
        ProgressUtils.show(mContext, UiUtils.getString(R.string.load_delete));
        mRepository.deleteBook(mBook.getBid(), new Callback() {
            @Override
            public void requestSuccess() {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_delete_success));

                Message msg = new Message();
                msg.what = MsgConstants.MSG_DELETE_BOOK_SUCCESS;
                EventBus.getDefault().post(msg);

                finish();
            }

            @Override
            public void requestFail(Error e) {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, e.getMessage());
            }
        });
    }
}

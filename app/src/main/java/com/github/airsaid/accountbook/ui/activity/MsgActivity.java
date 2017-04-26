package com.github.airsaid.accountbook.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.MsgAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.Msg;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.data.source.CommonDataSource;
import com.github.airsaid.accountbook.data.source.CommonRepository;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.recycler.HorizontalDividerItemDecoration;
import com.github.airsaid.accountbook.widget.recycler.OnSimpleClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/25
 * @desc 消息 Activity
 */
public class MsgActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    private MsgAdapter mAdapter;
    private CommonRepository mRepository;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_msg;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_msg));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRepository = new CommonRepository();
        initAdapter();
        onRefresh();
    }

    private void initAdapter() {
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .size(DimenUtils.dp2px(10))
                .color(R.color.transparent)
                .showLastDivider()
                .build());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MsgAdapter(R.layout.item_msg, new ArrayList<Msg>());
        mAdapter.setEmptyView(UiUtils.getEmptyView(mContext, mRecyclerView
                , UiUtils.getString(R.string.empty_msg_data)));
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new OnSimpleClickListener(){
            @Override
            public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Msg msg = (Msg) baseQuickAdapter.getData().get(i);
                showOperateDialog(msg);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Msg msg = (Msg) baseQuickAdapter.getData().get(i);
                switch (view.getId()){
                    case R.id.btn_agree:    // 同意&删除消息
                        AccountBook book = msg.getApplyBook();
                        if(book != null){
                            agreeAddBook(msg);
                        }else{
                            deleteMessage(msg);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 显示消息操作 Dialog
     */
    private void showOperateDialog(final Msg msg) {
        final String[] items;
        if(msg.getType() == AppConfig.TYPE_MSG_APPLY_BOOK){
            items = new String[]{UiUtils.getString(R.string.dialog_item_refuse_apply)};
        }else{
            items = new String[]{UiUtils.getString(R.string.dialog_item_delete_msg)};
        }
        new AlertDialog.Builder(mContext)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = items[i];
                        if(item.equals(UiUtils.getString(R.string.dialog_item_refuse_apply))){// 拒绝申请
                            refuseAddBook(msg);
                        }else if(item.equals(UiUtils.getString(R.string.dialog_item_delete_msg))){// 删除消息
                            deleteMessage(msg);
                        }
                    }
                }).create().show();
    }

    /**
     * 同意加入帐薄
     */
    private void agreeAddBook(Msg msg) {
        ProgressUtils.show(mContext);
        mRepository.agreeAddBook(msg, new Callback() {
            @Override
            public void requestSuccess() {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_agree_success));
                onRefresh();
            }

            @Override
            public void requestFail(Error e) {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, e.getMessage());
            }
        });
    }

    /**
     * 拒绝加入帐薄
     */
    private void refuseAddBook(Msg msg) {
        ProgressUtils.show(mContext);
        mRepository.refuseAddBook(msg, new Callback() {
            @Override
            public void requestSuccess() {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_operate_success));
                onRefresh();
            }

            @Override
            public void requestFail(Error e) {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, e.getMessage());
            }
        });
    }

    /**
     * 删除消息
     */
    private void deleteMessage(Msg msg) {
        ProgressUtils.show(mContext, UiUtils.getString(R.string.load_delete));
        mRepository.deleteMessage(msg, new Callback() {
            @Override
            public void requestSuccess() {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_delete_success));
                onRefresh();
            }

            @Override
            public void requestFail(Error e) {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, e.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                requestData();
            }
        });
    }

    private void requestData(){
        mRepository.queryMsgList(UserUtils.getUser(), new CommonDataSource.QueryMsgListCallback() {
            @Override
            public void querySuccess(List<Msg> list) {
                mRefreshLayout.setRefreshing(false);
                mAdapter.setNewData(list);
            }

            @Override
            public void queryFail(Error e) {
                mRefreshLayout.setRefreshing(false);
                ToastUtils.show(mContext, e.getMessage());
            }
        });
    }
}

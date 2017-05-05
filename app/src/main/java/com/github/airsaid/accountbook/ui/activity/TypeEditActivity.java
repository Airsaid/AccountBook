package com.github.airsaid.accountbook.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.TypeEditAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.base.BaseApplication;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.Type;
import com.github.airsaid.accountbook.data.TypeDao;
import com.github.airsaid.accountbook.util.LogUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.recycler.OnSimpleClickListener;

import java.util.List;

import butterknife.BindView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/3
 * @desc 支出/收入分类编辑 Activity
 */
public class TypeEditActivity extends BaseActivity {

    public static final int CODE_ADD_TYPE = 1;
    public static final int CODE_EDIT_TYPE = 2;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private TypeEditAdapter mAdapter;
    private List<Type> mTypes;
    private TypeDao mDao;
    private int mType;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_list;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        mType = getIntent().getIntExtra(AppConstants.EXTRA_ACCOUNT_TYPE, -1);
        if (mType == AppConfig.TYPE_COST) {
            initToolbar(UiUtils.getString(R.string.title_edit_cost_type));
        } else if (mType == AppConfig.TYPE_INCOME) {
            initToolbar(UiUtils.getString(R.string.title_edit_income_type));
        } else {
            finish();
        }
        initAdapter();
        mAdapter.setNewData(initData());
    }

    /**
     * 根据分类获取对应数据
     */
    private List<Type> initData() {
        mDao = BaseApplication.getInstance().getSession().getTypeDao();
        mTypes = mDao.queryBuilder()
                .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(mType))
                .orderAsc(TypeDao.Properties.Index)
                .list();
        return mTypes;
    }

    private void initAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new TypeEditAdapter(mTypes);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
        mAdapter.enableDragItem(itemTouchHelper);
        mAdapter.setOnItemDragListener(onItemDragListener);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new OnSimpleClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Type data = (Type) baseQuickAdapter.getData().get(i);
                // 编辑分类
                Intent intent = new Intent(mContext, AddEditTypeActivity.class);
                intent.putExtra(AppConstants.EXTRA_ACCOUNT_TYPE, mType);
                intent.putExtra(AppConstants.EXTRA_DATA, data);
                startActivityForResult(intent, CODE_EDIT_TYPE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_type_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_add:
                Intent intent = new Intent(mContext, AddEditTypeActivity.class);
                intent.putExtra(AppConstants.EXTRA_ACCOUNT_TYPE, mType);
                startActivityForResult(intent, CODE_ADD_TYPE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            updateIndex(pos);
        }
    };

    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            List<Type> data = mAdapter.getData();
            if(data.size() > 1){
                Type type = data.get(pos);
                deleteType(type);
            }
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder
                , float dX, float dY, boolean isCurrentlyActive) {
        }
    };

    /**
     * 根据位置索引更新分类对应索引
     * @param pos 索引
     */
    private void updateIndex(int pos) {
        List<Type> data = mAdapter.getData();
        for(int i = pos; i < data.size(); i++){
            Type type = data.get(i);
            type.setIndex(i);
            mDao.update(type);
        }
    }

    /**
     * 删除分类
     * @param type 要删除的分类
     */
    private void deleteType(final Type type) {
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_delete_type))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAdapter.setNewData(initData());
                    }
                })
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDao.delete(type);
                        mAdapter.isDisableSwipe(initData());
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CODE_ADD_TYPE:     // 添加分类
                case CODE_EDIT_TYPE:    // 编辑分类
                    mAdapter.setNewData(initData());
                    break;
            }
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}

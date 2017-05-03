package com.github.airsaid.accountbook.ui.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

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

import java.util.List;

import butterknife.BindView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/3
 * @desc 支出/收入分类编辑 Activity
 */
public class TypeEditActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private List<Type> mTypes;
    private TypeEditAdapter mAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_list;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        int type = getIntent().getIntExtra(AppConstants.EXTRA_TYPE, AppConfig.TYPE_COST);
        if (type == AppConfig.TYPE_COST) {
            initToolbar(UiUtils.getString(R.string.title_edit_cost_type));
        } else if (type == AppConfig.TYPE_INCOME) {
            initToolbar(UiUtils.getString(R.string.title_edit_income_type));
        } else {
            finish();
        }
        initData(type);
        initAdapter();
    }

    /**
     * 根据分类获取对应数据
     *
     * @param type 支出、收入分类
     */
    private void initData(int type) {
        TypeDao dao = BaseApplication.getInstance().getSession().getTypeDao();
        mTypes = dao.queryBuilder()
                .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(type))
                .orderAsc(TypeDao.Properties.Index)
                .list();
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
    }

    OnItemDragListener onItemDragListener = new OnItemDragListener() {

        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("test", "onItemDragStart");
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            LogUtils.e("test", "onItemDragMoving");
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            for (int i = pos; i < mAdapter.getData().size(); i++) {
                Type type = mAdapter.getData().get(i);
            }
            LogUtils.e("test", "onItemDragEnd pos: " + pos);
        }
    };

    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("test", "onItemSwipeStart");
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("test", "clearView");
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("test", "onItemSwiped");
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            LogUtils.e("test", "onItemSwipeMoving");
        }
    };

}

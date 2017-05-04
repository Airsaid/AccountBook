package com.github.airsaid.accountbook.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
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
import com.github.airsaid.accountbook.adapter.AddEditTypeAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.base.BaseApplication;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.AddEditType;
import com.github.airsaid.accountbook.data.Type;
import com.github.airsaid.accountbook.data.TypeDao;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.recycler.OnSimpleClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/4
 * @desc 添加/编辑 分类 Activity
 */
public class AddEditTypeActivity extends BaseActivity {

    @BindView(R.id.txt_size)
    TextView mTxtSize;
    @BindView(R.id.edt_name)
    AppCompatEditText mEdtName;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private AddEditTypeAdapter mAdapter;
    private List<AddEditType> mTypes;
    private AddEditType mPreType;
    private TypeDao mDao;
    private Type mData;
    private int mType;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_add_edit_type;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        mData = getIntent().getParcelableExtra(AppConstants.EXTRA_DATA);
        mType = getIntent().getIntExtra(AppConstants.EXTRA_ACCOUNT_TYPE, AppConfig.TYPE_COST);
        if(mData == null){
            initToolbar(UiUtils.getString(R.string.title_add_type));
        }else{
            initToolbar(UiUtils.getString(R.string.title_edit_type));
        }

        mEdtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = 0;
                setSize(charSequence != null ? charSequence.length() : length);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        setSize(0);
        initData();
        initAdapter();
        mDao = BaseApplication.getInstance().getSession().getTypeDao();
    }

    private void setSize(int length){
        mTxtSize.setText("(".concat(String.valueOf(length)).concat("/10)"));
    }

    private void initData() {
        String typeIcon = "";
        if(mData != null){
            // 回显数据
            mEdtName.setText(mData.getName());
            typeIcon = mData.getIcon();
        }

        mTypes = new ArrayList<>();
        String[] costTypes = getResources().getStringArray(R.array.account_cost_type);
        String[] incomeTypes = getResources().getStringArray(R.array.account_income_type);
        addTypeData(costTypes.length, "ic_cost_type_", typeIcon);
        addTypeData(incomeTypes.length, "ic_income_type_", typeIcon);
        addTypeData(75, "type_add_", typeIcon);
    }

    /**
     * 添加分类数据
     */
    private void addTypeData(int size, String name, String imageName){
        for (int i = 0; i < size; i++) {
            String image = name.concat(String.valueOf(i));
            AddEditType type = new AddEditType();
            type.icon = image;
            if(!image.equals(imageName)){
                type.isSelect = false;
            }else{
                type.isSelect = true;
                mPreType = type;
            }
            mTypes.add(type);
        }
    }

    private void initAdapter() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));
        mAdapter = new AddEditTypeAdapter(R.layout.item_add_edit_type, mTypes);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnSimpleClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                // 设置选中分类图标
                AddEditType type = (AddEditType) baseQuickAdapter.getData().get(i);
                if(mPreType != null)
                    mPreType.isSelect = false;
                type.isSelect = true;
                mPreType = type;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_account_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_finish:// 保存分类
                saveType();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveType() {
        String name = mEdtName.getText().toString();
        if(TextUtils.isEmpty(name)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_type_name));
        }else if(mPreType == null){
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_select_type_icon));
        }else{
            if(mData == null){
                Type type = new Type();
                type.setUid(UserUtils.getUid());
                type.setIndex((int) getIndex());
                type.setIcon(mPreType.icon);
                type.setType(mType);
                type.setName(name);
                mDao.insert(type);
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_save_success));
            }else{
                mData.setIcon(mPreType.icon);
                mData.setName(name);
                mDao.update(mData);
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_update_success));
            }
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * 根据支出、收入类型获取最后位置索引。
     * @return 新加添分类位置索引
     */
    private long getIndex(){
        return mDao.queryBuilder()
                .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(mType))
                .count();
    }

}

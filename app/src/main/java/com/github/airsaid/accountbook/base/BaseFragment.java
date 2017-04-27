package com.github.airsaid.accountbook.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.airsaid.accountbook.R;

import butterknife.ButterKnife;

/**
 * Created by zhouyou on 2016/6/27.
 * Class desc: fragment base class
 */
public abstract class BaseFragment extends Fragment {

    private View mView;
    private Toolbar mToolbar;
    private Activity mActivity;
    protected Context mContext;
    protected static String TAG;
    private AppCompatActivity mCompatActivity;
    protected LayoutInflater mLayoutInflater;

    // 是否可见
    protected boolean mIsVisiable;
    // 是否已经调用了 onCreateView
    protected boolean mIsViewCreate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mActivity = getActivity();
        mContext = mActivity;
        mCompatActivity = (AppCompatActivity) mActivity;
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayoutInflater = getLayoutInflater(savedInstanceState);
        mView = getLayout(inflater, container, savedInstanceState);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsViewCreate = true;
        // 绑定依赖注入框架
        ButterKnife.bind(this, mView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCreateFragment(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 初始化标题栏
     */
    protected Toolbar initToolbar(String title){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCompatActivity.setSupportActionBar(mToolbar);
        ActionBar actionBar = mCompatActivity.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        // 设置标题
        if(!TextUtils.isEmpty(title)){
            mToolbar.setTitle(title);
        }
        // 设置左侧图标
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        return mToolbar;
    }

    /**
     * 设置标题栏标题
     */
    public void setTitle(String title){
        if(mToolbar != null){
            mToolbar.setTitle(title);
        }
    }

    /**
     * 设置标题栏标题颜色
     */
    public void setTitleTextColor(int id) {
        if (mToolbar != null) {
            mToolbar.setTitleTextColor(id);
        }
    }

    /**
     * 返回事件，默认退出当前 activity
     */
    protected void onBack(){
        finish();
    }

    /**
     * 销毁当前挂载的 Activity
     */
    protected void finish(){
        mActivity.finish();
    }

    /**
     * 查找当前控件
     */
    protected View findViewById(int id){
        return mView.findViewById(id);
    }

    /**
     * 获取 Layout 布局
     */
    public abstract View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisiable = isVisibleToUser;
        if(mIsVisiable && mIsViewCreate){
            onLazyLoadData();
        }
    }

    protected void onLazyLoadData(){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public abstract void onCreateFragment(@Nullable Bundle savedInstanceState);
}

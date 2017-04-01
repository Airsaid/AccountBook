package com.github.airsaid.accountbook.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.util.ActivityManager;
import com.github.airsaid.accountbook.widget.slideback.SlideBackActivity;

import butterknife.ButterKnife;


/**
 * Created by zhouyou on 2016/6/23.
 * Class desc: activity base class
 */
public abstract class BaseActivity extends SlideBackActivity {

    protected Activity mContext;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置 Activity 屏幕方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 隐藏 ActionBar
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.mContext = this;

        // 设置布局
        setContentView(LayoutInflater.from(this).inflate(getLayoutRes(), null));
        // 绑定依赖注入框架
        ButterKnife.bind(this);

        onCreateActivity(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager.getInstance().pushActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityManager.getInstance().popActivity(this);
    }

    /**
     * 初始化标题栏
     */
    public Toolbar initToolbar(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 取消原有左侧标题
            actionBar.setDisplayShowTitleEnabled(false);
        }
        // 设置标题
        if(!TextUtils.isEmpty(title)){
            TextView txtTitle = (TextView) findViewById(R.id.txt_title_title);
            txtTitle.setText(title);
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
            TextView txtTitle = (TextView) findViewById(R.id.txt_title_title);
            txtTitle.setText(title);
        }
    }

    /**
     * 设置标题栏标题颜色
     */
    public void setTitleTextColor(int id) {
        if (mToolbar != null) {
            TextView txtTitle = (TextView) findViewById(R.id.txt_title_title);
            txtTitle.setTextColor(id);
        }
    }

    /**
     * 设置左侧标题
     */
    public void setLeftTitle(String leftTitle){
        if(mToolbar != null && leftTitle != null){
            mToolbar.setNavigationIcon(null);
            TextView txtLeftTitle = (TextView) findViewById(R.id.txt_title_left);
            txtLeftTitle.setVisibility(View.VISIBLE);
            txtLeftTitle.setText(leftTitle);
            txtLeftTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBack();
                }
            });
        }
    }

    /**
     * 设置左侧标题颜色
     */
    public void setLeftTitleColor(int id){
        if(mToolbar != null){
            TextView txtLeftTitle = (TextView) findViewById(R.id.txt_title_left);
            txtLeftTitle.setTextColor(id);
        }
    }

    /**
     * 设置左侧标题字体大小
     */
    public void setLeftTitleSize(float size){
        if(mToolbar != null){
            TextView txtLeftTitle = (TextView) findViewById(R.id.txt_title_left);
            txtLeftTitle.setTextSize(size);
        }
    }

    /**
     * 返回
     */
    protected void onBack() {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            // 点击空白位置 隐藏软键盘
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取布局
     */
    public abstract int getLayoutRes();

    public abstract void onCreateActivity(@Nullable Bundle savedInstanceState);
}

package com.github.airsaid.accountbook.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.account.AccountActivity;
import com.github.airsaid.accountbook.adapter.SimpleFragmentPagerAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private String[] mTitles;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.app_name));

        // 设置侧滑导航
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);

        initTitles();
        initAdapter();
    }

    /**
     * 初始化 Tab 标题数据
     */
    private void initTitles(){
        mTitles = new String[12];
        Calendar c = Calendar.getInstance(Locale.CHINA);
        int year = c.get(Calendar.YEAR);
        for (int i = 1; i <= 12; i++) {
            String month = i < 10 ? "0".concat(String.valueOf(i)) : String.valueOf(i);
            mTitles[i-1]  = year + "年" + month + "月";
        }
    }

    /**
     * 初始化 ViewPager 的 Adapter
     */
    private void initAdapter(){
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(
                getSupportFragmentManager(), mTitles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        // 默认当前月份
        Calendar c = Calendar.getInstance(Locale.CHINA);
        int month = c.get(Calendar.MONTH);
        mViewPager.setCurrentItem(month);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        ToastUtils.show(mContext, "点击了：" + id);
        if (id == R.id.nav_camera) {

        }else if(id == R.id.nav_gallery) {

        }else if(id == R.id.nav_slideshow) {

        }else if(id == R.id.nav_manage) {

        }else if(id == R.id.nav_share) {

        }else if(id == R.id.nav_send) {

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.fab)
    public void onClick() {
        TransitionsHeleper.startActivity(this, AccountActivity.class, mFab);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

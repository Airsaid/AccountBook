package com.github.airsaid.accountbook.mvp.main;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.MainFragmentPagerAdapter;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.CommonDataSource;
import com.github.airsaid.accountbook.data.source.CommonRepository;
import com.github.airsaid.accountbook.mvp.account.AccountActivity;
import com.github.airsaid.accountbook.mvp.books.AccountBooksActivity;
import com.github.airsaid.accountbook.mvp.count.CountActivity;
import com.github.airsaid.accountbook.mvp.user.UserInfoActivity;
import com.github.airsaid.accountbook.ui.activity.MsgActivity;
import com.github.airsaid.accountbook.ui.activity.SettingActivity;
import com.github.airsaid.accountbook.util.AppUtils;
import com.github.airsaid.accountbook.util.ExitAppHelper;
import com.github.airsaid.accountbook.util.ImageLoader;
import com.github.airsaid.accountbook.util.MenuBadgeUtils;
import com.github.airsaid.accountbook.util.SPUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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

    private ImageView mImgIcon;
    private TextView mTxtPhone;
    private TextView mTxtUsername;

    private MenuItem mMenuItem;
    private CommonRepository mCommonRepository;
    private ExitAppHelper mExitAppHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setSlideable(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
        queryUnReadMsg();
    }

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

        initView();
        initAdapter();

        mCommonRepository = new CommonRepository();
        mExitAppHelper = new ExitAppHelper(mContext);
        SPUtils.setSP(mContext, AppConstants.KEY_LAST_UPDATE_TIME, AppUtils.getLastUpdateTime());
    }

    private void initView() {
        View headerView = mNavView.getHeaderView(0);
        mImgIcon = (ImageView) headerView.findViewById(R.id.img_icon);
        mTxtPhone = (TextView) headerView.findViewById(R.id.txt_phone);
        mTxtUsername = (TextView) headerView.findViewById(R.id.txt_username);
        mImgIcon.setOnClickListener(this);
    }

    /**
     * 初始化 ViewPager 的 Adapter
     */
    private void initAdapter(){
        // 初始化标题、Fragment
        String[] titles = new String[12];
        Calendar c = Calendar.getInstance(Locale.CHINA);
        int year = c.get(Calendar.YEAR);
        for (int i = 1; i <= 12; i++) {
            String month = i < 10 ? "0".concat(String.valueOf(i)) : String.valueOf(i);
            titles[i-1]  =  year + "年" + month + "月";
        }
        // 初始化 ViewPager
        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(
                getSupportFragmentManager(), titles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        // 默认当前月份
        int month = c.get(Calendar.MONTH);
        mViewPager.setCurrentItem(month);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mMenuItem = menu.findItem(R.id.menu_title_msg);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_msg:         // 消息
                startActivity(new Intent(mContext, MsgActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 查询未读消息
     */
    private void queryUnReadMsg(){
        mCommonRepository.queryUnReadMsg(UserUtils.getUser(), new CommonDataSource.QueryUnReadMsgCallback() {
            @Override
            public void querySuccess(int count) {
                if(mMenuItem != null){
                    MenuBadgeUtils.update(mContext, mMenuItem, R.mipmap.ic_msg_read, count);
                }
            }

            @Override
            public void queryFail(Error e) {}
        });
    }

    /**
     * 设置用户信息
     */
    private void setUserInfo(){
        User user = UserUtils.getUser();
        if(user != null){
            String username = user.getUsername();
            String phone = user.getMobilePhoneNumber();
            mTxtUsername.setText(username);
            mTxtPhone.setText(phone);
            AVFile avatar = user.getAvatar();
            if(avatar != null){
                ImageLoader.getIns(this).loadIcon(avatar.getUrl(), mImgIcon);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_account_books:// 帐薄
                startActivity(new Intent(mContext, AccountBooksActivity.class));
                break;
            case R.id.nav_count:        // 统计
                startActivity(new Intent(mContext, CountActivity.class));
                break;
            case R.id.nav_feedback:     // 用户反馈
                FeedbackAgent agent = new FeedbackAgent(mContext);
                agent.startDefaultThreadActivity();
                break;
            case R.id.nav_setting:      // 设置
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
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
            if(mExitAppHelper.click()){
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_icon:
                startActivity(new Intent(mContext, UserInfoActivity.class));
                break;
        }
    }
}

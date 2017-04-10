package com.github.airsaid.accountbook.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.mvp.main.MainFragment;
import com.github.airsaid.accountbook.mvp.main.MainPresenter;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;
    private final AccountRepository mRepository;

    public MainFragmentPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
        mRepository = new AccountRepository();
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.EXTRA_POSITION, titles[position]);
        MainFragment fragment = MainFragment.newInstance(bundle);
        new MainPresenter(mRepository, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

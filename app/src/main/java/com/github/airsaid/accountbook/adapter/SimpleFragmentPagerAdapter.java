package com.github.airsaid.accountbook.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.main.MainFragment;
import com.github.airsaid.accountbook.main.MainPresenter;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public SimpleFragmentPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.EXTRA_POSITION, titles[position]);
        MainFragment fragment = MainFragment.newInstance(bundle);
        new MainPresenter(new AccountRepository(), fragment);
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

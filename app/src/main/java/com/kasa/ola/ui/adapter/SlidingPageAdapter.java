package com.kasa.ola.ui.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by edwin on 16-6-12.
 */
public class SlidingPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mLists = new ArrayList<Fragment>();
    private List<String> mTabs = new ArrayList<String>();

    public SlidingPageAdapter(FragmentManager fm, List<Fragment> mItem, List<String> tabs) {
        super(fm);
        this.mLists = mItem;
        this.mTabs = tabs;
    }

    public int getCacheCount() {
        return mLists.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public int getCount() {
        return null == mLists ? 0 : mLists.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null == mTabs ? null : mTabs.get(position);
    }
}


package com.smile.commonlib.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by smile on 2019/4/29.
 */

public class CommonViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> tabTitleList;
    private ArrayList<Fragment> fragmentList;

    public CommonViewPagerAdapter(FragmentManager fm, ArrayList<String> tabTitleList, ArrayList<Fragment> fragmentList) {
        super(fm);
        this.tabTitleList = tabTitleList;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleList.get(position);
    }
}

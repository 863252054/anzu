package com.example.anzu.ui.order;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class OrderPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    public OrderPagerAdapter(List<Fragment> fragmentList, FragmentManager fm) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList != null && !fragmentList.isEmpty() ? fragmentList.size() : 0;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
}
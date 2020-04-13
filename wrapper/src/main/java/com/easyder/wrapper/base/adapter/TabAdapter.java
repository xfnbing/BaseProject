package com.easyder.wrapper.base.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;


import com.easyder.wrapper.base.view.WrapperMvpFragment;

import java.util.List;

/**
 * Auther:  winds
 * Data:    2017/7/26
 * Version: 1.0
 * Desc:
 */

public class TabAdapter extends FragmentPagerAdapter {
    private int position;

    private List<WrapperMvpFragment> list;

    public TabAdapter(FragmentManager fm, List<WrapperMvpFragment> list) {
        super(fm);
        this.list = list;
    }

    public WrapperMvpFragment getCurrentFragment() {
        return list.get(position);
    }

    public int getCurrentPosition() {
        return position;
    }

    @Override
    public Fragment getItem(int i) {
        return list.get(i);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        this.position = position;
    }


    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}

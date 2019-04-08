package com.wys.wysplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wys.wysplayer.ui.fragment.BaseFragment;

import java.util.List;

/**
 * create by wys
 * on2019/4/8,19:22
 * Emial : 424126544@qq.com
 * 适配主页pagerAdapter
 */
public class MPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mList;

    public MPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }


}

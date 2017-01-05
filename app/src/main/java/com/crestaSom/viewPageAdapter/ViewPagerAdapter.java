package com.crestaSom.viewPageAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/3/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments=new ArrayList<>();
    List<String> tabTitles=new ArrayList<>();
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public  void addFragments(Fragment fragment,String tabTitle){
        fragments.add(fragment);
        tabTitles.add(tabTitle);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}

package com.dealspok.dealspok.adapter;

/**
 * Created by Umi on 28.08.2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dealspok.dealspok.fragment.Deals;
import com.dealspok.dealspok.fragment.DealsHeute;
import com.dealspok.dealspok.fragment.Favourite;
import com.dealspok.dealspok.fragment.Gutscheine;
import com.dealspok.dealspok.fragment.OnlineDeals;
import com.dealspok.dealspok.fragment.Shopping;

public class CustomFragmentPageAdapter extends FragmentPagerAdapter{
    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 6;
    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Gutscheine();
            case 1:
                return new Deals();
            case 2:
                return new OnlineDeals();
            case 3:
                return new Shopping();
            case 4:
                return new DealsHeute();
            case 5:
                return new Favourite();
        }
        return null;
    }
    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Gutscheine";
            case 1:
                return "Deals";
            case 2:
                return "Online Deals";
            case 3:
                return "Shopping";
            case 4:
                return "Deals Heute";
            case 5:
                return "Favoriten";
        }
        return null;
    }
}
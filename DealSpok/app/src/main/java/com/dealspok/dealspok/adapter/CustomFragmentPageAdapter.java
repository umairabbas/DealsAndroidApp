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

public class CustomFragmentPageAdapter extends FragmentPagerAdapter{
    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 8;
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
                return new DealsHeute();
            case 3:
                return new OnlineDeals();
            case 4:
                return new OnlineDeals();
            case 5:
                return new OnlineDeals();
            case 6:
                return new OnlineDeals();
            case 7:
                return new OnlineDeals();
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
                return "Deals Heute";
            case 3:
                return "Online Deals";
            case 4:
                return "Shopping";
            case 5:
                return "Favouriten";
            case 6:
                return "Deals Today";
            case 7:
                return "Categories";
        }
        return null;
    }
}
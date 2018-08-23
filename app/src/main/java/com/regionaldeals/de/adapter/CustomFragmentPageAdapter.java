package com.regionaldeals.de.adapter;

/**
 * Created by Umi on 28.08.2017.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.regionaldeals.de.R;
import com.regionaldeals.de.fragment.Deals;
import com.regionaldeals.de.fragment.Favourite;
import com.regionaldeals.de.fragment.Gutscheine;
import com.regionaldeals.de.fragment.OnlineDeals;
import com.regionaldeals.de.fragment.NearBy;

public class CustomFragmentPageAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_COUNT = 5;
    private Context context;

    public CustomFragmentPageAdapter(FragmentManager fm, Context con) {
        super(fm);
        context = con;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Gutscheine();
            case 1:
                return new Deals();
            case 2:
                return new OnlineDeals();
            case 3:
                return new NearBy();
            case 4:
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
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.gutscheine);
            case 1:
                return context.getResources().getString(R.string.deals);
            case 2:
                return context.getResources().getString(R.string.online_deals);
            case 3:
                return context.getResources().getString(R.string.nearby);
            case 4:
                return context.getResources().getString(R.string.favouriten);
        }
        return null;
    }
}
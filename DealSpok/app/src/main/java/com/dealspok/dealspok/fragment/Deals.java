package com.dealspok.dealspok.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.adapter.CustomFragmentPageAdapter;

/**
 * Created by Umi on 28.08.2017.
 */

public class Deals extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals, container, false);

        return view;
    }
}
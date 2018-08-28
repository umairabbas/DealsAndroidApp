package com.regionaldeals.de.adapter

/**
 * Created by Umi on 28.08.2017.
 */

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.regionaldeals.de.R
import com.regionaldeals.de.fragment.Deals
import com.regionaldeals.de.fragment.Favourite
import com.regionaldeals.de.fragment.Gutscheine
import com.regionaldeals.de.fragment.OnlineDeals
import com.regionaldeals.de.fragment.NearBy

class CustomFragmentPageAdapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return Gutscheine()
            1 -> return Deals()
            2 -> return OnlineDeals()
            3 -> return NearBy()
            4 -> return Favourite()
        }
        return null
    }

    override fun getCount(): Int {
        return FRAGMENT_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return context.resources.getString(R.string.gutscheine)
            1 -> return context.resources.getString(R.string.deals)
            2 -> return context.resources.getString(R.string.online_deals)
            3 -> return context.resources.getString(R.string.nearby)
            4 -> return context.resources.getString(R.string.favouriten)
        }
        return null
    }

    companion object {
        private val FRAGMENT_COUNT = 5
    }
}
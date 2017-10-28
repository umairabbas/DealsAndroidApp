package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.Shop;

import java.util.List;

/**
 * Created by Umi on 28.10.2017.
 */

public class ShopAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Shop> mDataSource;

    public ShopAdapter(Context context, List<Shop> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.shop_list_row, parent, false);
        // Get title element
        TextView serialText =
                (TextView) rowView.findViewById(R.id.serial);
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.title);

        Shop recipe = (Shop) getItem(position);

// 2
        serialText.setText(Integer.toString(recipe.getShopId()));
        subtitleTextView.setText(recipe.getShopName());

        return rowView;
    }
}

package com.regionaldeals.de.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.regionaldeals.de.AddShopActivity;
import com.regionaldeals.de.R;
import com.regionaldeals.de.entities.Shop;

import java.util.List;

/**
 * Created by Umi on 30.01.2018.
 */

public class NearbyAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Shop> mDataSource;

    public NearbyAdapter(Context context, List<Shop> items) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get view for row item
        final View rowView = mInflater.inflate(R.layout.nearby_list_row, parent, false);
        Shop recipe = (Shop) getItem(position);

        TextView Category =
                (TextView) rowView.findViewById(R.id.title);
        TextView Name =
                (TextView) rowView.findViewById(R.id.title2);
        TextView Contact =
                (TextView) rowView.findViewById(R.id.title3);
        TextView Address =
                (TextView) rowView.findViewById(R.id.title4);
        TextView desc =
                (TextView) rowView.findViewById(R.id.title5);

//        serialText.setText(Integer.toString(recipe.getShopId()));
        Name.setText(recipe.getShopName());
        Address.setText(recipe.getShopAddress());
        Contact.setText(recipe.getShopContact());
        desc.setText(recipe.getShopDetails());
        if(recipe.getShopCategories()!=null) {
            Category.setText(recipe.getShopCategories());
        }


        return rowView;
    }
}
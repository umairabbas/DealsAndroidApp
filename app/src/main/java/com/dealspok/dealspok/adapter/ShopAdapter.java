package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dealspok.dealspok.AddShopActivity;
import com.dealspok.dealspok.DealsDetail;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get view for row item
        final View rowView = mInflater.inflate(R.layout.shop_list_row, parent, false);
        // Get title element
        TextView serialText =
                (TextView) rowView.findViewById(R.id.serial);
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.title);

        Shop recipe = (Shop) getItem(position);

// 2
        serialText.setText(Integer.toString(recipe.getShopId()));
        subtitleTextView.setText(recipe.getShopName());

        rowView.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = position;
               Shop s = mDataSource.get(i);
                Context context = v.getContext();
                Intent intent = new Intent(context, AddShopActivity.class);
                intent.putExtra("EXTRA_SHOP_OBJ", s);
                context.startActivity(intent);
            }}));

        return rowView;
    }
}

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
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.title);
        TextView detailTextView =
                (TextView) rowView.findViewById(R.id.title2);

        Shop recipe = (Shop) getItem(position);

        subtitleTextView.setText(recipe.getShopName());
        detailTextView.setText(recipe.getShopAddress());


        rowView.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = position;
                Shop s = mDataSource.get(i);
                Context context = v.getContext();
                Intent intent = new Intent(context, AddShopActivity.class);
                intent.putExtra("EXTRA_SHOP_OBJ", s);
                context.startActivity(intent);
            }
        }));

        return rowView;
    }
}

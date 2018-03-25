package com.regionaldeals.de.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.regionaldeals.de.R;
import com.regionaldeals.de.entities.NotificationsObject;
import com.regionaldeals.de.entities.Shop;

import java.util.List;
import java.util.Locale;

/**
 * Created by Umi on 10.03.2018.
 */

public class NotificationsAdapter  extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private List<NotificationsObject> mDataSource;
    private Activity act;

    public NotificationsAdapter(Context context, List<NotificationsObject> items) {
        mContext = context;
        act = (Activity) context;
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
        final View rowView = mInflater.inflate(R.layout.notifications_list_row, parent, false);
        if(mDataSource.size()>0) {
            NotificationsObject recipe = (NotificationsObject) getItem(position);

//        TextView Category =
//                (TextView) rowView.findViewById(R.id.title);
            TextView Name =
                    (TextView) rowView.findViewById(R.id.titleShop);
            TextView Address =
                    (TextView) rowView.findViewById(R.id.addressShop);
            TextView desc =
                    (TextView) rowView.findViewById(R.id.shopDesc);

            //Contact.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_24, 0, 0, 0);
            //Address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_24, 0, 0, 0);
            //desc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.details_24, 0, 0, 0);

            Name.setText(recipe.getNotificationDetails());
            Address.setText( recipe.getGutscheineObject().getShop().getShopName() + ", " + recipe.getGutscheineObject().getShop().getShopAddress());
            desc.setText("CODE: "+ recipe.getGutscheineObject().getGutscheinCode());

            desc.setTextColor(Color.RED);

            final float lat = Float.parseFloat(recipe.getGutscheineObject().getShop().getShopLocationLat());
            final float lng = Float.parseFloat(recipe.getGutscheineObject().getShop().getShopLocationLong());
            final String address = recipe.getGutscheineObject().getShop().getShopAddress();

            Address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = String.format(Locale.GERMANY, "geo:%f,%f?q=" + address, lat, lng);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    mContext.startActivity(intent);
                }
            });

        }
        return rowView;
    }
}
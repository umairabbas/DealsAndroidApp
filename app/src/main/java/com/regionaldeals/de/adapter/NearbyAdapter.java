package com.regionaldeals.de.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.regionaldeals.de.R;
import com.regionaldeals.de.entities.Shop;

import java.util.List;
import java.util.Locale;

/**
 * Created by Umi on 30.01.2018.
 */

public class NearbyAdapter extends BaseAdapter implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Shop> mDataSource;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 12;
    private Activity act;

    public NearbyAdapter(Context context, List<Shop> items) {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone(contact);
                }
            }
        }
    }

    private void callPhone(String contact) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contact));
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            mContext.startActivity(intent);
        }
    }

    private String contact;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get view for row item
        final View rowView = mInflater.inflate(R.layout.nearby_list_row, parent, false);
        if (mDataSource.size() > 0) {
            Shop recipe = (Shop) getItem(position);

//        TextView Category =
//                (TextView) rowView.findViewById(R.id.title);
            TextView Name =
                    (TextView) rowView.findViewById(R.id.titleShop);
            TextView Contact =
                    (TextView) rowView.findViewById(R.id.contactShop);
            TextView Address =
                    (TextView) rowView.findViewById(R.id.addressShop);
            TextView desc =
                    (TextView) rowView.findViewById(R.id.shopDesc);

            Contact.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_24, 0, 0, 0);
            Address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_24, 0, 0, 0);
            desc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.details_24, 0, 0, 0);

            Name.setText(recipe.getShopName());
            Address.setText(recipe.getShopAddress());
            Contact.setText(recipe.getShopContact());
            desc.setText(recipe.getShopDetails());
//        if(recipe.getShopCategories()!=null) {
//            Category.setText(recipe.getShopCategories());
//        }
            contact = recipe.getShopContact();
            final float lat = Float.parseFloat(recipe.getShopLocationLat());
            final float lng = Float.parseFloat(recipe.getShopLocationLong());
            final String address = recipe.getShopAddress();

            Contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(act,
                                new String[]{android.Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        return;
                    } else {
                        callPhone(contact);
                    }
                }
            });
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
package com.dealspok.dealspok;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.dealspok.dealspok.R;

/**
 * Created by Umi on 13.09.2017.
 */

public class DealsDetail extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deals_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.deal_detail_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));

        int postion = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Resources resources = getResources();
        //String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle("Deal Title");//places[postion % places.length]);

        //String[] placeDetails = resources.getStringArray(R.array.place_details);
        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText("Deal Detail");//placeDetails[postion % placeDetails.length]);

        //String[] placeLocations = resources.getStringArray(R.array.place_locations);
        TextView placeLocation =  (TextView) findViewById(R.id.place_location);
        placeLocation.setText("Deal Location");//(placeLocations[postion % placeLocations.length]);

        //TypedArray placePictures = resources.obtainTypedArray(R.array.places_picture);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        placePicutre.setImageResource(R.drawable.adele);//(placePictures.getDrawable(postion % placePictures.length()));

        //placePictures.recycle();
    }
}
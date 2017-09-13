package com.dealspok.dealspok;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

/**
 * Created by Umi on 13.09.2017.
 */

public class DealsDetail extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    public static final String EXTRA_POSITION = "position";
    private GoogleMap mMap;
    private LatLng LOCATIONCORDINATE = new LatLng(-50.7753, 6.0839);
    String title = "";
    private Marker mPerth;

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

        Intent intent = getIntent();
        int postion = intent.getIntExtra(EXTRA_POSITION, 0);

        title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String coverUrl = intent.getStringExtra("coverImg");
        Double locationLat = intent.getDoubleExtra("lat", -50.7753);
        Double locationLong = intent.getDoubleExtra("long", 6.0839);

        LOCATIONCORDINATE = new LatLng(locationLat, locationLong);
        //Resources resources = getResources();
        //String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle(title);//places[postion % places.length]);

        //String[] placeDetails = resources.getStringArray(R.array.place_details);
        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText(desc);//placeDetails[postion % placeDetails.length]);

        //TypedArray placePictures = resources.obtainTypedArray(R.array.places_picture);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);

        Picasso.with(this).load(coverUrl).into(placePicutre);
        //placePicutre.setImageResource(R.drawable.adele);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //set height of map
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = height/2;
        mapFragment.getView().setLayoutParams(params);
    }

    /** Called when the map is ready. */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Add some markers to the map, and add a data object to each marker.
        mPerth = mMap.addMarker(new MarkerOptions()
                .position(LOCATIONCORDINATE)
                .title(title));
        mPerth.setTag(0);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATIONCORDINATE));
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(14.0f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}
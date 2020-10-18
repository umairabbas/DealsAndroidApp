package com.regionaldeals.de;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.regionaldeals.de.entities.DealObject;
import com.regionaldeals.de.entities.GutscheineObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Umi on 13.09.2017.
 */

public class DealsDetail extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback, BaseSliderView.OnSliderClickListener {

    public static final String EXTRA_POSITION = "position";
    private GoogleMap mMap;
    private LatLng LOCATIONCORDINATE = new LatLng(-50.7753, 6.0839);
    String title = "";
    private Marker mPerth;
    private SliderLayout mDemoSlider;

    //For deal deletion
    private Button delBtn;
    private ProgressDialog progressDialog;
    private Context context;
    private String message;
    private Boolean isSuccess = false;
    private String URL_DealDel = "/web/deals/deactivate";
    private int dealId = -1;
    private int shopId = -1;
    private String userId = "";

    //for gutscheien del
    private Boolean isGutschein = false;
    private String dealURL = "";

    private List<String> images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deals_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.deal_detail_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        delBtn = (Button) findViewById(R.id.btn_deal_del);
        TextView exp = (TextView) findViewById(R.id.exp_date);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));
        context = this;
        Intent intent = getIntent();
        int postion = intent.getIntExtra(EXTRA_POSITION, 0);

        TextView placeUrl = (TextView) findViewById(R.id.place_url);
        TextView titleUrl = (TextView) findViewById(R.id.urlTitle);

        if (intent.hasExtra("currDeal")) {
            DealObject currDeal = (DealObject) getIntent().getSerializableExtra("currDeal");
            dealId = currDeal.getDealId();
            shopId = currDeal.getShop().getShopId();
            exp.setText(DateFormat.format("dd/MM/yyyy", new Date(currDeal.getDateExpire())).toString());
            if (currDeal.getDealUrl() != null)
                if (!currDeal.getDealUrl().isEmpty()) {
                    dealURL = currDeal.getDealUrl();
                    titleUrl.setVisibility(View.VISIBLE);
                    placeUrl.setText(dealURL);
                    placeUrl.setVisibility(View.VISIBLE);
                    placeUrl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!dealURL.startsWith("http://") && !dealURL.startsWith("https://")) {
                                dealURL = "http://" + dealURL;
                            }
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dealURL));
                            startActivity(browserIntent);
                        }
                    });
                }
        }

        if (intent.hasExtra("currGut")) {
            GutscheineObject currGut = (GutscheineObject) getIntent().getSerializableExtra("currGut");
            dealId = currGut.getGutscheinId();
            shopId = currGut.getShop().getShopId();
            exp.setText(DateFormat.format("dd/MM/yyyy", new Date(currGut.getExpiryDate())).toString());
        }

        title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String coverUrl = intent.getStringExtra("coverImg");
        Double locationLat = intent.getDoubleExtra("lat", -50.7753);
        Double locationLong = intent.getDoubleExtra("long", 6.0839);
        int imgCount = intent.getIntExtra("imgCount", 1);
        String contact = "";
        if (intent.hasExtra("contact")) {
            contact = intent.getStringExtra("contact");
        }
        String shopName = "";
        if (intent.hasExtra("shopName")) {
            shopName = intent.getStringExtra("shopName");
        }
        String address = "";
        if (intent.hasExtra("address")) {
            address = intent.getStringExtra("address");
        }

        Boolean enableDeleteBtn = false;
        if (intent.hasExtra("deleteEnable")) {
            enableDeleteBtn = intent.getBooleanExtra("deleteEnable", false);
        }
        if (intent.hasExtra("isGutschein")) {
            isGutschein = intent.getBooleanExtra("isGutschein", false);
        }


        if (enableDeleteBtn) {
            delBtn.setVisibility(View.VISIBLE);
            SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
            String restoredUser = prefs.getString("userObject", null);
            try {
                if (restoredUser != null) {
                    JSONObject obj = new JSONObject(restoredUser);
                    userId = obj.getString("userId");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable t) {
            }
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog = new ProgressDialog(context,
                            R.style.ThemeOverlay_AppCompat_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Removing...");
                    progressDialog.show();
                    delBtn.setEnabled(false);
                    new DealDeleteCall().execute();
                }
            });
        }

        TextView shopNameText = (TextView) findViewById(R.id.shopName);
        shopNameText.setText(shopName);

        TextView contactText = (TextView) findViewById(R.id.shop_contact);
        contactText.setText(contact);

        TextView AddressText = (TextView) findViewById(R.id.shop_address);
        AddressText.setText(address);


        LOCATIONCORDINATE = new LatLng(locationLat, locationLong);
        collapsingToolbar.setTitle(title);

        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText(desc);

        //ImageView placePicutre = (ImageView) findViewById(R.id.image);
        //Picasso.with(this).load(coverUrl).into(placePicutre);
        mDemoSlider = (SliderLayout) findViewById(R.id.image);

        HashMap<String, String> url_maps = new HashMap<String, String>();
        String[] imgTitle = {
                " ",
                "  ",
                "   ",
                "    ",
                "     ",
        };
        for (int a = 1; a <= imgCount; a++) {
            url_maps.put(imgTitle[a - 1], coverUrl + Integer.toString(a) + "&res=470x320");
        }

        images = new ArrayList<>();
        for (String name : url_maps.keySet()) {
            images.add(url_maps.get(name));
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setOnSliderClickListener(this)
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            mDemoSlider.addSlider(textSliderView);
            mDemoSlider.stopAutoCycle();

        }

        //mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        if (imgCount == 1) {
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(600000);
        }

        Fresco.initialize(this);

//        for (int i = 0; i < images.size(); i++) {
//            BaseSliderView baseSliderView = new BaseSliderView(context) {
//                @Override
//                public View getView() {
//                    View v = LayoutInflater.from(getContext()).inflate(R.layout.image_slider, null);
//                    ImageView target = (ImageView) v.findViewById(R.id.daimajia_slider_image);
//                    bindEventAndShow(v, target);
//                    return v;
//                }
//            };
//
//            baseSliderView.image(images.get(i));
//            baseSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                @Override
//                public void onSliderClick(BaseSliderView slider) {
//                    new ImageViewer.Builder(context, images)
//                            .setStartPosition(0)
//                            .show();
//                    Log.d("MyActivity", "index selected:" + mDemoSlider.getCurrentPosition());
//                }
//            });
//        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //set height of map
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = height / 2;
        mapFragment.getView().setLayoutParams(params);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
//        new ImageViewer.Builder(context, images)
//                .setStartPosition(0)
//                .show();
    }

    /**
     * Called when the map is ready.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        CameraPosition cp = new CameraPosition.Builder()
                .target(LOCATIONCORDINATE)      // Sets the center of the map to Mountain View
                .zoom(11.0f)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

        // Add some markers to the map, and add a data object to each marker.
        mPerth = mMap.addMarker(new MarkerOptions()
                .position(LOCATIONCORDINATE)
                .title(title));
        mPerth.setTag(0);

        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(14.0f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class DealDeleteCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                message = "";
                URL url;
                if (isGutschein) {
                    URL_DealDel = "/web/gutschein/deactivate";
                    url = new URL(getString(R.string.apiUrl) + URL_DealDel + "?gutscheinid=" + dealId + "&userid=" + userId +
                            "&shopid=" + shopId);
                } else {
                    url = new URL(getString(R.string.apiUrl) + URL_DealDel + "?dealid=" + dealId + "&userid=" + userId +
                            "&shopid=" + shopId);
                }
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

                String response = conn.getResponseMessage();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer res = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                JSONObject jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                if (message.equals(getString(R.string.DEALS_REMOVE_OK)) || message.equals(getString(R.string.GUTSCHEIN_REMOVE_OK))) {
                    isSuccess = true;
                } else if (message.equals(getString(R.string.DEALS_REMOVE_ERR)) || message.equals(getString(R.string.GUTSCHEIN_REMOVE_ERR))) {
                    isSuccess = false;
                    message = "Cannot remove deal";
                } else {
                    isSuccess = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            delBtn.setEnabled(true);
            runOnUiThread(new Runnable() {
                public void run() {
                    if (isSuccess) {
                        Toast.makeText(context, "Deal Removed\n", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(context, "Failed\n" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
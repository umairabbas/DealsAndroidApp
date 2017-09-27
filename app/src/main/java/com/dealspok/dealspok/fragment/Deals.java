package com.dealspok.dealspok.fragment;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.Utils.JSONParser;
import com.dealspok.dealspok.adapter.DealsAdapter;
import com.dealspok.dealspok.entities.DealObject;
import com.dealspok.dealspok.entities.DealsObject;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Umi on 28.08.2017.
 */

public class Deals extends Fragment {

    private List<DealObject> deals;
    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
    // albums JSON url
    private static final String URL_ALBUMS = "http://82.165.160.225/dealspock/api/deals/list";

    // ALL JSON node names
    private static final String TAG_ID = "dealId";
    private static final String TAG_TITLE = "dealTitle";
    private static final String TAG_DESC = "dealDescription";
    private static final String TAG_IMG = "dealImageUrl";
    private static final String TAG_CREATED = "dateCreated";
    private static final String TAG_PUBLISH = "datePublished";
    private static final String TAG_EXP = "dateExpire";
    // albums JSONArray
    private JSONArray dealArr = null;

    private RecyclerView songRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);

        getActivity().setTitle("DealSpok");
        songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        deals = new ArrayList<>();

        // Loading Albums JSON in Background Thread
        new LoadDeals().execute();

        return view;
    }

    Location createNewLocation(double latitude, double longitude) {
        Location location = new Location("dummyprovider");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }
    public List<DealsObject> getData() {
        List<DealsObject> deals = new ArrayList<>();

//        deals.add(new DealsObject(3, Uri.parse("http://www.couponforshopping.com/wp-content/uploads/2014/12/hm-coupon.png"),
//                "20% H&M", "Sale on Jeans, shirts, ladies shoes, Jackets. Valid only till 2017", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7746461,6.0846929), created, created));
//        deals.add(new DealsObject(2, Uri.parse("http://worldfranchise.eu/sites/default/files/franchises/photos/rcl_backwerk-fassade_02_a.jpg"),
//                "30% Discount BackWerk", "Free Coffee in Morning. We offer amazing environment. Hot tea + buffet in very cheap price also available", "+49 241 94379920", createNewLocation(50.7803965,6.0775336), created, created));
//        deals.add(new DealsObject(1, Uri.parse("http://2.bp.blogspot.com/-Mbq5kmMtrgI/T44zS6BzQ6I/AAAAAAAAA20/QjajhABxexU/s1600/458827_10151501202440023_142518590022_23600640_1232321177_o.jpg"),
//                "20% Rabatt Mcdonalds", "Promotion period: 18 April – 18 May and while stocks last. Available from 11am – 4am, EVERYDAY!.\nWe offer amazing environment. Hot tea + buffet in very cheap price also available\n Kindly make booking before coming, we are expecting a lot of customers", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7752744,6.0864533), created, created));
//        deals.add(new DealsObject(2, Uri.parse("http://worldfranchise.eu/sites/default/files/franchises/photos/rcl_backwerk-fassade_02_a.jpg"),
//                "30% Discount BackWerk", "Free Coffee in Morning. We offer amazing environment. Hot tea + buffet in very cheap price also available", "+49 241 94379920", createNewLocation(50.7803965,6.0775336), created, created));
//        deals.add(new DealsObject(3, Uri.parse("http://www.couponforshopping.com/wp-content/uploads/2014/12/hm-coupon.png"),
//                "20% H&M", "Sale on Jeans, shirts, ladies shoes, Jackets. Valid only till 2017", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7746461,6.0846929), created, created));
//        deals.add(new DealsObject(1, Uri.parse("http://2.bp.blogspot.com/-Mbq5kmMtrgI/T44zS6BzQ6I/AAAAAAAAA20/QjajhABxexU/s1600/458827_10151501202440023_142518590022_23600640_1232321177_o.jpg"),
//                "20% Rabatt Mcdonalds", "Promotion period: 18 April – 18 May and while stocks last. Available from 11am – 4am, EVERYDAY!.\nWe offer amazing environment. Hot tea + buffet in very cheap price also available\n Kindly make booking before coming, we are expecting a lot of customers", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7752744,6.0864533), created, created));

        return deals;
    }

    /**
     * Background Async Task to Load all Albums by making http request
     * */
    class LoadDeals extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(AlbumsActivity.this);
//            pDialog.setMessage("Listing Albums ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        /**
         * getting Albums JSON
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL_ALBUMS, "GET",
                    params);

            // Check your log cat for JSON reponse
            Log.d("Albums JSON: ", "> " + json);

            try {
                dealArr = new JSONArray(json);

                if (dealArr != null) {
                    // looping through All albums
                    for (int i = 0; i < dealArr.length(); i++) {
                        JSONObject c = dealArr.getJSONObject(i);
                        // Storing each json item values in variable
                        int id = c.getInt(TAG_ID);
                        String name = c.getString(TAG_TITLE);
                        String desc = c.getString(TAG_DESC);
                        String img = c.getString(TAG_IMG);
                        long created = c.getLong(TAG_CREATED);
                        long publish = c.getLong(TAG_PUBLISH);
                        long expiry = c.getLong(TAG_EXP);

                        deals.add(new DealObject(id, name, img, desc, created, publish, expiry));

                    }
                } else {
                    Log.d("Deals: ", "null");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            //pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    DealsAdapter mAdapter = new DealsAdapter(getActivity(), deals);
                    songRecyclerView.setAdapter(mAdapter);
                }
            });

        }
    }
}

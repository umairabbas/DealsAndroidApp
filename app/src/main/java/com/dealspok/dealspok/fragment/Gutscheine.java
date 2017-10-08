package com.dealspok.dealspok.fragment;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.Utils.JSONParser;
import com.dealspok.dealspok.adapter.DealsAdapter;
import com.dealspok.dealspok.adapter.GutscheineAdapter;
import com.dealspok.dealspok.entities.DealObject;
import com.dealspok.dealspok.entities.GutscheineObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umi on 28.08.2017.
 */

public class Gutscheine extends Fragment {

    private List<GutscheineObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Deals = "/mobile/api/gutschein/list";
    private JSONArray dealArr = null;
    private RecyclerView songRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        getActivity().setTitle("Regional Deals");
        context = getContext();
        songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        deals = new ArrayList<>();
        // Loading JSON in Background Thread
        new Gutscheine.LoadDeals().execute();

        return view;
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

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Deals, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                dealArr = new JSONArray(json);

                if (dealArr != null) {
                    for (int i = 0; i < dealArr.length(); i++) {
                        JSONObject c = dealArr.getJSONObject(i);
                        Gson gson = new GsonBuilder().create();
                        GutscheineObject newDeal = gson.fromJson(c.toString(), GutscheineObject.class);
                        deals.add(newDeal);
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
                    GutscheineAdapter mAdapter = new GutscheineAdapter(getActivity(), deals);
                    songRecyclerView.setAdapter(mAdapter);
                }
            });

        }
    }

//    public List<GutscheineObject> getTestData() {
//        Date created = new Date();
//        List<GutscheineObject> deals = new ArrayList<>();
//        deals.add(new GutscheineObject(1, Uri.parse("http://2.bp.blogspot.com/-Mbq5kmMtrgI/T44zS6BzQ6I/AAAAAAAAA20/QjajhABxexU/s1600/458827_10151501202440023_142518590022_23600640_1232321177_o.jpg"),
//                "20% Rabatt Mcdonalds", "Promotion period: 18 April – 18 May and while stocks last. Available from 11am – 4am, EVERYDAY!.\nWe offer amazing environment. Hot tea + buffet in very cheap price also available.\nKindly make booking before coming, we are expecting a lot of customers", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7752744,6.0864533), created, created));
//        deals.add(new GutscheineObject(2, Uri.parse("http://worldfranchise.eu/sites/default/files/franchises/photos/rcl_backwerk-fassade_02_a.jpg"),
//                "30% Discount BackWerk", "Free Coffee in Morning. We offer amazing environment. Hot tea + buffet in very cheap price also available", "+49 241 94379920", createNewLocation(50.7803965,6.0775336), created, created));
//        deals.add(new GutscheineObject(3, Uri.parse("http://www.couponforshopping.com/wp-content/uploads/2014/12/hm-coupon.png"),
//                "20% H&M", "Sale on Jeans, shirts, ladies shoes, Jackets. Valid only till 2017", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7746461,6.0846929), created, created));
//        deals.add(new GutscheineObject(1, Uri.parse("http://2.bp.blogspot.com/-Mbq5kmMtrgI/T44zS6BzQ6I/AAAAAAAAA20/QjajhABxexU/s1600/458827_10151501202440023_142518590022_23600640_1232321177_o.jpg"),
//                "20% Rabatt Mcdonalds", "Promotion period: 18 April – 18 May and while stocks last. Available from 11am – 4am, EVERYDAY!", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7752744,6.0864533), created, created));
//        deals.add(new GutscheineObject(2, Uri.parse("http://worldfranchise.eu/sites/default/files/franchises/photos/rcl_backwerk-fassade_02_a.jpg"),
//                "30% Discount BackWerk", "Free Coffee in Morning. We offer amazing environment. Hot tea + buffet in very cheap price also available", "+49 241 94379920", createNewLocation(50.7803965,6.0775336), created, created));
//        deals.add(new GutscheineObject(3, Uri.parse("http://www.couponforshopping.com/wp-content/uploads/2014/12/hm-coupon.png"),
//                "20% H&M", "Sale on Jeans, shirts, ladies shoes, Jackets. Valid only till 2017", "Tel: 089 / 7 85 94 - 413", createNewLocation(50.7746461,6.0846929), created, created));
//
//        return deals;
//    }
}

package com.regionaldeals.de.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.DoubleNameValuePair;
import com.regionaldeals.de.Utils.IntNameValuePair;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.adapter.OnlineDealsAdapter;
import com.regionaldeals.de.entities.DealObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.regionaldeals.de.Constants.LOCATION_KEY;
import static com.regionaldeals.de.Constants.USER_OBJECT_KEY;

/**
 * Created by Umi on 28.08.2017.
 */

public class OnlineDeals extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private List<DealObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Online = "/mobile/api/deals/list";
    private JSONArray dealArr = null;
    private RecyclerView songRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId = "";
    private Double locationLat = 50.781203;
    private Double locationLng = 6.078068;
    private OnlineDealsAdapter mAdapter;
    private int maxDistance = 50;
    private MyReceiver myReceiver;
    private IntentFilter filter;

    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            maxDistance = intent.getIntExtra("distance", maxDistance);
            new OnlineDeals.LoadDeals().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        context = getContext();
        getActivity().setTitle(getResources().getString(R.string.headerText));
        songRecyclerView = (RecyclerView) view.findViewById(R.id.rV_gutschein);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        deals = new ArrayList<>();

        mAdapter = new OnlineDealsAdapter(getActivity(), deals);
        songRecyclerView.setAdapter(mAdapter);
        // Loading JSON in Background Thread
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new OnlineDeals.LoadDeals().execute();
                    }
                }
        );

        locationLat = ((Main) getParentFragment()).getLat();
        locationLng = ((Main) getParentFragment()).getLng();

        String restoredText = SharedPreferenceUtils.getInstance(getActivity()).getStringValue(LOCATION_KEY, null);
        String restoredUser = SharedPreferenceUtils.getInstance(getActivity()).getStringValue(USER_OBJECT_KEY, null);

        try {
            if (locationLat == 0.0 || locationLng == 0.0) {
                if (restoredText != null) {
                    JSONObject obj = new JSONObject(restoredText);
                    String Lat = obj.getString("lat");
                    String Lng = obj.getString("lng");
                    if (!Lat.isEmpty() && !Lng.isEmpty()) {
                        locationLat = Double.parseDouble(Lat);
                        locationLng = Double.parseDouble(Lng);
                    }
                }
            }
            if (restoredUser != null) {
                JSONObject obj = new JSONObject(restoredUser);
                userId = obj.getString("userId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable t) {
        }

        filter = new IntentFilter("BroadcastReceiver");
        myReceiver = new MyReceiver();

        return view;
    }

    @Override
    public void onPause() {
        context.unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(myReceiver, filter);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new LoadDeals().execute();
    }

    /**
     * Background Async Task to Load all Albums by making http request
     */
    class LoadDeals extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
//            pDialog = new ProgressDialog(AlbumsActivity.this);
//            pDialog.setMessage("Listing Albums ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("dealtype", "TYPE_ONLINE_DEALS"));
            params.add(new DoubleNameValuePair("lat", locationLat));
            params.add(new DoubleNameValuePair("long", locationLng));
            params.add(new BasicNameValuePair("userid", userId));
            params.add(new IntNameValuePair("radius", maxDistance * 1000));
            params.add(new NameValuePair() {
                @Override
                public String getName() {
                    return "dealtype";
                }

                @Override
                public String getValue() {
                    return "online";
                }
            });

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Online, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                dealArr = new JSONArray(json);
                deals.clear();
                if (dealArr != null) {
                    for (int i = 0; i < dealArr.length(); i++) {
                        JSONObject c = dealArr.getJSONObject(i);
                        Gson gson = new GsonBuilder().create();
                        DealObject newDeal = gson.fromJson(c.toString(), DealObject.class);
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
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    mAdapter.notifyDataSetChanged();
                }
            });
            swipeRefreshLayout.setRefreshing(false);


        }
    }
}
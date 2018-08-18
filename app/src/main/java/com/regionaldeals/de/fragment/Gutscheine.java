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
import com.regionaldeals.de.MainActivity;
import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.DoubleNameValuePair;
import com.regionaldeals.de.Utils.IntNameValuePair;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.adapter.GutscheineAdapter;
import com.regionaldeals.de.entities.GutscheineObject;

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

public class Gutscheine extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<GutscheineObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Deals = "/mobile/api/gutschein/list";
    private JSONArray dealArr = null;
    private RecyclerView songRecyclerView;
    private GutscheineAdapter mAdapter;
    private Double locationLat = 50.781203;
    private Double locationLng = 6.078068;
    private int maxDistance = 50;
    private String userId = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyReceiver myReceiver;
    private IntentFilter filter;

    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            maxDistance = intent.getIntExtra("distance", maxDistance);
            new Gutscheine.UpdateDeals().execute();
        }
    }

    @Override
    public void onPause() {
        context.unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        getActivity().setTitle(getResources().getString(R.string.headerText));
        context = getContext();
        songRecyclerView = (RecyclerView) view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        filter = new IntentFilter("BroadcastReceiver");
        myReceiver = new MyReceiver();

        deals = new ArrayList<>();
        mAdapter = new GutscheineAdapter(getActivity(), deals, Gutscheine.this);
        songRecyclerView.setAdapter(mAdapter);

        // sfirst time fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new Gutscheine.LoadDeals().execute();
                    }
                }
        );

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(myReceiver, filter);
        //TODO: make seperate shouldrefresh bool for gut and deals etc
        if (((MainActivity) getActivity()).getShouldRefresh()) {
            String restoredUser = SharedPreferenceUtils.getInstance(getActivity()).getStringValue(USER_OBJECT_KEY, null);
            try {
                if (restoredUser != null) {
                    JSONObject obj = new JSONObject(restoredUser);
                    userId = obj.getString("userId");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable t) {
            }
            onRefresh();
            ((MainActivity) this.getActivity()).setShouldRefresh(false);
        }
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new Gutscheine.UpdateDeals().execute();
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
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userId));
            params.add(new DoubleNameValuePair("lat", locationLat));
            params.add(new DoubleNameValuePair("long", locationLng));
            params.add(new IntNameValuePair("radius", maxDistance * 1000));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Deals, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                dealArr = new JSONArray(json);
                deals.clear();
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
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    class UpdateDeals extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userId));
            params.add(new DoubleNameValuePair("lat", locationLat));
            params.add(new DoubleNameValuePair("long", locationLng));
            params.add(new IntNameValuePair("radius", maxDistance * 1000));

            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Deals, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                dealArr = new JSONArray(json);
                deals.clear();
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

        protected void onPostExecute(String file_url) {
            swipeRefreshLayout.setRefreshing(false);
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
package com.regionaldeals.de.fragment;

import android.content.Context;
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
import com.regionaldeals.de.adapter.DealsAdapter;
import com.regionaldeals.de.entities.DealObject;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.regionaldeals.de.Constants.LOCATION_KEY;

/**
 * Created by Umi on 28.08.2017.
 */

public class DealsHeute extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<DealObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Deals = "/mobile/api/deals/today";
    private JSONArray dealArr = null;
    private RecyclerView songRecyclerView;
    private DealsAdapter mAdapter;
    private Double locationLat = 50.781203;
    private Double locationLng = 6.078068;
    private int maxDistance = 50;
    private static final String[] paths = {"1 KM", "2 KM", "5 KM", "10 KM", "50 KM", "100 KM"};
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        context = getContext();
        getActivity().setTitle(getResources().getString(R.string.headerText));
        songRecyclerView = (RecyclerView) view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        String restoredText = SharedPreferenceUtils.getInstance(getActivity()).getStringValue(LOCATION_KEY, null);

        if (restoredText != null) {

            try {
                JSONObject obj = new JSONObject(restoredText);
                String Lat = obj.getString("lat");
                String Lng = obj.getString("lng");
                if (!Lat.isEmpty() && !Lng.isEmpty()) {
                    locationLat = Double.parseDouble(Lat);
                    locationLng = Double.parseDouble(Lng);

                }
            } catch (Throwable t) {
            }

        }

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        deals = new ArrayList<>();


        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new DealsHeute.LoadDeals().execute();
                    }
                }
        );

        return view;
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new UpdateDeals().execute();
    }

    /**
     * Background Async Task to Load all Albums by making http request
     */
    class LoadDeals extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

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
            swipeRefreshLayout.setRefreshing(false);
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    mAdapter = new DealsAdapter(getActivity(), deals);
                    songRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            });
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

            params.add(new DoubleNameValuePair("lat", 50.781203));
            params.add(new DoubleNameValuePair("long", 6.078068));
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

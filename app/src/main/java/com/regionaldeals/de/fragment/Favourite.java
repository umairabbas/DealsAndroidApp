package com.regionaldeals.de.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.regionaldeals.de.MainActivity;
import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.adapter.DealsAdapter;
import com.regionaldeals.de.entities.DealObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Umi on 28.08.2017.
 */

public class Favourite extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<DealObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Fav = "/mobile/api/deals/favourites";
    //private JSONArray dealArr = null;
    private JSONArray OnlinedealsArr = null;
    private JSONArray normalDealsArr = null;
    private RecyclerView songRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId = "";
    private DealsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        context = getContext();
        getActivity().setTitle(getResources().getString(R.string.headerText));
        songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        deals = new ArrayList<>();
        // Loading JSON in Background Thread
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new Favourite.LoadDeals().execute();
                    }
                }
        );

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);
        String restoredUser = prefs.getString("userObject", null);
        try {
            if (restoredText != null) {
                JSONObject obj = new JSONObject(restoredText);
                String Lat = obj.getString("lat");
                String Lng = obj.getString("lng");
                if(!Lat.isEmpty() && !Lng.isEmpty()) {
//                    locationLat = Double.parseDouble(Lat);
//                    locationLng = Double.parseDouble(Lng);
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

        return view;
    }
    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new Favourite.LoadDeals().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(((MainActivity) this.getActivity()).getShouldRefresh()) {
            SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
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
            onRefresh();
            ((MainActivity) this.getActivity()).setShouldRefresh(false);
        }
    }

    /**
     * Background Async Task to Load all Albums by making http request
     * */
    class LoadDeals extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... args) {
            if (userId.isEmpty() || userId == "") {
                return null;
            }
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", userId));
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Fav, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                normalDealsArr = null;
                deals.clear();
                JSONObject data = (JSONObject) new JSONObject(json);
                if(data.getString("message").equals(getString(R.string.FAVOURITES_LIST_OK))) {
                    normalDealsArr = (JSONArray) data.getJSONArray("data");
                    if (normalDealsArr != null) {
                        for (int i = 0; i < normalDealsArr.length(); i++) {
                            JSONObject c = normalDealsArr.getJSONObject(i);
                            Gson gson = new GsonBuilder().create();
                            DealObject newDeal = gson.fromJson(c.toString(), DealObject.class);
                            deals.add(newDeal);
                        }
                    } else {
                        Log.d("Deals: ", "null");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            swipeRefreshLayout.setRefreshing(false);
            if(getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter = new DealsAdapter(getActivity(), deals, true, false);
                    songRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            });

        }
    }
}
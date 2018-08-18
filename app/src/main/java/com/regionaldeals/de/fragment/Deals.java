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
import com.regionaldeals.de.adapter.DealsAdapter;
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

public class Deals extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private List<DealObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Deals = "/mobile/api/deals/list";
    private JSONArray dealArr = null;
    private RecyclerView songRecyclerView;
    private DealsAdapter mAdapter;
    private Double locationLat = 50.781203;
    private Double locationLng = 6.078068;
    private int maxDistance = 50;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId = "";
    private MyReceiver myReceiver;
    private IntentFilter filter;

    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            maxDistance = intent.getIntExtra("distance", maxDistance);
            new Deals.LoadDeals().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        context = getContext();
        getActivity().setTitle(getResources().getString(R.string.headerText));
        songRecyclerView = (RecyclerView) view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        songRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                                 @Override
                                                 public void onScrollStateChanged(RecyclerView view, int scrollState) {

                                                 }

                                                 @Override
                                                 public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                     int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                                                     swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        filter = new IntentFilter("BroadcastReceiver");
        myReceiver = new MyReceiver();
        deals = new ArrayList<>();

        mAdapter = new DealsAdapter(getActivity(), deals);
        songRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new LoadDeals().execute();
                    }
                }
        );

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
        if (((MainActivity) this.getActivity()).getShouldRefresh()) {
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
        new LoadDeals().execute();
    }

    class LoadDeals extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("dealtype", "TYPE_DEALS"));
            params.add(new DoubleNameValuePair("lat", locationLat));
            params.add(new DoubleNameValuePair("long", locationLng));
            params.add(new BasicNameValuePair("userid", userId));
            params.add(new IntNameValuePair("radius", maxDistance * 1000));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Deals, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                deals.clear();
                dealArr = new JSONArray(json);
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

        protected void onPostExecute(String file_url) {
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

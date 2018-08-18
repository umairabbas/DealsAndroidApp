package com.regionaldeals.de.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.DoubleNameValuePair;
import com.regionaldeals.de.Utils.IntNameValuePair;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.adapter.NearbyAdapter;
import com.regionaldeals.de.entities.Shop;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.regionaldeals.de.Constants.CAT_OBJECT_KEY;
import static com.regionaldeals.de.Constants.LOCATION_KEY;

/**
 * Created by Umi on 30.08.2017.
 */

public class NearBy extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemSelectedListener {

    private List<Shop> shopList;
    private ListView mListView;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Online = "/mobile/api/shops/list";
    private JSONArray shopArr = null;
    //    private SwipeRefreshLayout swipeRefreshLayout;
    //private String userId = "";
    private Double locationLat = 50.781203;
    private Double locationLng = 6.078068;
    private NearbyAdapter mAdapter;
    private JSONArray catArr;
    private Spinner spinner;
    private boolean isSpinnerInitial = true;
    private String catShortName = "essen";
    ArrayList<String> items = new ArrayList<String>();
    private TextView nearbyText;
    private int positionEssen = 0;
    private int maxDistance = 50;
    private MyReceiver myReceiver;
    private IntentFilter filter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            maxDistance = intent.getIntExtra("distance", maxDistance);
            new NearBy.LoadDeals().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        context = getContext();
        getActivity().setTitle(getResources().getString(R.string.headerText));
        mListView = (ListView) view.findViewById(R.id.shop_list);
        nearbyText = (TextView) view.findViewById(R.id.nearbyEmpty);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        items = new ArrayList<String>();

        locationLat = ((Main) getParentFragment()).getLat();
        locationLng = ((Main) getParentFragment()).getLng();

        String restoredText = SharedPreferenceUtils.getInstance(getActivity()).getStringValue(LOCATION_KEY, null);
        String restoredCat = SharedPreferenceUtils.getInstance(getActivity()).getStringValue(CAT_OBJECT_KEY, null);

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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable t) {
        }
        try {
            if (restoredCat != null) {
                catArr = new JSONArray(restoredCat);
                for (int i = 0; i < catArr.length(); i++) {
                    JSONObject catOb = (JSONObject) catArr.get(i);
                    String catt = (String) catOb.get("catName");
                    items.add(catt);
                    if (catt.equals("Essen")) {
                        positionEssen = i;
                    }
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable t) {
        }

        shopList = new ArrayList<>();
        mAdapter = new NearbyAdapter(context, shopList);
        mListView.setAdapter(mAdapter);

        spinner = (Spinner) view.findViewById(R.id.spinnerNearby);
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isSpinnerInitial = true;
        spinner.setAdapter(adapter);
        spinner.setSelection(positionEssen);
        spinner.setOnItemSelectedListener(this);

//        swipeRefreshLayout.post(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        new NearBy.LoadDeals().execute();
//                    }
//                }
//        );

        new NearBy.LoadDeals().execute();

        filter = new IntentFilter("BroadcastReceiver");
        myReceiver = new MyReceiver();

        return view;
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new NearBy.LoadDeals().execute();
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

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        if (isSpinnerInitial) {
            isSpinnerInitial = false;
        } else {
            JSONObject catOb = null;
            try {
                catOb = (JSONObject) catArr.get(position);
                catShortName = (String) catOb.get("catShortName");
                new NearBy.LoadDeals().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
//    @Override
//    public void onRefresh() {
//        // swipe refresh is performed, fetch the messages again
//        new NearBy.LoadDeals().execute();
//    }

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
            params.add(new BasicNameValuePair("cat", catShortName));
            params.add(new IntNameValuePair("radius", maxDistance * 1000));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Online, "GET",
                    params);

            Log.d("JSON: ", "> " + json);
            try {
                shopList.clear();
                JSONObject jO = new JSONObject(json);
                if (jO.getString("message").equals("SHOPS_LIST_EMPTY")) {
                    //Snackbar.make(getView(), "Cannot find shops in this Category", Snackbar.LENGTH_SHORT).show();
                } else {
                    shopArr = (JSONArray) jO.getJSONArray("data");
                    if (shopArr != null) {
                        for (int i = 0; i < shopArr.length(); i++) {
                            JSONObject c = shopArr.getJSONObject(i);
                            Gson gson = new GsonBuilder().create();
                            Shop newDeal = gson.fromJson(c.toString(), Shop.class);
                            shopList.add(newDeal);
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
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (shopList.size() > 0) {
                        nearbyText.setVisibility(View.GONE);
                    } else {
                        nearbyText.setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });

        }
    }
}
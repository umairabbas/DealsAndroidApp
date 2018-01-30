package com.regionaldeals.de.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.regionaldeals.de.Utils.DoubleNameValuePair;
import com.regionaldeals.de.Utils.IntNameValuePair;
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

public class Deals extends Fragment implements //AdapterView.OnItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private List<DealObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Deals = "/mobile/api/deals/list";
    private JSONArray dealArr = null;
    private RecyclerView songRecyclerView;
    private DealsAdapter mAdapter;
    private ProgressDialog pDialog;
    private boolean isSpinnerInitial = true;
    private Double locationLat = 50.781203;
    private Double locationLng = 6.078068;
    private int maxDistance = 5;
    //private Spinner spinner;
    //private static final String[] paths = {"5 KM", "10 KM", "50 KM", "100 KM", "500 KM", "ALL"};
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId = "";
    private MyReceiver myReceiver;
    private IntentFilter filter;
    //private SeekBar seekControl = null;

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
        songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        songRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =(recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
                }
            }
        );

//        spinner = (Spinner) view.findViewById(R.id.spinnerInput);
//        spinner.setVisibility(View.VISIBLE);
//        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_spinner_item, paths);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        isSpinnerInitial = true;
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);
        String restoredUser = prefs.getString("userObject", null);
        try {
            if (restoredText != null) {
                JSONObject obj = new JSONObject(restoredText);
                String Lat = obj.getString("lat");
                String Lng = obj.getString("lng");
                if(!Lat.isEmpty() && !Lng.isEmpty()) {
                    locationLat = Double.parseDouble(Lat);
                    locationLng = Double.parseDouble(Lng);
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

//        view.findViewById(R.id.distanceInput).setVisibility(View.VISIBLE);
//        SeekBar seek = (SeekBar) view.findViewById(R.id.select_distance);
//        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            TextView seekText = (TextView) view.findViewById(R.id.distance);
//            int newProgress = 10;
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(progress < 5){
//                    newProgress = progress;
//                }
//                else {
//                    newProgress = (int) (Math.rint((double) progress / 10) * 10);
//                }
//                seekBar.setProgress(newProgress);
//                seekText.setText(Integer.toString(newProgress) + " KM");
//                maxDistance = newProgress;
//                new LoadDeals().execute();
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });


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

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new LoadDeals().execute();
    }

//    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//
//        if(isSpinnerInitial)
//        {
//            isSpinnerInitial = false;
//        }
//        else  {
//            switch (position) {
//                case 0:
//                    maxDistance = 5;
//                    new LoadDeals().execute();
//                    break;
//                case 1:
//                    maxDistance = 10;
//                    new LoadDeals().execute();
//                    break;
//                case 2:
//                    maxDistance = 50;
//                    new LoadDeals().execute();
//                    break;
//                case 3:
//                    maxDistance = 100;
//                    new LoadDeals().execute();
//                    break;
//                case 4:
//                    maxDistance = 500;
//                    new LoadDeals().execute();
//                    break;
//                case 5:
//                    maxDistance = 9999;
//                    new LoadDeals().execute();
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }

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
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("dealtype", "TYPE_DEALS"));
            params.add(new DoubleNameValuePair("lat", locationLat));
            params.add(new DoubleNameValuePair("long", locationLng));
            params.add(new BasicNameValuePair("userid", userId));
            params.add(new IntNameValuePair("radius", maxDistance*1000));

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
            if(getActivity() == null)
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

package com.dealspok.dealspok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dealspok.dealspok.MainActivity;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.Utils.DoubleNameValuePair;
import com.dealspok.dealspok.Utils.IntNameValuePair;
import com.dealspok.dealspok.Utils.JSONParser;
import com.dealspok.dealspok.adapter.DealsAdapter;
import com.dealspok.dealspok.entities.DealObject;
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

public class Deals extends Fragment implements AdapterView.OnItemSelectedListener {

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
    private int maxDistance = 1;
    private Spinner spinner;
    private static final String[] paths = {"1 KM", "2 KM", "5 KM", "10 KM", "50 KM", "100 KM"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        context = getContext();
        getActivity().setTitle("Regional Deals");
        songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        spinner = (Spinner)view.findViewById(R.id.spinnerInput);
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isSpinnerInitial = true;
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);

        if (restoredText != null) {

            try {
                JSONObject obj = new JSONObject(restoredText);
                String Lat = obj.getString("lat");
                String Lng = obj.getString("lng");
                if(!Lat.isEmpty() && !Lng.isEmpty()) {
                    locationLat = Double.parseDouble(Lat);
                    locationLng = Double.parseDouble(Lng);

                }
            } catch (Throwable t) {
            }

        }

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

        // Loading JSON in Background Thread
        new LoadDeals().execute();

        return view;
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        if(isSpinnerInitial)
        {
            isSpinnerInitial = false;
        }
        else  {
            switch (position) {
                case 0:
                    maxDistance = 1;
                    new UpdateDeals().execute();
                    break;
                case 1:
                    maxDistance = 2;
                    new UpdateDeals().execute();
                    break;
                case 2:
                    maxDistance = 5;
                    new UpdateDeals().execute();
                    break;
                case 3:
                    maxDistance = 10;
                    new UpdateDeals().execute();
                    break;
                case 4:
                    maxDistance = 50;
                    new UpdateDeals().execute();
                    break;
                case 5:
                    maxDistance = 100;
                    new UpdateDeals().execute();
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new DoubleNameValuePair("lat", locationLat));
            params.add(new DoubleNameValuePair("long", locationLng));
            params.add(new IntNameValuePair("radius", maxDistance*1000));

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
            // dismiss the dialog after getting all albums
            pDialog.dismiss();
            // updating UI from Background Thread
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

            params.add(new DoubleNameValuePair("lat", 50.781203));
            params.add(new DoubleNameValuePair("long", 6.078068));
            params.add(new IntNameValuePair("radius", maxDistance*1000));

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
            // dismiss the dialog after getting all albums
            //pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}

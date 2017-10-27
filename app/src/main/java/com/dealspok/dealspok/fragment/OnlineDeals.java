package com.dealspok.dealspok.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.Utils.JSONParser;
import com.dealspok.dealspok.adapter.CustomFragmentPageAdapter;
import com.dealspok.dealspok.adapter.DealsAdapter;
import com.dealspok.dealspok.adapter.OnlineDealsAdapter;
import com.dealspok.dealspok.entities.DealObject;
import com.dealspok.dealspok.entities.OnlineDealsObject;
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

public class OnlineDeals extends Fragment {

    private List<OnlineDealsObject> deals;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Online = "/mobile/api/onlinedeals/list";
    private JSONArray dealArr = null;
    private RecyclerView songRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);
        context = getContext();
        getActivity().setTitle("Regional Deals");
        songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        deals = new ArrayList<>();
        // Loading JSON in Background Thread
        new OnlineDeals.LoadDeals().execute();
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

                if (dealArr != null) {
                    for (int i = 0; i < dealArr.length(); i++) {
                        JSONObject c = dealArr.getJSONObject(i);
                        Gson gson = new GsonBuilder().create();
                        OnlineDealsObject newDeal = gson.fromJson(c.toString(), OnlineDealsObject.class);
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
            if(getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    OnlineDealsAdapter mAdapter = new OnlineDealsAdapter(getActivity(), deals);
                    songRecyclerView.setAdapter(mAdapter);
                }
            });

        }
    }
}
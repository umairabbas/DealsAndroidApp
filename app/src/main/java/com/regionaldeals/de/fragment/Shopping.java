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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.DoubleNameValuePair;
import com.regionaldeals.de.Utils.IntNameValuePair;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.adapter.NearbyAdapter;
import com.regionaldeals.de.adapter.OnlineDealsAdapter;
import com.regionaldeals.de.adapter.ShopAdapter;
import com.regionaldeals.de.entities.DealObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.regionaldeals.de.entities.Shop;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Umi on 30.08.2017.
 */

public class Shopping extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{

    private List<Shop> shopList;
    private ListView mListView;
    JSONParser jsonParser = new JSONParser();
    Context context;
    private final String URL_Online = "/mobile/api/shops/list";
    private JSONArray shopArr = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private String userId = "";
    private Double locationLat = 50.781203;
    private Double locationLng = 6.078068;
    private NearbyAdapter mAdapter;
    private JSONArray cat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        context = getContext();
        getActivity().setTitle(getResources().getString(R.string.headerText));
        mListView = (ListView)view.findViewById(R.id.shop_list);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);
        String restoredCat = prefs.getString("categoriesObj", null);

        // restoredUser = prefs.getString("userObject", null);
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
            if(restoredCat != null){
                cat = new JSONArray(restoredCat);
            } else {

            }
//            if (restoredUser != null) {
//                JSONObject obj = new JSONObject(restoredUser);
//                userId = obj.getString("userId");
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable t) {
        }

        shopList = new ArrayList<>();
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new Shopping.LoadDeals().execute();
                    }
                }
        );

        return view;
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new Shopping.LoadDeals().execute();
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
            params.add(new DoubleNameValuePair("lat", locationLat));
            params.add(new DoubleNameValuePair("long", locationLng));
            //params.add(new DoubleNameValuePair("cat", ));
            params.add(new IntNameValuePair("radius", 10000));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Online, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                shopList.clear();
                JSONObject jO = new JSONObject(json);
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
                    mAdapter = new NearbyAdapter(context, shopList);
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            });

        }
    }
}
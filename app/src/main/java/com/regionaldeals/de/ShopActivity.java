package com.regionaldeals.de;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.adapter.ShopAdapter;
import com.regionaldeals.de.entities.Shop;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umi on 28.10.2017.
 */

public class ShopActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private ListView mListView;
    private List<Shop> shopList;
    private JSONArray shopArr = null;
    private Context context;
    private String userId = "";
    JSONParser jsonParser = new JSONParser();
    private ShopAdapter mAdapter;
    private final String URL_Deals = "/web/shops/list";
    private Boolean isFirst = true;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isFirst) {
            new LoadShop().execute();
        } else {
            isFirst = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_main);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbarShop);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.shop_list_view);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startActivityIntent = new Intent(ShopActivity.this, AddShopActivity.class);
                startActivity(startActivityIntent);
            }
        });

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
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

        shopList = new ArrayList<>();
        new LoadShop().execute();
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new LoadShop().execute();
        return;
    }


    class LoadShop extends AsyncTask<String, String, String> {

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

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Deals, "GET",
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

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            swipeRefreshLayout.setRefreshing(false);
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    mAdapter = new ShopAdapter(context, shopList);
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.regionaldeals.de;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.adapter.DealsAdapter;
import com.regionaldeals.de.entities.DealObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.regionaldeals.de.entities.Shop;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Umi on 10.12.2017.
 */

public class CreateDealsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    private RecyclerView songRecyclerView;
    private String userId = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<DealObject> deals;
    private final String URL_Deals = "/mobile/api/deals/list-owner";
    private JSONArray dealArr = null;
    JSONParser jsonParser = new JSONParser();
    private DealsAdapter mAdapter;
    public static final int ADD_DEALS_REQUEST_CODE = 115;
    private TextView dealCount;
    private FloatingActionButton fab;
    private int dealCounter;
    private String subStatus = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_deals_main);
        context = this;
        activity = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar_createDeals);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        songRecyclerView = (RecyclerView)findViewById(R.id.create_deals_list);
        dealCount = (TextView) findViewById(R.id.dealsCount);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredUser = prefs.getString("userObject", null);
        String restoredSub = prefs.getString("subscriptionObject", null);
        try {
            if (restoredUser != null) {
                JSONObject obj = new JSONObject(restoredUser);
                userId = obj.getString("userId");
            }
            if (restoredSub != null) {
                JSONObject data = new JSONObject(restoredSub);
                dealCounter = data.getInt("deals_listing");
                subStatus = data.getString("subscriptionStatus");
                dealCount.setText(" "+Integer.toString(dealCounter) + "/4" + " (" + subStatus + ") ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable t) {
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab_new_deals);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dealCounter>=4){
                    Snackbar.make(view, getResources().getString(R.string.deals_limit), Snackbar.LENGTH_LONG).show();
                }else if(dealCounter<4){
                    Intent startActivityIntent = new Intent(CreateDealsActivity.this, AddDealActivity.class);
                    startActivityIntent.putExtra("userId", userId);
                    startActivityForResult(startActivityIntent, ADD_DEALS_REQUEST_CODE);
                }
                //Should not reach here
                else {
                    Snackbar.make(view, "Error", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        deals = new ArrayList<>();

        getShopsFromServer();

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new CreateDealsActivity.LoadDeals().execute();
                    }
                }
        );
    }
    private final String URL_Shops = "/mobile/api/shops/list";
    private void getShopsFromServer() {
        AsyncHttpClient androidClient = new AsyncHttpClient();
        RequestParams params = new RequestParams("userid", userId);
        androidClient.get(this.getString(R.string.apiUrl) + URL_Shops, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "Cannot find shops..\nReport us back!", Toast.LENGTH_LONG).show();
                finish();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
                try {
                    JSONObject jO = new JSONObject(responseToken);
                    JSONArray data = (JSONArray) jO.getJSONArray("data");
                    if (data == null) {
                        Toast.makeText(context, getResources().getString(R.string.no_shop), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // do nothing
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, getResources().getString(R.string.no_shop), Toast.LENGTH_LONG).show();
                    finish();
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_DEALS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Boolean dealSuccess = data.getBooleanExtra("dealAddSuccess", false);
                if(dealSuccess) {
                    dealCounter++;
                    dealCount.setText(" "+Integer.toString(dealCounter) + "/4" + " (" + subStatus + ") ");
                    getSubscription();
                }
            }
        }
    }

    private void getSubscription() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient androidClient = new AsyncHttpClient();
                androidClient.get("https://regionaldeals.de/mobile/api/subscriptions/subscription?userid="+ userId, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("TAG", getString(R.string.token_failed) + responseString);
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        Log.d("TAG", "Client token: " + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            String msg = obj.getString("message");
                            if(msg.equals("PLANS_SUBSCRIPTIONS_OK")) {
                                JSONObject data = obj.getJSONObject("data");
                                JSONObject plan = data.getJSONObject("plan");
                                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
                                editor.putString("subscriptionObject", data.toString());
                                editor.commit();
                            } else if (msg.equals("PLANS_SUBSCRIPTIONS_NILL")) {
                                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
                                editor.remove("subscriptionObject");
                                editor.commit();
                            }
                            //should never come
                            else{
                                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
                                editor.remove("subscriptionObject");
                                editor.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Throwable t) {
                        }
                    }
                });
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
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

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new CreateDealsActivity.LoadDeals().execute();
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
            params.add(new BasicNameValuePair("userid", userId));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Deals, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                deals.clear();
                JSONObject dealObj = new JSONObject(json);
                dealArr = dealObj.getJSONArray("data");
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
            swipeRefreshLayout.setRefreshing(false);
            if(activity == null)
                return;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    mAdapter = new DealsAdapter(activity, deals, false, true);
                    songRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}

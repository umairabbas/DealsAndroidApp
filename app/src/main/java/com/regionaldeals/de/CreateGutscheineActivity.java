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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.adapter.GutscheineAdapter;
import com.regionaldeals.de.entities.GutscheineObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Umi on 19.12.2017.
 */

public class CreateGutscheineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    private RecyclerView songRecyclerView;
    private String userId = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<GutscheineObject> deals;
    private final String URL_Deals = "/mobile/api/gutschein/list-owner";
    private JSONArray dealArr = null;
    JSONParser jsonParser = new JSONParser();
    private TextView dealCount;
    private GutscheineAdapter mAdapter;
    public static final int ADD_GUT_REQUEST_CODE = 116;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dealCount = (TextView) findViewById(R.id.dealsCount);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_deals);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dealCounter>=4){
                    Snackbar.make(view, getResources().getString(R.string.deals_limit), Snackbar.LENGTH_LONG).show();
                }else if(dealCounter<4) {
                    Intent startActivityIntent = new Intent(CreateGutscheineActivity.this, AddDealActivity.class);
                    startActivityIntent.putExtra("isGutscheine", true);
                    startActivityIntent.putExtra("userId", userId);
                    startActivityForResult(startActivityIntent, ADD_GUT_REQUEST_CODE);
                }//Should not reach here
                else {
                    Snackbar.make(view, "Error", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        deals = new ArrayList<>();

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        new CreateGutscheineActivity.LoadDeals().execute();
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_GUT_REQUEST_CODE) {
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
        new CreateGutscheineActivity.LoadDeals().execute();
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
                        GutscheineObject newDeal = gson.fromJson(c.toString(), GutscheineObject.class);
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
                    mAdapter = new GutscheineAdapter(activity, deals, true);
                    songRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}

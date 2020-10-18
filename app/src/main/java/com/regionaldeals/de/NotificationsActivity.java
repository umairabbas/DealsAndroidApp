package com.regionaldeals.de;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.adapter.NotificationsAdapter;
import com.regionaldeals.de.entities.GutscheineObject;
import com.regionaldeals.de.entities.NotificationsObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umi on 10.03.2018.
 */

public class NotificationsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar toolbar;
    private Context context;
    private final String URL_Deals = "/web/users/notifications";
    private TextView nearbyText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<NotificationsObject> shopList;
    private ListView mListView;
    JSONParser jsonParser = new JSONParser();
    private JSONArray shopArr = null;
    private NotificationsAdapter mAdapter;
    private Activity activity;
    private String userId = "";
    private String InstallationNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);
        context = this;
        activity = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar_createDeals);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.shop_list);
        nearbyText = (TextView) findViewById(R.id.nearbyEmpty);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        InstallationNumber = Installation.id(this);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
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
        mAdapter = new NotificationsAdapter(context, shopList);
        mListView.setAdapter(mAdapter);

        new NotificationsActivity.LoadDeals().execute();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        new NotificationsActivity.LoadDeals().execute();
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
            params.add(new BasicNameValuePair("deviceuuidimei", InstallationNumber));

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Deals, "GET",
                    params);

            Log.d("JSON: ", "> " + json);

            try {
                System.out.print(URL_Deals);
                shopList.clear();
                JSONObject jO = new JSONObject(json);
                if (jO.getString("message").equals("USER_NOTIFICATION_EMPTY") || jO.getString("message").equals("USER_NOTIFICATION_COUNT - 0")) {
                    //Snackbar.make(getView(), "Cannot find shops in this Category", Snackbar.LENGTH_SHORT).show();
                } else {
                    shopArr = (JSONArray) jO.getJSONArray("data");
                    if (shopArr != null) {
                        for (int i = 0; i < shopArr.length(); i++) {
                            JSONObject c = shopArr.getJSONObject(i);
                            NotificationsObject newDeal = new NotificationsObject();
                            newDeal.setNotificationType(c.getInt("notificationType"));
                            if (!c.isNull("notificationText1") && !c.isNull("notificationText2")) {
                                newDeal.setNotificationText1(c.getString("notificationText1"));
                                newDeal.setNotificationText2(c.getString("notificationText2"));
                            }

                            newDeal.setNotificationDetails(c.getString("notificationDetails"));
                            if (newDeal.getNotificationType() == 20) {
                                Gson gson = new GsonBuilder().create();
                                GutscheineObject g = gson.fromJson(c.getJSONObject("notificationObject").toString(), GutscheineObject.class);
                                newDeal.setGutscheineObject(g);
                            } else {
                                newDeal.setGutscheineObject(null);
                            }

                            try {
                                newDeal.setNotificationDate(c.getLong("notificationDate"));
                            } catch (Exception e) {
                            }

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
            if (activity == null)
                return;
            activity.runOnUiThread(new Runnable() {
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

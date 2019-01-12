package com.regionaldeals.de;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.regionaldeals.de.Utils.HttpClient;
import com.regionaldeals.de.entities.Plans;

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
 * Created by Umi on 02.12.2017.
 */

public class SubscribeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbarSub);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView currentSub = (TextView) findViewById(R.id.currentSub);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredSub = prefs.getString("subscriptionObject", null);
        try {
            //Subscription
            if (restoredSub != null) {
                JSONObject data = new JSONObject(restoredSub);
                JSONObject plan = data.getJSONObject("plan");
                String planName = plan.getString("planName");
                int billingCycle = plan.getInt("billingCycle");
                int numberBillingCycles = plan.getInt("numberBillingCycles");
                String subStatus = data.getString("subscriptionStatus");
                String start = "";
                if(!data.isNull("subscriptionStartDate")) {
                    long subStartDate = data.getLong("subscriptionStartDate");
                    Date d = new Date(subStartDate);
                    SimpleDateFormat startDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    start = startDate.format(d);
                }
                String next = "";
                if(!data.isNull("subscriptionNextPaymentDate")){
                    long subNextPayment = data.getLong("subscriptionNextPaymentDate");
                    Date d2 = new Date(subNextPayment);
                    SimpleDateFormat nextDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    next = nextDate.format(d2);
                }
                //String planDesc = plan.getString("planDescription");
                currentSub.setVisibility(View.VISIBLE);

                currentSub.setText("Plan" + "\n" + planName + "\n" + "\n"
                        + "Status" + "\n" + subStatus + "\n" + "\n"
                        + getResources().getString(R.string.billing_cycle) + "\n" + Integer.toString(billingCycle) + "/" + Integer.toString(numberBillingCycles) + "\n" + "\n"
                        + getResources().getString(R.string.start_date) + "\n" + start + "\n" + "\n"
                        + getResources().getString(R.string.next_date) + "\n" + next + "\n"
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable t) {
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

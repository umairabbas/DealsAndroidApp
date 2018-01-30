package com.regionaldeals.de;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.regionaldeals.de.Utils.HttpClient;
import com.regionaldeals.de.entities.Plans;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

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
    private Context context;
    private Activity activity;
    private View v;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PATH_TO_SERVER = "https://regionaldeals.de/mobile/api/subscriptions/client_token";
    private static final String PATH_TO_SERVER_SUB_DEALS = "https://www.regionaldeals.de/mobile/api/subscriptions/plans";
    private static final String PATH_TO_SERVER_CHECKOUT = "https://regionaldeals.de/mobile/api/subscriptions/customer_vault";
    private String clientToken;
    private static final int BRAINTREE_REQUEST_CODE = 4949;
    private String paymentNonce = "";
    private String msg = "";
    private Button firstDealBtn;
    private TextView firstDealTv;
    private Button secDealBtn;
    private TextView secDealTv;
    private JSONArray planArr = null;
    private List<Plans> deals;
    private String planShortName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbarSub);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        activity = this;

        firstDealBtn = (Button)findViewById(R.id.btn_1);
        secDealBtn = (Button)findViewById(R.id.btn_2);
        firstDealTv = (TextView)findViewById(R.id.tv1);
        secDealTv = (TextView)findViewById(R.id.tv2);
        TextView currentSub = (TextView)findViewById(R.id.currentSub);

        deals = new ArrayList<>();

        firstDealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v = view;
                planShortName  = deals.get(0).getPlanShortName();
                onBraintreeSubmit(view);
            }
        });

        secDealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v = view;
                planShortName  = deals.get(1).getPlanShortName();
                onBraintreeSubmit(view);
            }
        });

        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredSub = prefs.getString("subscriptionObject", null);
        String restoredUser = prefs.getString("userObject", null);
        try {
            //Subscription
            if (restoredSub != null) {
                JSONObject data = new JSONObject(restoredSub);
                JSONObject plan = data.getJSONObject("plan");
                String planName = plan.getString("planName");
                int billingCycle = plan.getInt("billingCycle");
                int numberBillingCycles = plan.getInt("numberBillingCycles");
                long subStartDate = data.getLong("subscriptionStartDate");
                String subStatus = data.getString("subscriptionStatus");
                long subNextPayment = data.getLong("subscriptionNextPaymentDate");
                //String planDesc = plan.getString("planDescription");
                currentSub.setVisibility(View.VISIBLE);

                Date d = new Date(subStartDate);
                SimpleDateFormat startDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String start = startDate.format(d);
                Date d2 = new Date(subNextPayment);
                SimpleDateFormat nextDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String next = nextDate.format(d2);

                currentSub.setText("Plan" + "\n" + planName +"\n" + "\n"
                        + "Status" + "\n" + subStatus + "\n" + "\n"
                        + getResources().getString(R.string.billing_cycle) + "\n" + Integer.toString(billingCycle)+ "/" + Integer.toString(numberBillingCycles) + "\n" + "\n"
                        + getResources().getString(R.string.start_date) + "\n" + start + "\n" + "\n"
                        + getResources().getString(R.string.next_date) + "\n" + next + "\n"
                        );

                firstDealBtn.setVisibility(View.GONE);
                firstDealTv.setVisibility(View.GONE);
                secDealBtn.setVisibility(View.GONE);
                secDealTv.setVisibility(View.GONE);
            }else {
                firstDealBtn.setEnabled(true);
                secDealBtn.setEnabled(true);
                getSubDataFromServer();
                getClientTokenFromServer();
            }
            //User
            if (restoredUser != null) {
                JSONObject obj = new JSONObject(restoredUser);
                userId = obj.getString("userId");
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

    private void getSubDataFromServer(){
        AsyncHttpClient androidClient = new AsyncHttpClient();
        androidClient.get(PATH_TO_SERVER_SUB_DEALS, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", getString(R.string.token_failed) + responseString);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
                Log.d("TAG", "Client token: " + responseToken);
                try {
                    JSONObject obj = new JSONObject(responseToken);
                    String dealsObj = obj.getString("data");
                    deals.clear();
                    planArr = new JSONArray(dealsObj);
                    if (planArr != null) {
                        for (int i = 0; i < planArr.length(); i++) {
                            JSONObject c = planArr.getJSONObject(i);
                            Gson gson = new GsonBuilder().create();
                            Plans newDeal = gson.fromJson(c.toString(), Plans.class);
                            deals.add(newDeal);
                        }
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                //TODO: make dynamic
                                firstDealBtn.setText(deals.get(0).getPlanName());
                                secDealBtn.setText(deals.get(1).getPlanName());
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                    firstDealTv.setText(Html.fromHtml(deals.get(0).getPlanDescription(), Html.FROM_HTML_MODE_COMPACT));
                                    secDealTv.setText(Html.fromHtml(deals.get(1).getPlanDescription(), Html.FROM_HTML_MODE_COMPACT));
                                }else{
                                    firstDealTv.setText(Html.fromHtml(deals.get(0).getPlanDescription()));
                                    secDealTv.setText(Html.fromHtml(deals.get(1).getPlanDescription()));
                                }
                                //Toast.makeText(context, "Success!\n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.d("Plans: ", "null");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        });
    }

    private void getClientTokenFromServer(){
        AsyncHttpClient androidClient = new AsyncHttpClient();
        androidClient.get(PATH_TO_SERVER, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", getString(R.string.token_failed) + responseString);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
                Log.d("TAG", "Client token: " + responseToken);
                try {
                    JSONObject obj = new JSONObject(responseToken);
                    String token = obj.getString("data");
                    clientToken = token;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        });
    }

//    private void getCheckoutFromServer(){
//        AsyncHttpClient androidClient = new AsyncHttpClient();
//        androidClient.get(PATH_TO_SERVER_CHECKOUT+"?userid="+ userId + "&payment_method_nonce=" + paymentNonce +
//                "&billing_plan=" + planShortName, new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.d("TAG", getString(R.string.token_failed) + responseString);
//            }
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
//                Log.d("TAG", "Client token: " + responseToken);
//                try {
//                    JSONObject obj = new JSONObject(responseToken);
//                    String token = obj.getString("data");
//                    clientToken = token;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (Throwable t) {
//                }
//            }
//        });
//    }

    class checkout extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                String resultData = "";
                HttpClient client = new HttpClient(PATH_TO_SERVER_CHECKOUT);
                client.connectForMultipart();
                client.addFormPart("payment_method_nonce", paymentNonce);
                client.addFormPart("userid", userId);
                client.addFormPart("billing_plan", planShortName);
                client.finishMultipart();
                resultData = client.getResponse();
                resultData.toString();
                JSONObject obj = new JSONObject(resultData);

                msg = obj.getString("message");
                if(msg.equals("PLANS_SUBSCRIPTIONS_OK") || msg.equals("PLANS_SUBSCRIPTIONS_UPDATE_OK")) {

                    JSONObject data = obj.getJSONObject("data");
                    JSONObject plan = data.getJSONObject("plan");
                    final String planName = plan.getString("planName");
//                    String subStartDate = data.getString("subscriptionStartDate");
//                    String subStatus = data.getString("subscriptionStatus");
//                    String subNextPayment = data.getString("subscriptionNextPaymentDate");

                    SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
                    editor.putString("subscriptionObject", data.toString());
                    editor.commit();

                    if (activity != null)
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Success!\n" + "Sie haben erfolgreich " + planName + " gekauft", Toast.LENGTH_SHORT).show();
                                firstDealBtn.setEnabled(false);
                                secDealBtn.setEnabled(false);
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("subscribed", true);
                                startActivity(intent);
                                //finish();
                            }
                        });
                }else if(msg.equals("PLANS_SUBSCRIPTIONS_ALREADY_SUBSCRIBED")) {
                    if(activity!=null)
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Already Subscribed!\n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    if(activity!=null)
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "Failed!\n" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            //pDialog.dismiss();
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    if(msg.equals("PLANS_SUBSCRIPTIONS_OK")) {
//                        Toast.makeText(context, "Success!\n" + msg, Toast.LENGTH_SHORT).show();
//                    } else if(msg.equals("PLANS_SUBSCRIPTIONS_UPDATE_OK")){
//                        Toast.makeText(context, "Success!\n" + msg, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }
    }

    private void sendPaymentNonceToServer(String paymentNonce){
        this.paymentNonce = paymentNonce;
        new checkout().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BRAINTREE_REQUEST_CODE){
            if (RESULT_OK == resultCode){
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentNonce = result.getPaymentMethodNonce().getNonce();
                //send to your server
                Log.d(TAG, "Testing the app here");
                sendPaymentNonceToServer(paymentNonce);
            }else if(resultCode == Activity.RESULT_CANCELED){
                Log.d(TAG, "User cancelled payment");
            }else {
                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d(TAG, " error exception");
                Toast.makeText(context, "Kindly choose a different option", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onBraintreeSubmit(View view){
        DropInRequest dropInRequest = new DropInRequest().clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(this), BRAINTREE_REQUEST_CODE);
    }
}

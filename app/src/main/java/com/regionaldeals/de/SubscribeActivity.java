package com.dealspok.dealspok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.dealspok.dealspok.adapter.GutscheineAdapter;
import com.dealspok.dealspok.entities.DealObject;
import com.dealspok.dealspok.entities.Plans;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
//    EditText e1;
//    EditText e2;
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

        deals = new ArrayList<>();
//        e1 = (EditText)findViewById(R.id.input_t1);
//        e2 = (EditText)findViewById(R.id.input_t2);

//        e1.setText("https://regionaldeals.de/mobile/api/payment/client_token");
//        e2.setText("https://regionaldeals.de/mobile/api/payment/checkout");
        getSubDataFromServer();
        getClientTokenFromServer();
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
                                firstDealTv.setText(deals.get(0).getPlanDescription());
                                secDealBtn.setText(deals.get(1).getPlanName());
                                secDealTv.setText(deals.get(1).getPlanDescription());
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
                com.dealspok.dealspok.Utils.HttpClient client = new com.dealspok.dealspok.Utils.HttpClient(PATH_TO_SERVER_CHECKOUT);
                client.connectForMultipart();
                client.addFormPart("payment_method_nonce", paymentNonce);
                client.addFormPart("userid", userId);
                client.addFormPart("billing_plan", planShortName);
                client.finishMultipart();
                resultData = client.getResponse();
                resultData.toString();
                JSONObject obj = new JSONObject(resultData);
                String payId = obj.getString("data");
                msg = obj.getString("message");
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            //pDialog.dismiss();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if(msg.equals("PAYMENT_CLIENT_CHECKOUT_SUCCESS")) {
                        Toast.makeText(context, "Success!\n" + msg, Toast.LENGTH_SHORT).show();
                    } else if(msg.equals("PLANS_SUBSCRIPTIONS_UPDATE_OK")){
                        Toast.makeText(context, "Success!\n" + msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
            }
        }
    }

    public void onBraintreeSubmit(View view){
        DropInRequest dropInRequest = new DropInRequest().clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(this), BRAINTREE_REQUEST_CODE);
    }
}

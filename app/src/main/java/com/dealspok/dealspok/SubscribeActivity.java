package com.dealspok.dealspok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.dealspok.dealspok.adapter.GutscheineAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String PATH_TO_SERVER = "https://regionaldeals.de/mobile/api/payment/client_token";
    private static final String PATH_TO_SERVER_CHECKOUT = "https://regionaldeals.de/mobile/api/payment/checkout";
    private String clientToken;
    private static final int BRAINTREE_REQUEST_CODE = 4949;
    private String paymentNonce = "";
    private String msg = "";
//    EditText e1;
//    EditText e2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbarSub);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        e1 = (EditText)findViewById(R.id.input_t1);
//        e2 = (EditText)findViewById(R.id.input_t2);

//        e1.setText("https://regionaldeals.de/mobile/api/payment/client_token");
//        e2.setText("https://regionaldeals.de/mobile/api/payment/checkout");

        context = this;
        activity = this;

        getClientTokenFromServer();
        Button buyNowButton = (Button)findViewById(R.id.btn_1);
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v = view;
                onBraintreeSubmit(view);
            }
        });

        Button buyNowButton2 = (Button)findViewById(R.id.btn_2);
        buyNowButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v = view;
                onBraintreeSubmit(view);
            }
        });

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
                //client.addFormPart("userid", userId);
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

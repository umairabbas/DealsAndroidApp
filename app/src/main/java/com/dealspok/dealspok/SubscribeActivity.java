package com.dealspok.dealspok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Umi on 02.12.2017.
 */

public class SubscribeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context context;
    private Activity activity;
    private View v;

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

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PATH_TO_SERVER = "http://192.168.1.20/paypaltest/";
    private String clientToken;
    private static final int BRAINTREE_REQUEST_CODE = 4949;

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
                clientToken = responseToken;
            }
        });
    }

    private void sendPaymentNonceToServer(String paymentNonce){
        RequestParams params = new RequestParams("NONCE", paymentNonce);
        AsyncHttpClient androidClient = new AsyncHttpClient();
        androidClient.post(PATH_TO_SERVER, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Snackbar.make(v, "Failed\n" + responseString, Snackbar.LENGTH_LONG ).show();
                //Toast.makeText(context, "Failed\n" + responseString, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error: Failed to create a transaction");
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //Toast.makeText(context, "Success\n"+responseString, Toast.LENGTH_SHORT).show();
                Snackbar.make(v, "Success\n"+responseString, Snackbar.LENGTH_LONG ).show();
                Log.d(TAG, "Output " + responseString);
            }
        });
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

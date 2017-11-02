package com.dealspok.dealspok;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Umi on 28.10.2017.
 */

public class AddShopActivity extends AppCompatActivity {

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_details)
    EditText _detailText;
    @BindView(R.id.input_contact)
    EditText _contactText;
    @BindView(R.id.input_address)
    EditText _addressText;
    @BindView(R.id.input_tax)
    EditText _taxText;
    @BindView(R.id.btn_shop_submit)
    Button _shopButton;

    private final String URL_Login = "/mobile/api/shops/upload-shop";
    private ProgressDialog progressDialog;
    private String name = "";
    private String contact = "";
    private String address = "";
    private String tax = "";
    private String desc = "";
    private String userId = "";
    private Context context;
    private String message = "";
    private JSONObject jObject;
    private Boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_shop_activity);
        context = this;
        ButterKnife.bind(this);

        _shopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addShop();
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
    }

    public void addShop() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _shopButton.setEnabled(false);

        progressDialog = new ProgressDialog(this,
                R.style.ThemeOverlay_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        name = _nameText.getText().toString();
        contact = _contactText.getText().toString();
        tax = _taxText.getText().toString();
        address = _addressText.getText().toString();
        desc = _detailText.getText().toString();

        new ShopCall().execute();
    }

    class ShopCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                message = "";
                URL url = new URL(getString(R.string.apiUrl) + URL_Login + "?userid=" + userId);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("shopId", null);
                jsonParam.put("shopName", name);
                jsonParam.put("shopAddress", address);
                jsonParam.put("shopCity", "Aachen");
                jsonParam.put("shopCountry", "Germany");
                jsonParam.put("shopLocationLat", 50.776631);
                jsonParam.put("shopLocationLong", 6.0571445);
                jsonParam.put("shopContact", contact);
                jsonParam.put("shopDetails", desc);
                jsonParam.put("taxNumber", tax);
                jsonParam.put("active", false);

                Log.i("JSON", jsonParam.toString());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

                BufferedReader in;

                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    in = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                } else {
                    in = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                }
                String inputLine;
                StringBuffer res = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                conn.disconnect();

                if(message.equals(getString(R.string.SHOPS_UPLOAD_OK))) {
                    isSuccess = true;
                }
//                else if (message.equals(getString(R.string.LOGIN_ERR_INVALID_CREDENTIALS))){
//                    isSuccess = false;
//                    message = "Invalid Credentials";
//                }
                else {
                    isSuccess = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            _shopButton.setEnabled(true);
            runOnUiThread(new Runnable() {
                public void run() {
                    if(isSuccess) {

                        Toast.makeText(context, "Shop Added\n" + message, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(context,  "Failed\n" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void onLoginSuccess() {
        _shopButton.setEnabled(true);
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _shopButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String tax = _taxText.getText().toString();
        String contact = _contactText.getText().toString();
        String address = _addressText.getText().toString();

        if (name.isEmpty()) {
            _nameText.setError("enter a valid name");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (tax.isEmpty()) {
            _taxText.setError("enter a valid tax number");
            valid = false;
        } else {
            _taxText.setError(null);
        }

        if (contact.isEmpty()) {
            _contactText.setError("enter a valid contact");
            valid = false;
        } else {
            _contactText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("enter a valid address");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        return valid;
    }


}

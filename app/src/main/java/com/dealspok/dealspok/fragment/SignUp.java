package com.dealspok.dealspok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.Utils.JSONParser;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Umi on 11.10.2017.
 */

public class SignUp extends Fragment {

    public SignUp() {
        // Required empty public constructor
    }

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;
    @InjectView(R.id.shopCheck) Switch _isShopKeeper;

    private ViewPager viewPager;
    private JSONObject Result = null;
    JSONParser jsonParser = new JSONParser();
    private Context context;
    private final String URL_AddUser = "/mobile/api/users/signup";
    private ProgressDialog progressDialog;
    private String name = "";
    private String email = "";
    private String password = "";
    private Boolean isShop = false;
    private Boolean isSuccess = false;
    private String message = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup_fragment, container, false);

        ButterKnife.inject(this, v);
        context = getContext();

        viewPager = getActivity().findViewById(R.id.viewpager);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                // Finish the registration screen and return to the Login activity
            }
        });

        return v;
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(getContext(),
                R.style.ThemeOverlay_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        isShop = _isShopKeeper.isChecked();

        new SignUpCall().execute();

    }

    class SignUpCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                message = "";
                URL url = new URL(context.getString(R.string.apiUrl) + URL_AddUser);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userName", email);
                jsonParam.put("firstName", name);
                jsonParam.put("lastName", "");
                jsonParam.put("password", password);
                jsonParam.put("email", email);
                jsonParam.put("phone", "");
                jsonParam.put("address", "");
                jsonParam.put("shopKeeper", isShop);
                jsonParam.put("isActive", true);

                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

                String response = conn.getResponseMessage();

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer res = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                JSONObject jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                if(message.equals("success")) {
                    isSuccess = true;
                    //onSignupSuccess();
                }
                else {
                    isSuccess = false;
                    //onSignupFailed();
                }
                conn.disconnect();

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
            _signupButton.setEnabled(true);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if(isSuccess) {
                        Toast.makeText(context, "Successfully Registered!", Toast.LENGTH_LONG).show();
                        viewPager.setCurrentItem(0);
                    } else {
                        Snackbar.make(getView(), "Failed.\n" + message, Snackbar.LENGTH_SHORT);
                    }
                }
            });
        }
    }


    public void onSignupSuccess() {
        //setResult(RESULT_OK, null);
    }

    public void onSignupFailed() {

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
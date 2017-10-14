package com.dealspok.dealspok.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.Utils.DoubleNameValuePair;
import com.dealspok.dealspok.Utils.IntNameValuePair;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Umi on 11.10.2017.
 */

public class Login extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final int REQUEST_SIGNUP = 0;
    private Context context;
    private ViewPager viewPager;
    private final String URL_Login = "/mobile/api/users/login";
    private String email = "";
    private String password = "";
    private Boolean isSuccess = false;
    private String name ="";
    private ProgressDialog progressDialog;
    private String message = "";


    public Login() {
        // Required empty public constructor
    }

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.login_fragment, container, false);
        context = getContext();

        viewPager = getActivity().findViewById(R.id.viewpager);

        ButterKnife.inject(this, v);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                // Start the Signup fragment
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                SignUp fragment = new SignUp();
//                fragmentTransaction.add(getParentFragment().getId(), fragment);
//                fragmentTransaction.commit();
            }
        });
        return  v;
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(getContext(),
                R.style.ThemeOverlay_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        new LoginCall().execute();
    }

    class LoginCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                URL url = new URL(context.getString(R.string.apiUrl) + URL_Login);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

//                StringBuilder encodedUrl = new StringBuilder("username=" + URLEncoder.encode(email, "UTF-8"));
//                encodedUrl.append("&password=" + URLEncoder.encode(password, "UTF-8"));
//
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//
//                params.add(new BasicNameValuePair("username", email));
//                params.add(new BasicNameValuePair("password", password));

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("password", password);
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

                JSONObject jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                conn.disconnect();

                if(message.equals("successfull")) {
                    isSuccess = true;
                    String firstName = jObject.getString("firstName");
                    String lastName = jObject.getString("lastName");
                    name = firstName + " " + lastName;
                }
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
            _loginButton.setEnabled(true);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if(isSuccess) {
                        Toast.makeText(context, "Welcome " + name, Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(context,  "Login failed\n" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid username or email address");
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
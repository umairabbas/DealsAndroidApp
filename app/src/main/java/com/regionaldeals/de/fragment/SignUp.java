package com.regionaldeals.de.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.Utils.PrefsHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Umi on 11.10.2017.
 */

public class SignUp extends Fragment {

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    private ViewPager viewPager;
    private Context context;
    private final String URL_AddUser = "/web/users/signup";
    private ProgressDialog progressDialog;
    private String name = "";
    private String email = "";
    private String password = "";
    private Boolean isShop = false;
    private Boolean isSuccess = false;
    private String message = "";
    private String displayMsg = "";
    private JSONObject jObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup_fragment, container, false);

        ButterKnife.bind(this, v);
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
                displayMsg = "";
                URL url = new URL(context.getString(R.string.apiUrl) + URL_AddUser);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
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
                jsonParam.put("merchant", isShop);
                jsonParam.put("active", true);

                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

                String response = conn.getResponseMessage();

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer res = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                if (message.equals(getString(R.string.SIGNUP_OK))) {
                    isSuccess = true;
                    displayMsg = "Successfully Registered";
                    //onSignupSuccess();
                } else if (message.equals(getString(R.string.SIGNUP_ERR_EMAIL_TAKEN))) {
                    isSuccess = false;
                    displayMsg = "Error! Email already taken";
                    //onSignupSuccess();
                } else if (message.equals(getString(R.string.SIGNUP_ERR_USERNAME_TAKEN))) {
                    isSuccess = false;
                    displayMsg = "Error! Username already taken";
                    //onSignupSuccess();
                } else {
                    isSuccess = false;
                    displayMsg = "Failed! Server error";
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            _signupButton.setEnabled(true);
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (isSuccess) {

                        Toast.makeText(context, displayMsg, Toast.LENGTH_LONG).show();

                        try {

                            JSONObject data = jObject.getJSONObject("data");
                            PrefsHelper prefHelper = PrefsHelper.Companion.getInstance(context);
                            prefHelper.updateUser(context, data.toString());

                            Intent intent = getActivity().getIntent();
                            intent.putExtra("userEmail", data.getString("email"));
                            intent.putExtra("userId", data.getInt("userId"));
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, displayMsg, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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
            _nameText.setError(getString(R.string.valid_name));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.valid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getString(R.string.valid_pass));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
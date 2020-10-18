package com.regionaldeals.de.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.PrefsHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import kotlin.Pair;


/**
 * Created by Umi on 11.10.2017.
 */

public class Login extends Fragment {

    private Context context;
    private ViewPager viewPager;
    private final String URL_Login = "/web/users/login";
    private String email = "";
    private String password = "";
    private Boolean isSuccess = false;
    private String name = "";
    private ProgressDialog progressDialog;
    private String message = "";
    private JSONObject jObject;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    TextView _passowrdLink;

    private final DealsDataProvider dealsDataProvider = new DealsDataProvider();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_fragment, container, false);
        context = getContext();


        _emailText = v.findViewById(R.id.input_email);
        _passwordText = v.findViewById(R.id.input_password);
        _loginButton = v.findViewById(R.id.btn_login);
        _signupLink = v.findViewById(R.id.link_signup);
        _passowrdLink = v.findViewById(R.id.link_passowrd);

        viewPager = getActivity().findViewById(R.id.viewpager);

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
            }
        });

        _passowrdLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createDialogue();
            }
        });

        return v;
    }

    public void createDialogue() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogue_password, null);
        final EditText mEmailText = mView.findViewById(R.id.input_email);
        mBuilder.setTitle(R.string.passwort_vergessen);
        mBuilder.setView(mView);
        mBuilder.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (mEmailText.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailText.getText().toString()).matches()) {
                    Toast.makeText(getContext(), getString(R.string.valid_email), Toast.LENGTH_SHORT).show();
                } else {
                    dialogInterface.dismiss();
                    Pair<String, String> email = new Pair("email", mEmailText.getText().toString());
                    List<Pair<String, String>> formData = new ArrayList<>();
                    formData.add(email);
                    dealsDataProvider.postPasswordReset("/web/users/forgetpassword", formData);
                        Toast.makeText(getContext(), getString(R.string.email_reset_ins), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void login() {

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
                message = "";
                URL url = new URL(context.getString(R.string.apiUrl) + URL_Login);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("password", password);
                Log.i("JSON", jsonParam.toString());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

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

                if (message.equals(getString(R.string.LOGIN_OK))) {
                    isSuccess = true;
                    String firstName = jObject.getString("firstName");
                    String lastName = jObject.getString("lastName");
                    name = firstName + " " + lastName;
                } else if (message.equals(getString(R.string.LOGIN_ERR_INVALID_CREDENTIALS))) {
                    isSuccess = false;
                    message = "Invalid Credentials";
                } else {
                    isSuccess = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            _loginButton.setEnabled(true);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (isSuccess) {

                        PrefsHelper prefHelper = PrefsHelper.Companion.getInstance(context);
                        prefHelper.updateUser(context, jObject.toString());

                        Toast.makeText(context, getResources().getString(R.string.welcome) + " " + name, Toast.LENGTH_LONG).show();
                        try {
                            Intent intent = getActivity().getIntent();
                            intent.putExtra("userEmail", jObject.getString("email"));
                            intent.putExtra("userId", jObject.getInt("userId"));
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.login_failed) + "\n" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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
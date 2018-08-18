package com.regionaldeals.de.service;

/**
 * Created by Umi on 14.01.2018.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        this.token = token;
    }

    private String token = "";
//    class RegCall extends AsyncTask<String, String, String> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        protected String doInBackground(String... args) {
//            try {
//                String message = "";
//                URL url = new URL(getApplicationContext().getString(R.string.apiUrl) + "/mobile/api/device/update_device");
//                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "application/json");
//                conn.setRequestProperty("Accept","application/json");
//                conn.setDoOutput(true);
//                conn.setDoInput(true);
//                conn.connect();
//
//                JSONObject jsonParam = new JSONObject();
//
//
//                jsonParam.put("deviceType", "android");
//                jsonParam.put("deviceToken", token);
//                jsonParam.put("deviceUuidImei", UUID.randomUUID().toString());
//                jsonParam.put("deviceAppLanguage", "en");
//                jsonParam.put("deviceLocationLat", email);
//                jsonParam.put("deviceLocationLong", password);
//                jsonParam.put("deviceTimezone", 60);
//
//                Log.i("JSON", jsonParam.toString());
//
//                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
//                os.writeBytes(jsonParam.toString());
//
//                os.flush();
//                os.close();
//
//                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
//                Log.i("MSG" , conn.getResponseMessage());
//
//                BufferedReader in;
//
//                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
//                    in = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//                } else {
//                    in = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
//                }
//                String inputLine;
//                StringBuffer res = new StringBuffer();
//
//                while ((inputLine = in.readLine()) != null) {
//                    res.append(inputLine);
//                }
//                in.close();
//
//                jObject = new JSONObject(res.toString());
//                message = jObject.getString("message");
//
//                conn.disconnect();
//
//                if(message.equals(getString(R.string.LOGIN_OK))) {
//                    isSuccess = true;
//                    String firstName = jObject.getString("firstName");
//                    String lastName = jObject.getString("lastName");
//                    name = firstName + " " + lastName;
//                }
//                else if (message.equals(getString(R.string.LOGIN_ERR_INVALID_CREDENTIALS))){
//                    isSuccess = false;
//                    message = "Invalid Credentials";
//                }
//                else {
//                    isSuccess = false;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         **/
//        protected void onPostExecute(String file_url) {
//            progressDialog.dismiss();
//            _loginButton.setEnabled(true);
//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    if(isSuccess) {
//
//                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
//                        editor.putString("userObject", jObject.toString());
//                        editor.commit();
//
//                        Toast.makeText(context, "Welcome " + name, Toast.LENGTH_LONG).show();
//                        try {
//                            Intent intent = getActivity().getIntent();
//                            intent.putExtra("userEmail", jObject.getString("email"));
//                            getActivity().setResult(Activity.RESULT_OK, intent);
//                            getActivity().finish();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Toast.makeText(context,  "Login failed\n" + message, Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
//    }
}
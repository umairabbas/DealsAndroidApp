package com.regionaldeals.de;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.fragment.Main;
import com.regionaldeals.de.location.LocationPrediction;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;

import static com.regionaldeals.de.Constants.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private int userId = 0;
    public static final int LOGIN_REQUEST_CODE = 1;
    public static final int SIGNOUT_REQUEST_CODE = 2;
    public static final int SUB_REQUEST_CODE = 3;
    private TextView emailMenu;
    private static boolean shouldRefresh = false;
    private String token = "";
    private String city = "";
    private String InstallationNumber = "";
    private Boolean subscribed = false;
    private Boolean notIconOn = false;
    private MenuItem notMenuItem;
    private String email = "";
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InstallationNumber = Installation.id(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Main();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView loginImg =  header.findViewById(R.id.imageView);

        emailMenu = header.findViewById(R.id.textemail);

        if (getIntent().hasExtra("userCity")) {
            city = getIntent().getStringExtra("userCity");
        }

        String restoredText = SharedPreferenceUtils.getInstance(this).getStringValue(USER_OBJECT_KEY, null);
        String restoredSub = SharedPreferenceUtils.getInstance(this).getStringValue(SUB_OBJECT_KEY, null);
        final String restoredNot = SharedPreferenceUtils.getInstance(this).getStringValue(NOT_TOKEN_KEY, null);
        String restoredCat = SharedPreferenceUtils.getInstance(this).getStringValue(CAT_OBJECT_KEY, null);

        if (restoredText != null) {
            try {
                JSONObject obj = new JSONObject(restoredText);
                userId = obj.getInt("userId");
                email = obj.getString("email");
                emailMenu.setText(email);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (restoredSub != null) {
            subscribed = true;
        }
        if (restoredNot != null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    token = instanceIdResult.getToken();
                    if (!token.equals(restoredNot)) {
                        new RegCall().execute();
                    }
                }
            });
        } else {
            new RegCall().execute();
        }
        if (restoredCat == null) {
            getCatFromServer();
        }

        navigationView.setNavigationItemSelectedListener(this);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        if (getIntent().hasExtra("notificationBody")) {
            String body = getIntent().getStringExtra("notificationBody");
            getIntent().removeExtra("notificationBody");
            Intent startActivityIntent = new Intent(MainActivity.this, NotificationDealsActivity.class);
            startActivityIntent.putExtra("notificationBody", body);
            startActivity(startActivityIntent);
        } else if (getIntent().hasExtra("notificationGut")) {
            //Make notification icon color yellow
            notIconOn = true;
        }

        loginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != 0) {
                    //Already Loged in
                    Intent intent = new Intent(MainActivity.this, SignoutActivity.class);
                    intent.putExtra("uemail", email);
                    shouldRefresh = true;
                    startActivityForResult(intent, SIGNOUT_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    shouldRefresh = true;
                    startActivityForResult(intent, LOGIN_REQUEST_CODE);
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_favourite) {
            ViewPager vp = fragment.getView().findViewById(R.id.view_pager);
            vp.setCurrentItem(5);
        } else if (id == R.id.nav_ort) {
            Intent startActivityIntent = new Intent(MainActivity.this, LocationPrediction.class);
            startActivity(startActivityIntent);
        } else if (id == R.id.nav_benachrichtigungen) {
            Intent startActivityIntent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(startActivityIntent);
        } else if (id == R.id.nav_uberDealSpok) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_appTeilen) {
            ShareCompat.IntentBuilder.from(MainActivity.this)
                    .setType("text/plain")
                    .setChooserTitle("Chooser title")
                    .setText("http://play.google.com/store/apps/details?id=" + getPackageName())
                    .startChooser();
        } else if (id == R.id.abo_buchen) {
            if (!subscribed) {
                Intent intent = new Intent(MainActivity.this, SubscribeNewActivity.class);
                startActivityForResult(intent, SUB_REQUEST_CODE);
                shouldRefresh = true;
            }else {
                Intent intent = new Intent(MainActivity.this, SubscribeActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.meine_anzeigen) {
            if (userId == 0) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                shouldRefresh = true;
            } else if (!subscribed) {
                Snackbar.make(navigationView, getResources().getString(R.string.suberror), Snackbar.LENGTH_LONG).show();
            } else {
                Intent startActivityIntent = new Intent(MainActivity.this, CreateDealsActivity.class);
                startActivity(startActivityIntent);
            }
        } else if (id == R.id.meine_gutscheien) {
            if (userId == 0) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                shouldRefresh = true;
            } else if (!subscribed) {
                Snackbar.make(navigationView, getResources().getString(R.string.suberror), Snackbar.LENGTH_LONG).show();
            } else {
                Intent startActivityIntent = new Intent(MainActivity.this, CreateGutscheineActivity.class);
                startActivity(startActivityIntent);
            }
        } else if (id == R.id.anzeigen_erstellen) {
            if (userId == 0) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                shouldRefresh = true;
            } else if (!subscribed) {
                Snackbar.make(navigationView, getResources().getString(R.string.suberror), Snackbar.LENGTH_LONG).show();
            } else {
                Intent startActivityIntent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(startActivityIntent);
            }
        } else if (id == R.id.dataschnutz) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.regionaldeals.de/#datenschutz"));
            startActivity(browserIntent);
        } else if (id == R.id.agb) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.regionaldeals.de/#agb"));
            startActivity(browserIntent);
        } else if (id == R.id.impressung) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.regionaldeals.de/#impressum"));
            startActivity(browserIntent);
        } else if (id == R.id.funktion) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.regionaldeals.de/app"));
            startActivity(browserIntent);
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container_wrapper, fragment);
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getCatFromServer() {
        AsyncHttpClient androidClient = new AsyncHttpClient();
        RequestParams params = new RequestParams("userid", userId);
        androidClient.get(this.getString(R.string.apiUrl) + "/web/categories/list", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", getString(R.string.token_failed) + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
                Log.d("TAG", "Client token: " + responseToken);
                try {
                    JSONArray catArr = new JSONArray(responseToken);
                    if (catArr != null) {
                        SharedPreferenceUtils.getInstance(MainActivity.this).setValue(CAT_OBJECT_KEY, catArr.toString());
                    } else {
                        Log.d("Deals: ", "null");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("notificationBody")) {
            Intent startNotIntent = new Intent(MainActivity.this, NotificationDealsActivity.class);
            startNotIntent.putExtra("notificationBody", intent.getStringExtra("notificationBody"));
            intent.removeExtra("notificationBody");
            startActivity(startNotIntent);
        } else if (intent.hasExtra("notificationGut")) {
            notIconOn = true;
            notMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_not_check));
            notMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_not_check));
        } else if (intent.hasExtra("subscribed")) {
            subscribed = intent.getBooleanExtra("subscribed", false);
            intent.removeExtra("subscribed");
        }
        if (intent.hasExtra("updateCity")) {
            Boolean body = intent.getBooleanExtra("updateCity", false);
            if (body) {
                if (intent.hasExtra("userCity")) {
                    city = intent.getStringExtra("userCity");
                }
                //update push not.
                new RegCall().execute();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void setShouldRefresh(boolean val) {
        shouldRefresh = val;
    }


    public boolean getShouldRefresh() {
        return shouldRefresh;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        notMenuItem = menu.findItem(R.id.notification);
        if (notIconOn) {
            notMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_not_check));
        }
        return true;
    }

    private Tooltip mTooltip;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notification) {
            String text = "Keine neuen Benachrichtigungen";
            if (notIconOn) {
                //text = "Sie haben eine neue Benachrichtigung.\nBitte klicken Sie hier, um zu sehen.";
                notMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_notifications_none_white_24dp));
                notIconOn = false;
                Intent startActivityIntent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(startActivityIntent);
                return true;
            }

            if (mTooltip == null) {
                mTooltip = new Tooltip.Builder(findViewById(R.id.notification), R.style.Tooltip)
                        .setDismissOnClick(true)
                        .setGravity(Gravity.BOTTOM)
                        .setPadding(R.dimen.tile_padding)
                        .setText(text)
                        .show();
            } else {
                if (mTooltip.isShowing()) {
                    mTooltip.dismiss();
                } else {
                    mTooltip.show();
                }
            }
            item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_notifications_none_white_24dp));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                email = data.getStringExtra("userEmail");
                userId = data.getIntExtra("userId", 0);
                emailMenu.setText(email);
                //update Subscriptions
                getSubscription(userId);
                new RegCall().execute();
            }
        } else if (requestCode == SIGNOUT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferenceUtils.getInstance(this).removeKey(USER_OBJECT_KEY);
                SharedPreferenceUtils.getInstance(this).removeKey(SUB_OBJECT_KEY);
                userId = 0;
                subscribed = false;
                emailMenu.setText(getResources().getString(R.string.login_signup));
                new RegCall().execute();
            }
        } else if (requestCode == SUB_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra("userEmail");
                userId = data.getIntExtra("userId", 0);
                emailMenu.setText(email);
                getSubscription(userId);
            }
        }


    }

    private void getSubscription(final int user) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient androidClient = new AsyncHttpClient();
                androidClient.get(getString(R.string.apiUrl) + "/web/subscriptions/subscription?userid=" + Integer.toString(user), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("TAG", getString(R.string.token_failed) + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        Log.d("TAG", "Client token: " + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            String msg = obj.getString("message");
                            if (msg.equals("PLANS_SUBSCRIPTIONS_OK")) {
                                subscribed = true;
                                JSONObject data = obj.getJSONObject("data");
                                //JSONObject plan = data.getJSONObject("plan");
                                SharedPreferenceUtils.getInstance(MainActivity.this).setValue(SUB_OBJECT_KEY, data.toString());
                            } else if (msg.equals("PLANS_SUBSCRIPTIONS_NILL")) {
                                subscribed = false;
                                SharedPreferenceUtils.getInstance(MainActivity.this).removeKey(SUB_OBJECT_KEY);
                            }
                            //should never come
                            else {
                                subscribed = false;
                                SharedPreferenceUtils.getInstance(MainActivity.this).removeKey(SUB_OBJECT_KEY);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Throwable t) {
                        }
                    }
                });
            }
        };
        mainHandler.post(myRunnable);
    }

    class RegCall extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            if (city.isEmpty()) {

                String restoredText = SharedPreferenceUtils.getInstance(MainActivity.this).getStringValue(LOCATION_KEY, null);
                if (restoredText != null) {
                    try {
                        JSONObject obj = new JSONObject(restoredText);
                        city = obj.getString("Name");
                    } catch (Exception e) {
                    }
                }
            }

            try {
                String message = "";
                URL url = new URL(getApplicationContext().getString(R.string.apiUrl) + "/web/device/update_device");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("deviceType", "android");
                jsonParam.put("deviceToken", token);
                jsonParam.put("deviceUuidImei", InstallationNumber);
                jsonParam.put("deviceAppLanguage", Locale.getDefault().getLanguage());
//                jsonParam.put("deviceLocationLat", lat);
//                jsonParam.put("deviceLocationLong", lng);
                jsonParam.put("deviceCity", city);
                jsonParam.put("deviceTimezone", 60);
                if (userId == 0) {
                    jsonParam.put("userId", null);
                } else {
                    jsonParam.put("userId", userId);
                }
                Log.i("JSON", jsonParam.toString());

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.write(jsonParam.toString().getBytes("UTF-8"));

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

                JSONObject jObject = new JSONObject(res.toString());
                message = jObject.getString("message");
                int status = jObject.getInt("status");
                conn.disconnect();

                if (status == 200 && message.equals("DEVICE_DATA_UPDATE_OK")) {
                    SharedPreferenceUtils.getInstance(MainActivity.this).setValue(NOT_TOKEN_KEY, token);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}

package com.regionaldeals.de;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.regionaldeals.de.fragment.Main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private Context context;
    private Activity activity;
    private int userId = 0;
    public static final int LOGIN_REQUEST_CODE = 1;
    private TextView emailMenu;
    private static boolean shouldRefresh = false;
    static public final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1234;
    private double dlat = 0.0;
    private double dlng = 0.0;
    private String token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        activity = this;

        //GoogleApiAvailability.makeGooglePlayServicesAvailable();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Main();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView loginImg = (ImageView) header.findViewById(R.id.imageView);
        loginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                shouldRefresh = true;
            }
        });

        emailMenu = (TextView)header.findViewById(R.id.textemail);

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("userObject", null);
        if (restoredText != null) {
            try {
                JSONObject obj = new JSONObject(restoredText);
                userId = obj.getInt("userId");
                String email = obj.getString("email");
                emailMenu.setText(email);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable t) {
            }
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_favourite) {
                    ViewPager vp = fragment.getView().findViewById(R.id.view_pager);
                    vp.setCurrentItem(5);
                } else if (id == R.id.nav_ort) {
                    Intent startActivityIntent = new Intent(MainActivity.this, GooglePlacesAutocompleteActivity.class);
                    startActivity(startActivityIntent);
                }
                else if (id == R.id.nav_benachrichtigungen) {
                //    fragment = new Deals();
                    Snackbar.make(navigationView, "Coming soon.", Snackbar.LENGTH_SHORT).show();
                }
                else if (id == R.id.nav_einstellungen) {
                    //    fragment = new Deals();
                    Snackbar.make(navigationView, "Coming soon.", Snackbar.LENGTH_SHORT).show();
                }
                else if (id == R.id.nav_hilfe) {
                //    fragment = new Deals();
                    Snackbar.make(navigationView, "Coming soon.", Snackbar.LENGTH_SHORT).show();
                }
                else if (id == R.id.nav_uberDealSpok) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_appTeilen) {
                //    fragment = new Deals();
                    ShareCompat.IntentBuilder.from(activity)
                            .setType("text/plain")
                            .setChooserTitle("Chooser title")
                            .setText("http://play.google.com/store/apps/details?id=" + activity.getPackageName())
                            .startChooser();
                }
                else if (id == R.id.abo_buchen) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent intent = new Intent(MainActivity.this, SubscribeActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }

                }
                else if (id == R.id.meine_anzeigen) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent startActivityIntent = new Intent(MainActivity.this, CreateDealsActivity.class);
                        startActivity(startActivityIntent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }
                }
                else if (id == R.id.meine_gutscheien) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent startActivityIntent = new Intent(MainActivity.this, CreateGutscheineActivity.class);
                        startActivity(startActivityIntent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }
                }
                else if (id == R.id.anzeigen_erstellen) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent startActivityIntent = new Intent(MainActivity.this, ShopActivity.class);
                        startActivity(startActivityIntent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container_wrapper, fragment);
                transaction.commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        String id = UUID.randomUUID().toString();
        token = FirebaseInstanceId.getInstance().getToken();
        new RegCall().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(context, "IMPORTANT: The app is currently using dummy data and will be live on 1st Feb 2018.", Toast.LENGTH_LONG).show();
        if(shouldRefresh){
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
            String restoredText = prefs.getString("userObject", null);
            if (restoredText != null) {
                try {
                    JSONObject obj = new JSONObject(restoredText);
                    userId = obj.getInt("userId");
                    String email = obj.getString("email");
                    emailMenu.setText(email);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            Toast.makeText(context, "Coming soon.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra("userEmail");
                emailMenu.setText(email);
            }
        }
    }

    public void getLocation(Context context) {
        int status = context.getPackageManager().checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                context.getPackageName());
        if (status == PackageManager.PERMISSION_GRANTED) {
            LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = mgr.getAllProviders();
            if (providers != null && providers.contains(LocationManager.NETWORK_PROVIDER)) {
                Location loc = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    dlat = loc.getLatitude();
                    dlng = loc.getLongitude();
                }
            }
        }else{
            ActivityCompat.requestPermissions( activity, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    getLocation(context);
                } else {
                    Toast.makeText(context, "Cannot get user location", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    class RegCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getLocation(context);
        }
        protected String doInBackground(String... args) {
            try {
                String message = "";
                URL url = new URL(getApplicationContext().getString(R.string.apiUrl) + "/mobile/api/device/update_device");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("deviceType", "android");
                jsonParam.put("deviceToken", token);
                jsonParam.put("deviceUuidImei", UUID.randomUUID().toString());
                jsonParam.put("deviceAppLanguage", "en");
                jsonParam.put("deviceLocationLat", dlat);
                jsonParam.put("deviceLocationLong", dlng);
                jsonParam.put("deviceTimezone", 60);

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
                int status =  jObject.getInt("status");
                conn.disconnect();

                if(status==200){
                    Toast.makeText(context, "Server notification activated", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
        }
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}

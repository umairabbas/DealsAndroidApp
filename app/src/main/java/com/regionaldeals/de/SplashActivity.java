package com.regionaldeals.de;

/**
 * Created by Umi on 28.08.2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.regionaldeals.de.Utils.DoubleNameValuePair;
import com.regionaldeals.de.Utils.IntNameValuePair;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.entities.CitiesObject;
import com.regionaldeals.de.entities.DealObject;
import com.regionaldeals.de.fragment.Deals;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private JSONParser jsonParser = new JSONParser();
    private Context context;
    private Activity activity;
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 99;
    private final String URL_Cities = "/mobile/api/device/citieslist";
    private JSONArray data;
    private String[] COUNTRIES;
    private String message = "";
    private StringBuilder sb;
    private List<CitiesObject> city =  new ArrayList<>();
    //private CitiesObject city = new TypeToken<ArrayList<CitiesObject>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        context = this;
        activity = this;
        if(null != actionBar){
            actionBar.hide();
        }

        getLocation();
        //        if (getIntent().getExtras() != null) {
//            for (String key : getIntent().getExtras().keySet()) {
//                Object value = getIntent().getExtras().get(key);
//                Log.d(TAG, "Key: " + key + " Value: " + value);
//            }
//        }
    }

    class loadCities extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            sb = new StringBuilder();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(context.getString(R.string.apiUrl) + URL_Cities, "GET",
                    params);
            Log.d("JSON: ", "> " + json);
            try {
                JSONObject jsonObj = new JSONObject(json);
                data = jsonObj.getJSONArray("data");
                message =  jsonObj.getString("message");
                city.clear();
                COUNTRIES = new String[data.length()];
                for(int i=0; i<data.length(); i++){
                    JSONObject result = (JSONObject) data.get(i);
                    CitiesObject c = new CitiesObject();
                    c.setCityName(result.getString("cityName"));
                    c.setCountryCode(result.getString("countryCode"));
                    c.setId(result.getInt("id"));
                    COUNTRIES[i] = result.getString("cityName");
                    sb.append(COUNTRIES[i]).append(",");
                    if(!result.isNull("cityLat") && !result.isNull("cityLong")) {
                        c.setCityLat(result.getDouble("cityLat"));
                        c.setCityLong(result.getDouble("cityLong"));
                    }
                    city.add(c);
                }
                Arrays.sort(COUNTRIES);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            if(message.equals("CITIES_LIST_OK") && COUNTRIES.length > 1){
                //all good
            } else {
                Arrays.sort(DEFAULTCOUNTRIES);
                for(int i=0; i< DEFAULTCOUNTRIES.length; i++){
                    sb.append(DEFAULTCOUNTRIES[i]).append(",");
                }
            }

            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
            editor.putString("citiesString", sb.toString());
            Gson gson = new Gson();
            String json = gson.toJson(city);
            editor.putString("citiesObject", json);
            editor.commit();

            runOnUiThread(new Runnable() {
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, LocationManual.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            });
        }
    }

    public void getLocation() {
        int status = context.getPackageManager().checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                context.getPackageName());
        if (status == PackageManager.PERMISSION_GRANTED) {
            nextFlow();
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
                    getLocation();
                } else {
                    Toast.makeText(context, "Cannot get user location", Toast.LENGTH_SHORT).show();
                    nextFlow();
                }
                return;
            }
        }
    }


    private void nextFlow(){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);
        String restoredCities = prefs.getString("citiesString", null);
//        if (restoredCities == null) {
//            new SplashActivity.loadCities().execute();
//        }
        if (restoredText != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                    if (getIntent().hasExtra("notificationBody")) {
                        String body = getIntent().getStringExtra("notificationBody");
                        startActivityIntent.putExtra("notificationBody", body);
                    } else if (getIntent().hasExtra("dealids")) {     //should be redirect = true
                        String body = getIntent().getStringExtra("dealids");
                        startActivityIntent.putExtra("notificationBody", body);
                    }
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }else if(restoredCities != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, LocationManual.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new SplashActivity.loadCities().execute();
                }
            }, SPLASH_DISPLAY_LENGTH - 500);
        }
    }

    // an array with countries to display in the list
    private static String[] DEFAULTCOUNTRIES = new String[]
            {
                    "Aachen",
                    "Augsburg",
                    "Bergisch",
                    "Berlin",
                    "Bielefeld",
                    "Bochum",
                    "Bonn",
                    "Bottrop",
                    "Braunschweig",
                    "Bremen",
                    "Bremerhaven",
                    "Chemnitz",
                    "Cottbus",
                    "Darmstadt",
                    "Dessau-Roßlau",
                    "Dortmund",
                    "Dresden",
                    "Duisburg",
                    "Düren",
                    "Düsseldorf",
                    "Erfurt",
                    "Erlangen",
                    "Essen",
                    "Esslingen",
                    "Flensburg",
                    "Frankfurt",
                    "Freiburg",
                    "Fürth",
                    "Gelsenkirchen",
                    "Gera",
                    "Gladbach",
                    "Göttingen",
                    "Gütersloh",
                    "Hagen",
                    "Halle",
                    "Hamburg",
                    "Hamm",
                    "Hanau",
                    "Hannover",
                    "Heidelberg",
                    "Heilbronn",
                    "Herne",
                    "Hildesheim",
                    "Ingolstadt",
                    "Iserlohn",
                    "Jena",
                    "Kaiserslautern",
                    "Karlsruhe",
                    "Kassel",
                    "Kiel",
                    "Koblenz",
                    "Krefeld",
                    "Köln",
                    "Leipzig",
                    "Leverkusen",
                    "Ludwigsburg",
                    "Ludwigshafen",
                    "Lübeck",
                    "Lünen",
                    "Magdeburg",
                    "Mainz",
                    "Mannheim",
                    "Marl",
                    "Minden",
                    "Moers",
                    "Mönchengladbach",
                    "Mülheim",
                    "München",
                    "Münster",
                    "Neuss",
                    "Nürnberg",
                    "Oberhausen",
                    "Offenbach",
                    "Oldenburg",
                    "Osnabrück",
                    "Paderborn",
                    "Pforzheim",
                    "Potsdam",
                    "Ratingen",
                    "Recklinghausen",
                    "Regensburg",
                    "Remscheid",
                    "Reutlingen",
                    "Rostock",
                    "Saarbrücken",
                    "Salzgitter",
                    "Schwerin",
                    "Siegen",
                    "Solingen",
                    "Stuttgart",
                    "Trier",
                    "Tübingen",
                    "Ulm",
                    "Velbert",
                    "Villingen-Schwenn.",
                    "Wiesbaden",
                    "Witten",
                    "Wolfsburg",
                    "Wuppertal",
                    "Würzburg",
                    "Zwickau"
            };

}
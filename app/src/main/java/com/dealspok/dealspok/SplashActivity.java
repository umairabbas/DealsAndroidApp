package com.dealspok.dealspok;

/**
 * Created by Umi on 28.08.2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);

        if (restoredText != null) {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent startActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }else {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent startActivityIntent = new Intent(SplashActivity.this, GooglePlacesAutocompleteActivity.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
}
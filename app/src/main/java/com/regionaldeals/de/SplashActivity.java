package com.regionaldeals.de;

/**
 * Created by Umi on 28.08.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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
        final Context context = this;
        if(null != actionBar){
            actionBar.hide();
        }

        //        if (getIntent().getExtras() != null) {
//            for (String key : getIntent().getExtras().keySet()) {
//                Object value = getIntent().getExtras().get(key);
//                Log.d(TAG, "Key: " + key + " Value: " + value);
//            }
//        }

        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);

        if (restoredText != null) {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent startActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                    if (getIntent().hasExtra("notificationBody")) {
                        String body = getIntent().getStringExtra("notificationBody");
                        //Toast.makeText(context, "notificationBody: " + body, Toast.LENGTH_LONG).show();
                        startActivityIntent.putExtra("notificationBody", body);
                    }else if (getIntent().hasExtra("dealids")){     //should be redirect = true
                        String body = getIntent().getStringExtra("dealids");
                        //Toast.makeText(context, "notificationBody: " + body, Toast.LENGTH_LONG).show();
                        startActivityIntent.putExtra("notificationBody", body);
                    }
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
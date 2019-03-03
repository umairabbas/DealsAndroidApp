package com.regionaldeals.de;

/**
 * Created by Umi on 28.08.2017.
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.location.LocationPrediction;

import static com.regionaldeals.de.Constants.CITIES_KEY;
import static com.regionaldeals.de.Constants.LOCATION_KEY;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferenceUtils.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

        Boolean dataSecurityAccepted = SharedPreferenceUtils.getInstance(this).getBoolanValue("dataSecurity", false);

        if(dataSecurityAccepted) {
            getLocation();
        } else {
            createDialogue();
        }

    }
    public void createDialogue() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SplashActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialogue_checkbox, null);
        CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
        TextView tv = mView.findViewById(R.id.tvDiaogue);
        //tv.setText(Html.fromHtml(getString(R.string.datahtml)));
        tv.setText(getString(R.string.dataSecurity));
        mBuilder.setTitle(R.string.data_privacy);
        mBuilder.setView(mView);
        mBuilder.setPositiveButton(getString(R.string.continue_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
                editor.putBoolean("dataSecurity", true);
                editor.commit();
                getLocation();
            }
        });

        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        mDialog.setCanceledOnTouchOutside(false);

        ((AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    ((AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(true);
                }else{
                    ((AlertDialog) mDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(false);
                }
            }
        });
    }


    public void getLocation() {
        int status = getPackageManager().checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                getPackageName());
        if (status == PackageManager.PERMISSION_GRANTED) {
            nextFlow();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
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
                    Toast.makeText(this, "Cannot get user location", Toast.LENGTH_SHORT).show();
                    nextFlow();
                }
                return;
            }
        }
    }


    private void nextFlow() {

        String restoredText = SharedPreferenceUtils.getInstance(this).getStringValue(LOCATION_KEY, null);
        String restoredCities = SharedPreferenceUtils.getInstance(this).getStringValue(CITIES_KEY, null);

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
                    } else if (getIntent().hasExtra("notificationGut")) {
                        String body = getIntent().getStringExtra("notificationGut");
                        startActivityIntent.putExtra("notificationGut", body);
                    } else if (getIntent().hasExtra("gutscheinid")) {
                        String body = getIntent().getStringExtra("notificationGut");
                        startActivityIntent.putExtra("notificationGut", body);
                    }
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else if (restoredCities != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, LocationPrediction.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent startActivityIntent = new Intent(SplashActivity.this, LocationPrediction.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();

                }
            }, SPLASH_DISPLAY_LENGTH - 500);
        }
    }

}
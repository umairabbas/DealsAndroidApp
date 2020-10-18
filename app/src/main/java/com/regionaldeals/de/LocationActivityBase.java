package com.regionaldeals.de;

/**
 * Created by eumahay on 30/8/2017.
 */

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

public class LocationActivityBase extends FragmentActivity {

    public static final String TAG = "LocationActivityBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }
    
    public void initializeLogging() {
    }
}
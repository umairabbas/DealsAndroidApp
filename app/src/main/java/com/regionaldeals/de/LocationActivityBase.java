package com.dealspok.dealspok;

/**
 * Created by eumahay on 30/8/2017.
 */

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class LocationActivityBase extends FragmentActivity {

    public static final String TAG = "LocationActivityBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onStart() {
        super.onStart();
        initializeLogging();
    }

    /** Set up targets to receive log data */
    public void initializeLogging() {
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
//        LogWrapper logWrapper = new LogWrapper();
//        Log.setLogNode(logWrapper);
//
//        Log.i(TAG, "Ready");
    }
}
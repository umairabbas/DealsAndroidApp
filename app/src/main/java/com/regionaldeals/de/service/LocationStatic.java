package com.regionaldeals.de.service;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.List;

/**
 * Created by Umi on 15.02.2018.
 */

public class LocationStatic extends Service implements LocationListener
{

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    boolean isPassiveEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    public static double latitude = 0.0; // latitude
    public static double longitude = 0.0; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public LocationStatic(){

    }

    public LocationStatic(Context context)
    {
        getLocation(context);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null) {
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void getLocation(Context context) {
        try {
            int status = context.getPackageManager().checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    context.getPackageName());
            if (status == PackageManager.PERMISSION_GRANTED) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = locationManager.getAllProviders();
                Location bestLocation = null;
                for (String provider : providers) {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                }
                if (bestLocation == null) {
                    isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    isPassiveEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);
                    }
                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);
                    }
//                    if (isPassiveEnabled) {
//                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);
//                    }
                } else {
                    latitude = bestLocation.getLatitude();
                    longitude = bestLocation.getLongitude();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

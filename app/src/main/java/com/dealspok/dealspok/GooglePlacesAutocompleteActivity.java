package com.dealspok.dealspok;

/**
 * Created by eumahay on 30/8/2017.
 */

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class GooglePlacesAutocompleteActivity extends LocationActivityBase {
    /**
     * Request code for the autocomplete activity. This will be used to identify results from the
     * autocomplete activity in onActivityResult.
     *
     * https://github.com/googlesamples/android-play-places
     */
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private TextView mPlaceDetailsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

        Button skipButton = (Button) findViewById(R.id.skip);
        Button submitButton = (Button) findViewById(R.id.sumbit);

        // Open the autocomplete activity when the button is clicked.
        Button openButton = (Button) findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
                String restoredText = prefs.getString("locationObject", null);
                if (restoredText != null) {
                    Intent startActivityIntent = new Intent(GooglePlacesAutocompleteActivity.this, MainActivity.class);
                    startActivity(startActivityIntent);
                    GooglePlacesAutocompleteActivity.this.finish();
                }
                else {
                    Snackbar mySnackbar = Snackbar.make(view, R.string.locationErrorMsg, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar mySnackbar = Snackbar.make(view, "Kindly select", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                Intent startActivityIntent = new Intent(GooglePlacesAutocompleteActivity.this, LocationManual.class);
                startActivity(startActivityIntent);
                GooglePlacesAutocompleteActivity.this.finish();
            }
        });
        // Retrieve the TextViews that will display details about the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("locationObject", null);

        if (restoredText != null) {

            try {
                JSONObject obj = new JSONObject(restoredText);
                String locationName = obj.getString("Name");
                String locationAddress = obj.getString("Address");
                if(!locationName.isEmpty() && !locationAddress.isEmpty()) {
                    mPlaceDetailsText.setText( "Name: " + locationName + "\n" +  "Address: " + locationAddress);
                }
            } catch (Throwable t) {
            }

        }
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            //Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                //Log.i(TAG, "Place Selected: " + place.getName());

                mPlaceDetailsText.setText( "Name: \"" + place.getName() + "\n" +  "Address: " + place.getAddress());

                            // MY_PREFS_NAME - a static String variable like:

                LatLng latlng = place.getLatLng();

                String placeJson = "{\"Name\":\"" + place.getName()
                        + "\", \"Address\": \"" + place.getAddress()
                        + "\", \"lat\": \"" + latlng.latitude
                        + "\", \"lng\": \"" + latlng.longitude + "\"}";

                //Gson gson = new Gson();
                //String json = gson.toJson(placeJson);
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE).edit();
                editor.putString("locationObject", placeJson);
                editor.commit();

                // Format the place's details and display them in the TextView.
//                mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                        place.getId(), place.getAddress(), place.getPhoneNumber(),
//                        place.getWebsiteUri()));

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                //Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        //Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
        //        websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }
}
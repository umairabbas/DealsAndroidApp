package com.regionaldeals.de;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.regionaldeals.de.adapter.DropDownListAdapter;
import com.regionaldeals.de.entities.Shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Umi on 28.10.2017.
 */

public class AddShopActivity extends AppCompatActivity{

    @BindView(com.regionaldeals.de.R.id.input_name)
    EditText _nameText;
    @BindView(com.regionaldeals.de.R.id.input_details)
    EditText _detailText;
    @BindView(com.regionaldeals.de.R.id.input_contact)
    EditText _contactText;
    //    @BindView(R.id.input_address)
//    EditText _addressText;
    @BindView(com.regionaldeals.de.R.id.input_tax)
    EditText _taxText;
    @BindView(com.regionaldeals.de.R.id.btn_shop_submit)
    Button _shopButton;
    @BindView(com.regionaldeals.de.R.id.btn_shop_del)
    Button _delButton;
    @BindView(com.regionaldeals.de.R.id.input_place)
    EditText _placeText;

    private final String URL_Login = "/mobile/api/shops/upload-shop";
    private final String URL_ShopDel = "/mobile/api/shops/deactivate";
    private ProgressDialog progressDialog;
    private String name = "";
    private String contact = "";
    private String address = "";
    private String tax = "";
    private String desc = "";
    private String userId = "";
    private Context context;
    private String message = "";
    private JSONObject jObject;
    private Boolean isSuccess = false;
    private Activity activity;
    private int PLACE_PICKER_REQUEST = 1;
    private double lat = 0;
    private double lng = 0;
    private Boolean isEdit = false;
    private int shopId = -1;
    private Boolean shopActive = false;
    private String shopCity = "";
    private String shopCountry = "";
    private Geocoder mGeocoder;
//    private Spinner shopCitySpinner;
    private static String[] COUNTRIES;

    private PopupWindow pw;
    private boolean expanded;        //to  store information whether the selected values are displayed completely or in shortened representatn
    public static boolean[] checkSelected;    // store select/unselect information about the values in the list
    private JSONArray catArr;
    private ArrayList<String> items = new ArrayList<String>();
    private String selectedCat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.regionaldeals.de.R.layout.add_shop_activity);
        context = this;
        activity = this;
        isEdit = false;

//        shopCitySpinner = (Spinner) findViewById(R.id.spinner_city);

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
//        String restoredCities = prefs.getString("citiesString", null);
        String restoredCat = prefs.getString("categoriesObj", null);


//        if (restoredCities == null) {
//            //should not be
//            Toast.makeText(this, "Please restart or update app", Toast.LENGTH_LONG).show();
//            finish();
//        } else {
//            COUNTRIES = restoredCities.split(",");
//        }

//        Arrays.sort(COUNTRIES);
//        //Adapter Deals
//        ArrayAdapter<String> adapterDeals = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, COUNTRIES);
//        adapterDeals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        shopCitySpinner.setAdapter(adapterDeals);
//        shopCitySpinner.setOnItemSelectedListener(this);

        ButterKnife.bind(this);

        Shop editShop = (Shop) getIntent().getSerializableExtra("EXTRA_SHOP_OBJ");

        mGeocoder = new Geocoder(this, Locale.getDefault());

        if (editShop != null) {
            isEdit = true;
            lat = Double.parseDouble(editShop.getShopLocationLat());
            lng = Double.parseDouble(editShop.getShopLocationLong());
            _nameText.setText(editShop.getShopName());
            _detailText.setText(editShop.getShopDetails());
            _contactText.setText(editShop.getShopContact());
            _taxText.setText(editShop.getTaxNumber());
            _placeText.setText(editShop.getShopAddress() + "\n" + lat + ", " + lng);
            address = editShop.getShopAddress() + "\n" + lat + ", " + lng;
            shopActive = editShop.getActive();
            shopId = editShop.getShopId();
            _delButton.setVisibility(View.VISIBLE);
            try {
                getCityNameByCoordinates(lat, lng);
            } catch (IOException e) {
                e.printStackTrace();
            }

            _delButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    progressDialog = new ProgressDialog(context,
                            com.regionaldeals.de.R.style.ThemeOverlay_AppCompat_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Removing...");
                    progressDialog.show();
                    _delButton.setEnabled(false);
                    new ShopDeleteCall().execute();
                }
            });
        }

        _placeText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    GoogleApiAvailability.getInstance().getErrorDialog(activity, e.getConnectionStatusCode(),
                            0 /* requestCode */).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    String message = "Google Play Services is not available: " +
                            GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
                    //Log.e(TAG, message);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        _shopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addShop();
            }
        });

        String restoredUser = prefs.getString("userObject", null);
        try {
            if (restoredUser != null) {
                JSONObject obj = new JSONObject(restoredUser);
                userId = obj.getString("userId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable t) {
        }

        initialize(restoredCat);
    }


//    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//        switch (parent.getId()) {
//            case R.id.spinner_city:
//                shopCity = COUNTRIES[position];
//                break;
//        }
//    }

    /*
     * Function to set up initial settings: Creating the data source for drop-down list, initialising the checkselected[], set the drop-down list
     * */
    private void initialize(String cat) {

        items = new ArrayList<String>();

        try {
            catArr = new JSONArray(cat);
            for (int i = 0; i < catArr.length(); i++) {
                JSONObject catOb = (JSONObject) catArr.get(i);
                String catt = (String) catOb.get("catName");
                items.add(catt);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkSelected = new boolean[items.size()];
        //initialize all values of list to 'unselected' initially
        for (int i = 0; i < checkSelected.length; i++) {
            checkSelected[i] = false;
        }

        /*SelectBox is the TextView where the selected values will be displayed in the form of "Item 1 & 'n' more".
         * When this selectBox is clicked it will display all the selected values
         * and when clicked again it will display in shortened representation as before.
         * */
        final TextView tv = (TextView) findViewById(R.id.SelectBox);
        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!expanded) {
                    //display all selected values
                    String selected = "";
                    int flag = 0;
                    for (int i = 0; i < items.size(); i++) {
                        if (checkSelected[i] == true) {
                            selected += items.get(i);
                            selected += ", ";
                            flag = 1;
                        }
                    }
                    if (flag == 1)
                        tv.setText(selected);
                    expanded = true;
                } else {
                    //display shortened representation of selected values
                    tv.setText(DropDownListAdapter.getSelected());
                    expanded = false;
                }
            }
        });

        //onClickListener to initiate the dropDown list
        Button createButton = (Button) findViewById(R.id.create);
        createButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                initiatePopUp(items, tv);
            }
        });
    }

    /*
     * Function to set up the pop-up window which acts as drop-down list
     * */
    private void initiatePopUp(ArrayList<String> items, TextView tv) {
        LayoutInflater inflater = (LayoutInflater) AddShopActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get the pop-up window i.e.  drop-down layout
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.pop_up_window, (ViewGroup) findViewById(R.id.PopUpView));

        //get the view to which drop-down layout is to be anchored
        RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        pw = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);

        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        pw.setOutsideTouchable(true);
        pw.setHeight(LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        pw.setTouchInterceptor(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        //provide the source layout for drop-down
        pw.setContentView(layout);

        //anchor the drop-down to bottom-left corner of 'layout1'
        pw.showAsDropDown(layout1);

        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.dropDownList);
        DropDownListAdapter adapter = new DropDownListAdapter(this, items, tv);
        list.setAdapter(adapter);
    }

//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//    }

    private void getCityNameByCoordinates(double lat, double lon) throws IOException {

        List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 1);
        if (addresses != null && addresses.size() > 0) {
            shopCity = addresses.get(0).getLocality();
            shopCountry = addresses.get(0).getCountryCode();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latlng = place.getLatLng();
                lat = latlng.latitude;
                lng = latlng.longitude;
                address = place.getAddress().toString();
                _placeText.setText(address + "\n" + lat + ", " + lng);

                try {
                    getCityNameByCoordinates(lat, lng);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addShop() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _shopButton.setEnabled(false);

        progressDialog = new ProgressDialog(this,
                com.regionaldeals.de.R.style.ThemeOverlay_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding...");
        progressDialog.show();

        name = _nameText.getText().toString();
        contact = _contactText.getText().toString();
        tax = _taxText.getText().toString();
        desc = _detailText.getText().toString();

        new ShopCall().execute();
    }

    class ShopCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                message = "";
                URL url = new URL(getString(com.regionaldeals.de.R.string.apiUrl) + URL_Login + "?userid=" + userId);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                if (isEdit && shopId != -1) {
                    jsonParam.put("shopId", shopId);
                    jsonParam.put("active", shopActive);
                } else {
                    jsonParam.put("shopId", null);
                    jsonParam.put("active", false);
                }
                jsonParam.put("shopName", name);
                jsonParam.put("shopAddress", address);
                jsonParam.put("shopCity", shopCity);
                jsonParam.put("shopCountry", shopCountry);
                jsonParam.put("shopLocationLat", lat);
                jsonParam.put("shopLocationLong", lng);
                jsonParam.put("shopContact", contact);
                jsonParam.put("shopDetails", desc);
                jsonParam.put("taxNumber", tax);
                jsonParam.put("shopCategories", selectedCat);

                Log.i("JSON", jsonParam.toString());

                String normalizedString = jsonParam.toString();
                //normalizedString = Normalizer.normalize(normalizedString, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.write(normalizedString.getBytes("UTF-8"));

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

                jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                conn.disconnect();

                if (message.equals(getString(com.regionaldeals.de.R.string.SHOPS_UPLOAD_OK))) {
                    isSuccess = true;
                }
//                else if (message.equals(getString(R.string.LOGIN_ERR_INVALID_CREDENTIALS))){
//                    isSuccess = false;
//                    message = "Invalid Credentials";
//                }
                else {
                    isSuccess = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            _shopButton.setEnabled(true);
            runOnUiThread(new Runnable() {
                public void run() {
                    if (isSuccess) {
                        Toast.makeText(context, "Shop Added\n" + message, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(context, "Failed\n" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    class ShopDeleteCall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                message = "";
                URL url = new URL(getString(com.regionaldeals.de.R.string.apiUrl) + URL_ShopDel + "?userid=" + userId +
                        "&shopid=" + shopId);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

                String response = conn.getResponseMessage();

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer res = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                JSONObject jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                if (message.equals(getString(com.regionaldeals.de.R.string.SHOPS_REMOVE_OK))) {
                    isSuccess = true;
                } else if (message.equals(getString(com.regionaldeals.de.R.string.SHOPS_REMOVE_ERR))) {
                    isSuccess = false;
                    message = "Cannot remove shop";
                } else {
                    isSuccess = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            _delButton.setEnabled(true);
            runOnUiThread(new Runnable() {
                public void run() {
                    if (isSuccess) {
                        Toast.makeText(context, "Shop Removed\n", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(context, "Failed\n" + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void onLoginSuccess() {
        _shopButton.setEnabled(true);
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _shopButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String tax = _taxText.getText().toString();
        String contact = _contactText.getText().toString();
        //String address = _addressText.getText().toString();

        if (name.isEmpty()) {
            _nameText.setError("enter a valid name");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (tax.isEmpty()) {
            _taxText.setError("enter a valid tax number");
            valid = false;
        } else {
            _taxText.setError(null);
        }

        if (contact.isEmpty()) {
            _contactText.setError("enter a valid contact");
            valid = false;
        } else {
            _contactText.setError(null);
        }

        if (lat == 0 || lng == 0 || address.isEmpty()) {
            _placeText.setError("enter shop location");
            valid = false;
        } else {
            _placeText.setError(null);
        }

        selectedCat = "";
        try {
            for (int i = 0; i < items.size(); i++) {
                if (checkSelected[i] == true) {
                    JSONObject catOb = (JSONObject) catArr.get(i);
                    selectedCat += catOb.getString("catShortName");
                    selectedCat += ";";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (selectedCat.isEmpty() || selectedCat.equals("")) {
            valid = false;
            Toast.makeText(context, "Please select a Category first.", Toast.LENGTH_SHORT).show();
        } else {
            selectedCat = selectedCat.substring(0, selectedCat.length() - 1);
        }

        return valid;
    }


}

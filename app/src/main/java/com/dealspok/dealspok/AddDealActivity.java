package com.dealspok.dealspok;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dealspok.dealspok.Utils.RealPathUtil;
import com.dealspok.dealspok.entities.CategoryObject;
import com.dealspok.dealspok.entities.Shop;
import com.dealspok.dealspok.fragment.Deals;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Umi on 11.12.2017.
 */

public class AddDealActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private List<Shop> shopList;
    private List<CategoryObject> catList;
    private JSONArray shopArr = null;
    private JSONArray catArr = null;
    private SpinAdapter adapter;
    private SpinAdapterCat adapterCat;
    private String userId = "";
    private EditText expiry;
    private Calendar calendarDate;
    private Context context;
    private Activity activity;

    private static final String TAG = MainActivity.class.getSimpleName();
    private String selectedFilePath;
    private List<String> selectedFilePathList;
    private String resultData = "";
    private int shopId = -1;
    private String dealTypeValue = "TYPE_DEALS";
    private String catShortName = "essen";

    private SliderLayout mDemoSlider;
    private Button bUpload;
    private ProgressDialog dialog;
    private Spinner spinnerShop;
    private Spinner spinnerDeals;
    private Spinner spinnerCat;
    private EditText inputUrl;
    private EditText inputTitle;
    private EditText inputDesc;
    private EditText inputOPrice;
    private EditText inputDPrice;
    private ImageView attachImg;

    private static final int PICK_FILE_REQUEST = 1;
    private final String URL_Shops = "/mobile/api/shops/list";
    private final String URL_Cat = "/mobile/api/categories/list";
    private String SERVER_URL = "https://www.regionaldeals.de/mobile/api/deals/upload-deal";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int REQUEST_CODE_CHOOSE = 1234;
    private static final String[] dealTypes = {"Deals", "Online Deals"};
    private Boolean isGutscheine = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_deals_activity);
        context = this;
        activity = this;

        if(getIntent().hasExtra("isGutscheine")) {
            isGutscheine = getIntent().getBooleanExtra("isGutscheine", false);
        }
        if(getIntent().hasExtra("userId")) {
            userId = getIntent().getStringExtra("userId");
        }

        //CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        expiry = (EditText) findViewById(R.id.input_expiry);
        calendarDate = Calendar.getInstance();
        selectedFilePathList = new ArrayList<>();
        bUpload = (Button) findViewById(R.id.b_upload);
        mDemoSlider = (SliderLayout) findViewById(R.id.image);
        spinnerShop = (Spinner) findViewById(R.id.spinner_shops);
        spinnerDeals = (Spinner) findViewById(R.id.spinner_dealType);
        spinnerCat = (Spinner) findViewById(R.id.spinner_cat);
        inputTitle = (EditText) findViewById(R.id.input_title);
        inputDesc = (EditText) findViewById(R.id.input_desc);
        inputOPrice = (EditText) findViewById(R.id.input_oprice);
        inputDPrice = (EditText) findViewById(R.id.input_dprice);
        inputUrl = (EditText) findViewById(R.id.input_url);
        attachImg = (ImageView) findViewById(R.id.ivAttachment);
        LinearLayout dealType = (LinearLayout)findViewById(R.id.deal_type_layout);

        if(isGutscheine){
            inputOPrice.setVisibility(View.GONE);
            dealType.setVisibility(View.GONE);
            SERVER_URL = "https://www.regionaldeals.de/mobile/api/gutschein/upload-gutschein";
        }

        attachImg.setOnClickListener(this);
        bUpload.setOnClickListener(this);

        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dp = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendarDate.set(year, month, day);
                        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
                        calendarDate.set(Calendar.MINUTE, 0);
                        expiry.setText(day + "/" + month + "/" + year);
                    }
                }, calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH));
                dp.setTitle("Select Expiry Date");
                dp.getDatePicker().setMinDate(System.currentTimeMillis() + 86400000);//add 1 day
                dp.show();
            }
        });

        //Adapter Deals
        ArrayAdapter<String>adapterDeals = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dealTypes);
        adapterDeals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeals.setAdapter(adapterDeals);
        spinnerDeals.setOnItemSelectedListener(this);

        //Adapter Shops
        shopList = new ArrayList<>();
        adapter = new SpinAdapter(this,
                R.layout.custom_spinner_item,
                shopList);
        spinnerShop.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShop.setOnItemSelectedListener(this);
        getShopsFromServer();

        //Adapter Category
        catList = new ArrayList<>();
        adapterCat = new SpinAdapterCat(this,
                R.layout.custom_spinner_item,
                catList);
        spinnerCat.setAdapter(adapterCat);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setOnItemSelectedListener(this);
        getCatFromServer();
    }

    private void getShopsFromServer() {
        AsyncHttpClient androidClient = new AsyncHttpClient();
        RequestParams params = new RequestParams("userid", userId);
        androidClient.get(this.getString(R.string.apiUrl) + URL_Shops, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", getString(R.string.token_failed) + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
                Log.d("TAG", "Client token: " + responseToken);
                try {
                    shopList.clear();
                    JSONObject jO = new JSONObject(responseToken);
                    shopArr = (JSONArray) jO.getJSONArray("data");
                    if (shopArr != null) {
                        for (int i = 0; i < shopArr.length(); i++) {
                            JSONObject c = shopArr.getJSONObject(i);
                            Gson gson = new GsonBuilder().create();
                            Shop newDeal = gson.fromJson(c.toString(), Shop.class);
                            shopList.add(newDeal);
                        }
                        adapter.notifyDataSetChanged();

                        if (shopList.size() <= 0) {
                            Toast.makeText(context, "Kindly add a shop first", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.d("Deals: ", "null");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        });
    }

    private void getCatFromServer() {
        AsyncHttpClient androidClient = new AsyncHttpClient();
        RequestParams params = new RequestParams("userid", userId);
        androidClient.get(this.getString(R.string.apiUrl) + URL_Cat, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", getString(R.string.token_failed) + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseToken) {
                Log.d("TAG", "Client token: " + responseToken);
                try {
                    catList.clear();
                    catArr = new JSONArray(responseToken);
                    if (catArr != null) {
                        for (int i = 0; i < catArr.length(); i++) {
                            JSONObject c = catArr.getJSONObject(i);
                            Gson gson = new GsonBuilder().create();
                            CategoryObject newDeal = gson.fromJson(c.toString(), CategoryObject.class);
                            catList.add(newDeal);
                        }
                        adapterCat.notifyDataSetChanged();

                        if (catList.size() <= 0) {
                            Toast.makeText(context, "No categories found", Toast.LENGTH_SHORT).show();
                            //finish();
                        }
                    } else {
                        Log.d("Deals: ", "null");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_shops:
                Shop tmp = (Shop) parent.getItemAtPosition(position);
                shopId = tmp.getShopId();
                break;
            case R.id.spinner_dealType:
                switch (position) {
                    case 0:
                        dealTypeValue = "TYPE_DEALS";
                        inputUrl.setVisibility(View.GONE);
                        break;
                    case 1:
                        dealTypeValue = "TYPE_ONLINE_DEALS";
                        inputUrl.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case R.id.spinner_cat:
                CategoryObject tmp2 = (CategoryObject) parent.getItemAtPosition(position);
                catShortName = tmp2.getCatShortName();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onClick(View v) {
        if (v == attachImg) {
            showFileChooser();
        }
        if (v == bUpload) {
            if (selectedFilePath != null) {
                dialog = ProgressDialog.show(AddDealActivity.this, "", "Uploading File...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile();
                    }
                }).start();
            } else {
                Snackbar.make(v, "Please choose a picture first", Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    //Get User permissions
    @SuppressLint("NewApi")
    private void showFileChooser() {
        if (Build.VERSION.SDK_INT >= 19) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                proceedFileChooser();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    proceedFileChooser();
                } else {
                    Toast.makeText(context, "Cannot add pictures without user permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void proceedFileChooser() {
//        Intent chooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        chooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(Intent.createChooser(chooseIntent,"Choose File to Upload.."), PICK_FILE_REQUEST);
        Matisse.from(AddDealActivity.this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))
                .countable(true)
                .maxSelectable(5)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                if (data != null) {
                    List<Uri> mSelected = Matisse.obtainResult(data);
                    Log.d("Matisse", "mSelected: " + mSelected);
                    //Remove pervious pictures
                    mDemoSlider.removeAllSliders();
                    selectedFilePathList.clear();

                    HashMap<String, String> url_maps = new HashMap<String, String>();
                    for (int a = 0; a < mSelected.size(); a++) {
                        selectedFilePath = RealPathUtil.getRealPath(this, mSelected.get(a));
                        if (selectedFilePath != null && !selectedFilePath.equals("")) {
                            selectedFilePathList.add(selectedFilePath);
                            url_maps.put(Integer.toString(a), mSelected.get(a).toString());
                        }
                    }
                    for (String name : url_maps.keySet()) {
                        TextSliderView textSliderView = new TextSliderView(this);
                        textSliderView
                                .description(name)
                                .image(url_maps.get(name))
                                .setScaleType(BaseSliderView.ScaleType.Fit);
                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", name);
                        mDemoSlider.addSlider(textSliderView);
                    }
                    mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                    mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                    mDemoSlider.setDuration(6000);
                }
            }
        }
    }

    private static byte[] readBytesFromFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

    //android upload file to server
    public void uploadFile() {
        int serverResponseCode = 0;
        List<File> selectedFile = new ArrayList<>();
        List<String> fileName = new ArrayList<>();
        for (int i = 0; i < selectedFilePathList.size(); i++) {
            selectedFile.add(new File(selectedFilePathList.get(i)));
            String[] parts = selectedFilePathList.get(i).split("/");
            fileName.add(parts[parts.length - 1]);
            if (!selectedFile.get(i).isFile()) {
                dialog.dismiss();
                final int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Source File Doesn't Exist: " + selectedFilePathList.get(finalI), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }

        try {
            com.dealspok.dealspok.Utils.HttpClient client = new com.dealspok.dealspok.Utils.HttpClient(SERVER_URL);
            client.connectForMultipart();
            int userIdInt = Integer.valueOf(userId);
            client.addFormPartInt("userid", userIdInt);
            client.addFormPartInt("shopid", shopId);
            client.addFormPart("cat", catShortName);
            client.addFormPartLong("createdate", System.currentTimeMillis());
            client.addFormPartLong("publishdate", System.currentTimeMillis());
            client.addFormPartLong("expirydate", calendarDate.getTimeInMillis());
            //60 is gor DE
            client.addFormPartInt("tz", 60);
            client.addFormPart("currency", "EUR");

            if(isGutscheine){
                client.addFormPart("gutscheintitle", inputTitle.getText().toString());
                client.addFormPart("gutscheindesc", inputDesc.getText().toString());
                client.addFormPartDouble("gutscheinprice", Double.valueOf(inputDPrice.getText().toString()));
            } else {
                client.addFormPart("dealtitle", inputTitle.getText().toString());
                client.addFormPart("dealdesc", inputDesc.getText().toString());
                client.addFormPartDouble("origprice", Double.valueOf(inputOPrice.getText().toString()));
                client.addFormPartDouble("dealprice", Double.valueOf(inputDPrice.getText().toString()));
                client.addFormPart("dealtype", dealTypeValue);
                client.addFormPart("dealurl", inputUrl.getText().toString());
            }

            for (int j = 0; j < selectedFilePathList.size(); j++) {
                byte[] bData = readBytesFromFile(selectedFilePathList.get(j));
                int count = j + 1;
                if(isGutscheine) {
                    client.addFilePart("gutscheinimg_" + Integer.toString(count), fileName.get(j), bData);
                } else {
                    client.addFilePart("dealimg_" + Integer.toString(count), fileName.get(j), bData);
                }
            }

            client.finishMultipart();
            resultData = client.getResponse();
            resultData.toString();
            JSONObject jRes = new JSONObject(resultData);
            final String res = jRes.getString("message");
            if(res.equals("DEALS_UPLOAD_OK") || res.equals("GUTSCHEIN_UPLOAD_OK")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Success "+ res, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Failed " + res, Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
             Toast.makeText(AddDealActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(AddDealActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(AddDealActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        dialog.dismiss();
    }

    //Adapter for getting shops data
    public class SpinAdapter extends ArrayAdapter<Shop> {
        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<Shop> values;

        public SpinAdapter(Context context, int textViewResourceId,
                           List<Shop> values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Shop getItem(int position) {
            return values.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(values.get(position).getShopName());
            label.setGravity(Gravity.LEFT);
            label.setTextSize(18);
            label.setPadding(20, 10, 0, 10);
            return label;
        }

        // And here is when the "chooser" is popped up
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(values.get(position).getShopName());
            label.setGravity(Gravity.LEFT);
            label.setTextSize(18);
            label.setPadding(20, 15, 0, 15);
            return label;
        }
    }

    //Adapter for getting Category data
    public class SpinAdapterCat extends ArrayAdapter<CategoryObject> {
        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<CategoryObject> values;

        public SpinAdapterCat(Context context, int textViewResourceId,
                           List<CategoryObject> values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public CategoryObject getItem(int position) {
            return values.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(values.get(position).getCatName());
            label.setGravity(Gravity.LEFT);
            label.setTextSize(18);
            label.setPadding(20, 10, 0, 10);
            return label;
        }

        // And here is when the "chooser" is popped up
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(values.get(position).getCatName());
            label.setGravity(Gravity.LEFT);
            label.setTextSize(18);
            label.setPadding(20, 15, 0, 15);
            return label;
        }
    }

}

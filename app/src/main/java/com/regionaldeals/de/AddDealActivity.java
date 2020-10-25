package com.regionaldeals.de;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.regionaldeals.de.Utils.HttpClient;
import com.regionaldeals.de.Utils.RealPathUtil;
import com.regionaldeals.de.entities.CategoryObject;
import com.regionaldeals.de.entities.Shop;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Umi on 11.12.2017.
 */

public class AddDealActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private List<Shop> shopList;
    //private List<CategoryObject> catList;
    private JSONArray shopArr = null;
    private JSONArray catArr = null;
    private SpinAdapter adapter;
    //private SpinAdapterCat adapterCat;
    private String userId = "";
    private EditText expiry;
    private Calendar calendarDate;
    private Context context;
    private Activity activity;

    //private static final String TAG = MainActivity.class.getSimpleName();
    private String selectedFilePath;
    private List<String> selectedFilePathList;
    private String resultData = "";
    private int shopId = -1;
    private String dealTypeValue = "TYPE_DEALS";
    //private String catShortName = "essen";

    private SliderLayout mDemoSlider;
    private Button bUpload;
    private ProgressDialog dialog;
    private Spinner spinnerShop;
    private Spinner spinnerDeals;
    //private Spinner spinnerCat;
    private EditText inputUrl;
    private EditText inputTitle;
    private EditText inputDesc;
    private EditText inputOPrice;
    private EditText inputDPrice;
    private ImageView attachImg;

    private static final int PICK_FILE_REQUEST = 1;
    private final String URL_Shops = "/web/shops/list";
    private final String URL_Cat = "/web/categories/list";
    private String SERVER_URL = "/web/deals/upload-deal";
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

        //CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        expiry = (EditText) findViewById(R.id.input_expiry);
        calendarDate = Calendar.getInstance();
        selectedFilePathList = new ArrayList<>();
        bUpload = (Button) findViewById(R.id.b_upload);
        mDemoSlider = (SliderLayout) findViewById(R.id.image);
        spinnerShop = (Spinner) findViewById(R.id.spinner_shops);
        spinnerDeals = (Spinner) findViewById(R.id.spinner_dealType);
        //spinnerCat = (Spinner) findViewById(R.id.spinner_cat);
        inputTitle = (EditText) findViewById(R.id.input_title);
        inputDesc = (EditText) findViewById(R.id.input_desc);
        inputOPrice = (EditText) findViewById(R.id.input_oprice);
        inputDPrice = (EditText) findViewById(R.id.input_dprice);
        inputUrl = (EditText) findViewById(R.id.input_url);
        attachImg = (ImageView) findViewById(R.id.ivAttachment);
        TextView label1 = (TextView) findViewById(R.id.link_signup1);
        TextView label2 = (TextView) findViewById(R.id.link_signup2);
        TextInputLayout tVPrice = (TextInputLayout) findViewById(R.id.tVPrice);


        LinearLayout dealType = (LinearLayout) findViewById(R.id.deal_type_layout);

        if (getIntent().hasExtra("isGutscheine")) {
            isGutscheine = getIntent().getBooleanExtra("isGutscheine", false);
        }
        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getStringExtra("userId");
        }

        if (isGutscheine) {
            inputOPrice.setVisibility(View.GONE);
            dealType.setVisibility(View.GONE);
            SERVER_URL = "/web/gutschein/upload-gutschein";
        }

        //Adapter Deals
        ArrayAdapter<String> adapterDeals = new ArrayAdapter<String>(this,
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


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getShopsFromServer();
            }
        }, 300);


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

        if(isGutscheine){
            label1.setVisibility(View.GONE);
            label2.setVisibility(View.GONE);
            tVPrice.setHint("Wert des Gutscheines");

        }


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
                            Toast.makeText(context, getResources().getString(R.string.no_shop), Toast.LENGTH_SHORT).show();
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
                dialog = ProgressDialog.show(context, "", "Uploading File...", true);
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
    private void showFileChooser() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            proceedFileChooser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                proceedFileChooser();
            } else {
                Toast.makeText(context, "Cannot add pictures without user permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void proceedFileChooser() {
        Matisse.from(AddDealActivity.this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))
                .countable(true)
                .maxSelectable(5)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private List<Uri> mSelected = Collections.emptyList();
    private HashMap<String, String> url_maps = new HashMap<String, String>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                selectedFilePath = RealPathUtil.getRealPath(this, resultUri);
                url_maps.put(Integer.toString(url_maps.size()), resultUri.toString());
                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    selectedFilePathList.add(selectedFilePath);
                }
                // start picker to get image for cropping and then use the image in cropping activity
                if (mSelected.size() > 0) {
                    CropImage.activity(mSelected.get(0))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(5, 3)
                            .setFixAspectRatio(true)
                            .start(this);
                    mSelected.remove(0);
                } else {
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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                if (data != null) {
                    mSelected = Matisse.obtainResult(data);
                    Log.d("Matisse", "mSelected: " + mSelected);
                    //Remove pervious pictures
                    mDemoSlider.removeAllSliders();
                    selectedFilePathList.clear();
                    url_maps.clear();

                    // start picker to get image for cropping and then use the image in cropping activity
                    if (mSelected.size() > 0) {
                        CropImage.activity(mSelected.get(0))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(5, 3)
                                .setFixAspectRatio(true)
                                .start(this);
                        mSelected.remove(0);
                    }

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
            HttpClient client = new HttpClient(context.getString(R.string.apiUrl) + SERVER_URL);
            client.connectForMultipart();
            int userIdInt = Integer.valueOf(userId);
            client.addFormPartInt("userid", userIdInt);
            client.addFormPartInt("shopid", shopId);
            //client.addFormPart("cat", catShortName);
            client.addFormPartLong("createdate", System.currentTimeMillis());
            client.addFormPartLong("publishdate", System.currentTimeMillis());
            client.addFormPartLong("expirydate", calendarDate.getTimeInMillis());
            //60 is gor DE
            client.addFormPartInt("tz", 60);
            client.addFormPart("currency", "EUR");
            client.addFormPart("cat", null);


            if (isGutscheine) {
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
                if (isGutscheine) {
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
            if (res.equals("DEALS_UPLOAD_OK") || res.equals("GUTSCHEIN_UPLOAD_OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Success " + res, Toast.LENGTH_SHORT).show();
                        Intent intent = activity.getIntent();
                        intent.putExtra("dealAddSuccess", true);
                        activity.setResult(Activity.RESULT_OK, intent);
                        activity.finish();
                    }
                });
            } else if (res.equals("ERR_MAX_DEALS")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Sorry. MAX Deals Reached! \n" + res, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Failed " + res, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "File Not Found", Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(context, "URL error!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
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

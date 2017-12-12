package com.dealspok.dealspok;

import android.*;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dealspok.dealspok.Utils.RealPathUtil;
import com.dealspok.dealspok.entities.Shop;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
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

    private Spinner spinner;
    private boolean isSpinnerInitial = true;
    private final String URL_Shops = "/mobile/api/shops/list";
    private List<Shop> shopList;
    private JSONArray shopArr = null;
    private SpinAdapter adapter;
    private String userId = "";
    private EditText expiry;
    private Calendar calendarDate;
    private Context context;

    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String selectedFilePath;
    private List<String> selectedFilePathList;
    private String SERVER_URL = "https://www.regionaldeals.de/mobile/api/deals/upload-deal";
    ImageView ivAttachment;
    Button bUpload;
    TextView tvFileName;
    ProgressDialog dialog;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int REQUEST_CODE_CHOOSE = 1234;
    private SliderLayout mDemoSlider;
    private String resultData = "";
    private int shopId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_deals_activity);
        context = this;
        expiry = (EditText)findViewById(R.id.input_expiry);
        calendarDate = Calendar.getInstance();
        selectedFilePathList = new ArrayList<>();
        ivAttachment = (ImageView) findViewById(R.id.ivAttachment);
        bUpload = (Button) findViewById(R.id.b_upload);
        tvFileName = (TextView) findViewById(R.id.tv_file_name);
        ivAttachment.setOnClickListener(this);
        bUpload.setOnClickListener(this);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mDemoSlider = (SliderLayout)findViewById(R.id.image);

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
                }, calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH),calendarDate.get(Calendar.DAY_OF_MONTH));
            dp.setTitle("Select Expiry Date");
            dp.getDatePicker().setMinDate(System.currentTimeMillis() + 86400000);//add 1 day
            dp.show();
            }
        });

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.sharedPredName), MODE_PRIVATE);
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
        shopList = new ArrayList<>();
        spinner = (Spinner)findViewById(R.id.spinner_shops);
        spinner.setVisibility(View.VISIBLE);
        adapter = new SpinAdapter(this,
                R.layout.custom_spinner_item,
                shopList);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        getShopsFromServer();

    }

    private void getShopsFromServer(){
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
                        isSpinnerInitial = true;
                        adapter.notifyDataSetChanged();

                        if(shopList.size()<=0){
                            Toast.makeText(context, "Kindly add shop first", Toast.LENGTH_SHORT).show();
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
        Shop tmp=(Shop) parent.getItemAtPosition(position);
        shopId = tmp.getShopId();
//        if(isSpinnerInitial)
//        {
//            isSpinnerInitial = false;
//        }
//        else  {
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class SpinAdapter extends ArrayAdapter<Shop>{

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
        public int getCount(){
            return values.size();
        }

        @Override
        public Shop getItem(int position){
            return values.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(values.get(position).getShopName());
            label.setGravity(Gravity.LEFT);
            label.setTextSize(18);
            label.setPadding(0,10,0,10);
            label.setAllCaps(true);

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(getResources().getColor(R.color.colorGrey));
            label.setText(values.get(position).getShopName());
            label.setGravity(Gravity.LEFT);
            label.setTextSize(18);
            label.setPadding(0,10,0,10);
            label.setAllCaps(true);
            return label;
        }
    }

    @Override
    public void onClick(View v) {
        if(v == ivAttachment){

            //on attachment icon click
            showFileChooser();
        }
        if(v == bUpload){

            //on upload button Click
            if(selectedFilePath != null){
                dialog = ProgressDialog.show(AddDealActivity.this,"","Uploading File...",true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //creating new thread to handle Http Operations
                        uploadFile(selectedFilePath);
                    }
                }).start();
            }else{
                Toast.makeText(AddDealActivity.this,"Please choose a File First",Toast.LENGTH_SHORT).show();
            }

        }
    }
    @SuppressLint("NewApi")
    private void showFileChooser() {
        if (Build.VERSION.SDK_INT >= 19) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }else {
                proceedFileChooser();
            }
        }
    }

    private void proceedFileChooser(){
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedFileChooser();
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }
                Uri selectedFileUri = data.getData();
                selectedFilePath = RealPathUtil.getRealPath(this, selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    tvFileName.setText(selectedFilePath);
                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CODE_CHOOSE) {
                if (data != null) {
                    List<Uri> mSelected = Matisse.obtainResult(data);
                    Log.d("Matisse", "mSelected: " + mSelected);
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

                    //mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
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

            //read file into bytes[]
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
    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        List<File> selectedFile = new ArrayList<>();
        List<String> fileName =  new ArrayList<>();
        for(int i=0; i< selectedFilePathList.size(); i++){
            selectedFile.add(new File(selectedFilePathList.get(i)));
            String[] parts = selectedFilePathList.get(i).split("/");
            fileName.add(parts[parts.length-1]);
            if (!selectedFile.get(i).isFile()){
                dialog.dismiss();
                final int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvFileName.setText("Source File Doesn't Exist: " + selectedFilePathList.get(finalI));
                    }
                });
                return 0;
            }
        }

            try{

                com.dealspok.dealspok.Utils.HttpClient client = new com.dealspok.dealspok.Utils.HttpClient(SERVER_URL);
                client.connectForMultipart();
                client.addFormPartInt("userid",17);
                client.addFormPartInt("shopid", shopId);
                client.addFormPart("cat", "auto");
                client.addFormPart("dealtitle", "Title1");
                client.addFormPart("dealdesc", "Desc1");
                client.addFormPartLong("createdate", System.currentTimeMillis());       //must
                client.addFormPartLong("publishdate", System.currentTimeMillis());
                client.addFormPartLong("expirydate", calendarDate.getTimeInMillis());       //must
                //client.addFormPart("tz", 0);
                client.addFormPartDouble("origprice", 20);
                client.addFormPartDouble("dealprice", 10);
                client.addFormPart("currency", "EUR");
                client.addFormPart("dealtype", "TYPE_DEALS");
                //client.addFormPart("dealurl", "TYPE_DEALS");


                for(int j=0; j<selectedFilePathList.size(); j++) {
                    byte[] bData = readBytesFromFile(selectedFilePathList.get(j));
                    client.addFilePart("dealimg_1", fileName.get(j), bData);
                }

                client.finishMultipart();
                resultData = client.getResponse();
                resultData.toString();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddDealActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(AddDealActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(AddDealActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            } catch(Throwable t) {
                t.printStackTrace();
            }
            dialog.dismiss();
            return serverResponseCode;

    }

}

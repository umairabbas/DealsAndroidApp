package com.regionaldeals.de;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.entities.CitiesObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.regionaldeals.de.Constants.CITIES_KEY;
import static com.regionaldeals.de.Constants.CITIES_OBJECT_KEY;
import static com.regionaldeals.de.Constants.LOCATION_KEY;

/**
 * Created by Umi on 09.09.2017.
 */

public class LocationManual extends AppCompatActivity implements ListView.OnItemClickListener {
    private GestureDetector mGestureDetector;

    // x and y coordinates within our side index
    private static float sideIndexX;
    private static float sideIndexY;

    // height of side index
    private int sideIndexHeight;

    // number of items in the side index
    private int indexListSize;

    // list with items for side index
    private ArrayList<Object[]> indexList = null;

    private ListView lv1;

    private List<CitiesObject> city = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_manual);

        String restoredCitiesObject = SharedPreferenceUtils.getInstance(this).getStringValue(CITIES_OBJECT_KEY, "");
        String restoredCities = SharedPreferenceUtils.getInstance(this).getStringValue(CITIES_KEY, null);

        lv1 = (ListView) findViewById(R.id.ListView01);
        lv1.setOnItemClickListener(this);

        if (restoredCities == null) {
            //should not be
            Toast.makeText(this, "Please restart or update app", Toast.LENGTH_LONG).show();
            finish();
        } else {
            COUNTRIES = restoredCities.split(",");
            Arrays.sort(COUNTRIES);
            lv1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, COUNTRIES));
            mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());
        }

        if (restoredCitiesObject != null) {
            Gson gson = new Gson();
            Type founderListType = new TypeToken<ArrayList<CitiesObject>>() {
            }.getType();
            city = gson.fromJson(restoredCitiesObject, founderListType);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String citySelected = COUNTRIES[i];

        String lat = null;
        String lng = null;

        for (CitiesObject c : city) {
            if (c.getCityName().equals(citySelected)) {
                lat = Double.toString(c.getCityLat());
                lng = Double.toString(c.getCityLong());
            }
        }

        String placeJson = "{\"Name\":\"" + citySelected + "\", \"lat\": \"" + lat + "\", \"lng\":\"" + lng + "\"}";
        SharedPreferenceUtils.getInstance(this).setValue(LOCATION_KEY, placeJson);

        Intent startActivityIntent = new Intent(LocationManual.this, MainActivity.class);
        startActivityIntent.putExtra("userCity", citySelected);
        startActivityIntent.putExtra("updateCity", true);
        startActivity(startActivityIntent);
        finish();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<Object[]> createIndex(String[] strArr) {
        ArrayList<Object[]> tmpIndexList = new ArrayList<Object[]>();
        Object[] tmpIndexItem = null;

        int tmpPos = 0;
        String tmpLetter = "";
        String currentLetter = null;
        String strItem = null;

        for (int j = 0; j < strArr.length; j++) {
            strItem = strArr[j];
            currentLetter = strItem.substring(0, 1);

            // every time new letters comes
            // save it to index list
            if (!currentLetter.equals(tmpLetter)) {
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = tmpLetter;
                tmpIndexItem[1] = tmpPos - 1;
                tmpIndexItem[2] = j - 1;

                tmpLetter = currentLetter;
                tmpPos = j + 1;

                tmpIndexList.add(tmpIndexItem);
            }
        }

        // save also last letter
        tmpIndexItem = new Object[3];
        tmpIndexItem[0] = tmpLetter;
        tmpIndexItem[1] = tmpPos - 1;
        tmpIndexItem[2] = strArr.length - 1;
        tmpIndexList.add(tmpIndexItem);

        // and remove first temporary empty entry
        if (tmpIndexList != null && tmpIndexList.size() > 0) {
            tmpIndexList.remove(0);
        }

        return tmpIndexList;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //public void onCitiesResult(){

        final ListView listView = (ListView) findViewById(R.id.ListView01);
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        sideIndex.removeAllViews();

        // TextView for every visible item
        TextView tmpTV = null;

        // we'll create the index list
        indexList = createIndex(COUNTRIES);

        // number of items in the index List
        indexListSize = indexList.size();

        // maximal number of item, which could be displayed
        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);

        int tmpIndexListSize = indexListSize;

        // handling that case when indexListSize > indexMaxSize
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }

        // computing delta (only a part of items will be displayed to save a
        // place)
        double delta = indexListSize / tmpIndexListSize;

        String tmpLetter = null;
        Object[] tmpIndexItem = null;

        // show every m-th letter
        for (double i = 1; i <= indexListSize; i = i + delta) {
            tmpIndexItem = indexList.get((int) i - 1);
            tmpLetter = tmpIndexItem[0].toString();
            tmpTV = new TextView(this);
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(20);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        // and set a touch listener for it
        sideIndex.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();

                return false;
            }
        });
    }

    class SideIndexGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // we know already coordinates of first touch
            // we know as well a scroll distance
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            // when the user scrolls within our side index
            // we can show for every position in it a proper
            // item in the country list
            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void displayListItem() {
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // compute minimal position for the item in the list
        int minPosition = (int) (itemPosition * pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        Object[] indexItem = indexList.get(itemPosition);

        // and compute the proper item in the country list
        int indexMin = Integer.parseInt(indexItem[1].toString());
        int indexMax = Integer.parseInt(indexItem[2].toString());
        int indexDelta = Math.max(1, indexMax - indexMin);

        double pixelPerSubitem = pixelPerIndexItem / indexDelta;
        int subitemPosition = (int) (indexMin + (sideIndexY - minPosition) / pixelPerSubitem);

        ListView listView = (ListView) findViewById(R.id.ListView01);
        listView.setSelection(subitemPosition);
    }

    // an array with countries to display in the list
    private static String[] COUNTRIES;

}
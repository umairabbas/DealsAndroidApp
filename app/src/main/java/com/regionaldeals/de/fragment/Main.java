package com.regionaldeals.de.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.regionaldeals.de.MainActivity;
import com.regionaldeals.de.R;
import com.regionaldeals.de.adapter.CustomFragmentPageAdapter;
import com.regionaldeals.de.service.LocationStatic;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Umi on 28.08.2017.
 */

public class Main extends Fragment {

    private static final String TAG = Main.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    CustomFragmentPageAdapter cfpa;
    private SeekBar seekControl = null;
    Intent intent;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    private Context context;

    public Main() {
    }

    public static double getLat(){
        return latitude;
    }

    public static double getLng(){
        return longitude;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        context = getActivity();
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        cfpa = new CustomFragmentPageAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(cfpa);
        tabLayout.setupWithViewPager(viewPager);
        new LocationStatic(getContext());

        intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction("BroadcastReceiver");
        intent.putExtra("Foo", "Bar");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        latitude  = LocationStatic.latitude; // latitude
        longitude = LocationStatic.longitude; // latitude

        if(latitude == 0 || longitude == 0){
            SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getString(R.string.sharedPredName), MODE_PRIVATE);
            String restoredText = prefs.getString("locationObject", null);
            if (restoredText != null) {
                try {
                    JSONObject obj = new JSONObject(restoredText);
                        if(!obj.isNull("lat") && !obj.isNull("lng") ){
                            latitude = obj.getDouble("lat");
                            longitude = obj.getDouble("lng");
                        }
                }catch(Exception e){
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekControl = (SeekBar) view.findViewById(R.id.fontSeekBar);
        final TextView seekTitle2 = (TextView) view.findViewById(R.id.seekBarTitle);
        seekControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.print("sd");
                seekTitle2.setText(progressChanged + " KM");
                intent.putExtra("distance", progressChanged);
                getActivity().sendBroadcast(intent);
            }
        });
    }
}
package com.dealspok.dealspok.fragment;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.adapter.DealsAdapter;
import com.dealspok.dealspok.entities.DealsObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DATE;

/**
 * Created by Umi on 28.08.2017.
 */

public class Gutscheine extends Fragment {

    public Gutscheine() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gutscheine, container, false);

        getActivity().setTitle("DealSpok");
        RecyclerView songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        DealsAdapter mAdapter = new DealsAdapter(getActivity(), getTestData());
        songRecyclerView.setAdapter(mAdapter);
        return view;
    }

    Location createNewLocation(double latitude, double longitude) {
        Location location = new Location("dummyprovider");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }

    public List<DealsObject> getTestData() {
        Location dealLoc = createNewLocation(50.7947313, 6.1019621);
        Date created = new Date();
        List<DealsObject> deals = new ArrayList<>();
        deals.add(new DealsObject(1, Uri.parse("http://2.bp.blogspot.com/-Mbq5kmMtrgI/T44zS6BzQ6I/AAAAAAAAA20/QjajhABxexU/s1600/458827_10151501202440023_142518590022_23600640_1232321177_o.jpg"),
                "20% Rabatt Mcdonalds", "Promotion period: 18 April – 18 May and while stocks last. Available from 11am – 4am, EVERYDAY!", "Tel: 089 / 7 85 94 - 413", dealLoc, created, created));
        deals.add(new DealsObject(2, Uri.parse("http://worldfranchise.eu/sites/default/files/franchises/photos/rcl_backwerk-fassade_02_a.jpg"),
                "30% Discount BackWerk", "Free Coffee in Morning", "+49 241 94379920", dealLoc, created, created));
        deals.add(new DealsObject(3, Uri.parse("http://www.couponforshopping.com/wp-content/uploads/2014/12/hm-coupon.png"),
                "20% H&M", "Sale on Jeans, shirts, ladies shoes, Jackets. Valid only till 2017", "Tel: 089 / 7 85 94 - 413", dealLoc, created, created));
        deals.add(new DealsObject(1, Uri.parse("http://2.bp.blogspot.com/-Mbq5kmMtrgI/T44zS6BzQ6I/AAAAAAAAA20/QjajhABxexU/s1600/458827_10151501202440023_142518590022_23600640_1232321177_o.jpg"),
                "20% Rabatt Mcdonalds", "Promotion period: 18 April – 18 May and while stocks last. Available from 11am – 4am, EVERYDAY!", "Tel: 089 / 7 85 94 - 413", dealLoc, created, created));
        deals.add(new DealsObject(2, Uri.parse("http://worldfranchise.eu/sites/default/files/franchises/photos/rcl_backwerk-fassade_02_a.jpg"),
                "30% Discount BackWerk", "Free Coffee in Morning", "+49 241 94379920", dealLoc, created, created));
        deals.add(new DealsObject(3, Uri.parse("http://www.couponforshopping.com/wp-content/uploads/2014/12/hm-coupon.png"),
                "20% H&M", "Sale on Jeans, shirts, ladies shoes, Jackets. Valid only till 2017", "Tel: 089 / 7 85 94 - 413", dealLoc, created, created));

        return deals;
    }
}

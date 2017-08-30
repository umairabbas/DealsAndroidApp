package com.dealspok.dealspok.fragment;

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

import java.util.ArrayList;
import java.util.List;

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

    public List<DealsObject> getTestData() {
        List<DealsObject> recentSongs = new ArrayList<>();
//        recentSongs.add(new DealsObject("Adele", "Someone Like You", ""));
//        recentSongs.add(new DealsObject("Adele", "Someone Like You", ""));
//        recentSongs.add(new DealsObject("Adele", "Someone Like You", ""));
//        recentSongs.add(new DealsObject("Adele", "Someone Like You", ""));
//        recentSongs.add(new DealsObject("Adele", "Someone Like You", ""));
//        recentSongs.add(new DealsObject("Adele", "Someone Like You", ""));
        return recentSongs;
    }
}

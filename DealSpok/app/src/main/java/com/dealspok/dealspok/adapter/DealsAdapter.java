package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.DealsObject;

import java.util.List;

public class DealsAdapter extends RecyclerView.Adapter<DealsViewHolder>{

    private Context context;
    private List<DealsObject> allSongs;

    public DealsAdapter(Context context, List<DealsObject> allSongs) {
        this.context = context;
        this.allSongs = allSongs;
    }

    @Override
    public DealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dealslist_layout, parent, false);
        return new DealsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DealsViewHolder holder, int position) {
        DealsObject songs = allSongs.get(position);
        holder.songTitle.setText(songs.getSongTitle());
        holder.songAuthor.setText(songs.getSongAuthor());
    }

    @Override
    public int getItemCount() {
        return allSongs.size();
    }

}

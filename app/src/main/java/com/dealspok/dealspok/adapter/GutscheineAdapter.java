package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.GutscheineObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GutscheineAdapter extends RecyclerView.Adapter<GutscheineViewHolder>{

    private Context context;
    private List<GutscheineObject> allDeals;

    public GutscheineAdapter(Context context, List<GutscheineObject> allDeals) {
        this.context = context;
        this.allDeals = allDeals;
    }

    @Override
    public GutscheineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gutscheinelist_layout, parent, false);
        return new GutscheineViewHolder(view, allDeals);
    }

    @Override
    public void onBindViewHolder(GutscheineViewHolder holder, int position) {
        GutscheineObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getGutscheinTitle());
        holder.dealDescription.setText(deals.getGutscheinDescription());
        Picasso.with(context).load(deals.getGutscheinImageUrl(context)).into(holder.dealCoverUrl);
    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

}

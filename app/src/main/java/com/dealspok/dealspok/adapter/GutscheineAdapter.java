package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.DealsObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GutscheineAdapter extends RecyclerView.Adapter<DealsViewHolder>{

    private Context context;
    private List<DealsObject> allDeals;

    public GutscheineAdapter(Context context, List<DealsObject> allDeals) {
        this.context = context;
        this.allDeals = allDeals;
    }

    @Override
    public DealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gutscheinelist_layout, parent, false);
        return new DealsViewHolder(view, allDeals);
    }

    @Override
    public void onBindViewHolder(DealsViewHolder holder, int position) {
        DealsObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getTitle());
        holder.dealDescription.setText(deals.getDescription());
        Picasso.with(context).load(deals.getCoverUrl()).into(holder.dealCoverUrl);
    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

}

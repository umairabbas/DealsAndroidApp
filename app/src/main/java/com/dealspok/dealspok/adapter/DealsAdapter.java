package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.DealObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DealsAdapter extends RecyclerView.Adapter<DealsViewHolder>{

    private Context context;
    private List<DealObject> allDeals;

    public DealsAdapter(Context context, List<DealObject> allDeals) {
        this.context = context;
        this.allDeals = allDeals;
    }

    @Override
    public DealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dealslist_layout, parent, false);
        return new DealsViewHolder(view, allDeals);
    }

    @Override
    public void onBindViewHolder(DealsViewHolder holder, int position) {
        DealObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getDealTitle());
        holder.dealDescription.setText(deals.getDealDescription());
        holder.dealOldPrice.setText(Long.toString(deals.getOriginalPrice()) + " €");
        holder.dealPrice.setText(Long.toString(deals.getDealPrice()) + " €");
        Picasso.with(context).load(deals.getDealImageUrl(context)).placeholder(R.drawable.menucover).into(holder.dealCoverUrl);
    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

}

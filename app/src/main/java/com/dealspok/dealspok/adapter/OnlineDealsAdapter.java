package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.DealObject;
import com.dealspok.dealspok.entities.OnlineDealsObject;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Umi on 07.10.2017.
 */

public class OnlineDealsAdapter extends RecyclerView.Adapter<OnlineDealsViewHolder>{

    private Context context;
    private List<OnlineDealsObject> allDeals;

    public OnlineDealsAdapter(Context context, List<OnlineDealsObject> allDeals) {
        this.context = context;
        this.allDeals = allDeals;
    }

    @Override
    public OnlineDealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dealslist_layout, parent, false);
        return new OnlineDealsViewHolder(view, allDeals);
    }

    @Override
    public void onBindViewHolder(OnlineDealsViewHolder holder, int position) {
        OnlineDealsObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getDealTitle());
        holder.dealDescription.setText(deals.getDealDescription());
        holder.dealOldPrice.setText(Long.toString(deals.getOriginalPrice()) + " €");
        holder.dealPrice.setText(Long.toString(deals.getDealPrice()) + " €");
        Picasso.with(context).load(deals.getDealImageUrl(context)).into(holder.dealCoverUrl);
    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

}

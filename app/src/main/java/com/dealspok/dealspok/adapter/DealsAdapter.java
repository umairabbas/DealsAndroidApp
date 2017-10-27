package com.dealspok.dealspok.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.DealObject;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class DealsAdapter extends RecyclerView.Adapter<DealsViewHolder>{

    private Context context;
    private GradientDrawable gradientDrawable;
    private List<DealObject> allDeals;
    private int [] androidColors;

    public DealsAdapter(Context context, List<DealObject> allDeals) {
        this.context = context;
        this.allDeals = allDeals;
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        androidColors = context.getResources().getIntArray(R.array.androidcolors);
    }

    @Override
    public DealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dealslist_layout, parent, false);
        return new DealsViewHolder(view, allDeals);
    }

    @Override
    public void onBindViewHolder(DealsViewHolder holder, int position) {
        if(allDeals.size() < position){
            return;
        }
        DealObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getDealTitle());
        holder.dealDescription.setText(deals.getDealDescription());
        holder.dealOldPrice.setText(Long.toString(deals.getOriginalPrice()) + " €");
        holder.dealPrice.setText(Long.toString(deals.getDealPrice()) + " €");
        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gradientDrawable.setColor(androidColors[new Random().nextInt(androidColors.length)]);
        Picasso.with(context).load(deals.getDealImageUrl(context)).placeholder(gradientDrawable).into(holder.dealCoverUrl);
    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

}

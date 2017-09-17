package com.dealspok.dealspok.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dealspok.dealspok.DealsDetail;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.DealsObject;

import java.util.List;


public class DealsViewHolder extends RecyclerView.ViewHolder{

    public TextView dealTitle;
    public TextView dealDescription;
    public ImageView dealCoverUrl;

    public DealsViewHolder(View itemView, TextView dealTitle, TextView dealDescription, ImageView dealCoverUrl) {
        super(itemView);
        this.dealTitle = dealTitle;
        this.dealDescription = dealDescription;
        this.dealCoverUrl = dealCoverUrl;
    }

    public DealsViewHolder(View itemView, final List<DealsObject> allDeals) {
        super(itemView);

        dealTitle = (TextView)itemView.findViewById(R.id.deal_title);
        dealDescription = (TextView)itemView.findViewById(R.id.deal_description);
        dealCoverUrl = (ImageView)itemView.findViewById(R.id.card_image_gut);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DealsDetail.class);
                DealsObject currDeal = allDeals.get(getAdapterPosition());
                intent.putExtra("title", currDeal.getTitle());
                intent.putExtra("desc", currDeal.getDescription());
                intent.putExtra("coverImg", currDeal.getCoverUrl().toString());
                intent.putExtra("lat", currDeal.getLocation().getLatitude());
                intent.putExtra("long", currDeal.getLocation().getLongitude());
                intent.putExtra("contact", currDeal.getContact());
                intent.putExtra(DealsDetail.EXTRA_POSITION, getAdapterPosition());
                context.startActivity(intent);
            }
        });
    }
}

package com.dealspok.dealspok.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dealspok.dealspok.R;


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

    public DealsViewHolder(View itemView) {
        super(itemView);

        dealTitle = (TextView)itemView.findViewById(R.id.deal_title);
        dealDescription = (TextView)itemView.findViewById(R.id.deal_description);
        dealCoverUrl = (ImageView)itemView.findViewById(R.id.deal_cover_url);
    }
}

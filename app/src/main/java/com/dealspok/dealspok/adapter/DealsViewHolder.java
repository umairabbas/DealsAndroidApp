package com.dealspok.dealspok.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dealspok.dealspok.DealsDetail;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.DealObject;
import com.dealspok.dealspok.entities.OnlineDealsObject;
import com.dealspok.dealspok.fragment.OnlineDeals;

import java.util.List;


public class DealsViewHolder extends RecyclerView.ViewHolder{

    public TextView dealTitle;
    public TextView dealDescription;
    public TextView dealOldPrice;
    public TextView dealPrice;
    public ImageView dealCoverUrl;
    public ImageButton favoriteImageButton;

//    public DealsViewHolder(View itemView, TextView dealTitle, TextView dealDescription, ImageView dealCoverUrl) {
//        super(itemView);
//        this.dealTitle = dealTitle;
//        this.dealDescription = dealDescription;
//        this.dealCoverUrl = dealCoverUrl;
//    }

    public DealsViewHolder(View itemView, final List<DealObject> allDeals) {
        super(itemView);

        dealTitle = (TextView)itemView.findViewById(R.id.deal_title);
        dealDescription = (TextView)itemView.findViewById(R.id.deal_description);
        dealOldPrice = (TextView)itemView.findViewById(R.id.deal_old_price);
        dealOldPrice.setPaintFlags(dealOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        dealPrice = (TextView)itemView.findViewById(R.id.deal_price);
        dealCoverUrl = (ImageView)itemView.findViewById(R.id.card_image_gut);
        favoriteImageButton = (ImageButton) itemView.findViewById(R.id.favorite_button);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DealsDetail.class);
                DealObject currDeal = allDeals.get(getAdapterPosition());
                intent.putExtra("title", currDeal.getDealTitle());
                intent.putExtra("desc", currDeal.getDealDescription());
                intent.putExtra("coverImg", currDeal.getDealImageUrl(context));
                intent.putExtra("lat",  Double.parseDouble(currDeal.getShopObj().getShopLocationLat()));
                intent.putExtra("long", Double.parseDouble(currDeal.getShopObj().getShopLocationLong()));
                intent.putExtra("contact", currDeal.getShopObj().getShopContact());
                intent.putExtra("address", currDeal.getShopObj().getShopAddress());
                intent.putExtra("shopName", currDeal.getShopObj().getShopName());
                intent.putExtra("shopCountry", currDeal.getShopObj().getShopCountry());
                intent.putExtra("shopDetails", currDeal.getShopObj().getShopDetails());
                intent.putExtra(DealsDetail.EXTRA_POSITION, getAdapterPosition());
                context.startActivity(intent);
            }
        });
    }
}

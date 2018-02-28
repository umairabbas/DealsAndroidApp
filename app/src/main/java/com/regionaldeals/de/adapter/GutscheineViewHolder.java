package com.regionaldeals.de.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.regionaldeals.de.DealsDetail;
import com.regionaldeals.de.R;
import com.regionaldeals.de.entities.GutscheineObject;

import java.util.List;


public class GutscheineViewHolder extends RecyclerView.ViewHolder {

    public TextView dealTitle;
    public TextView dealDescription;
    public TextView gut_price;
    public ImageView dealCoverUrl;
    public ImageView mitMachenBtn;
    public LinearLayout mitmachenLay;
    private Activity context;

    public GutscheineViewHolder(View itemView, TextView dealTitle, TextView dealDescription, ImageView dealCoverUrl, ImageButton mitBtn) {
        super(itemView);
        this.dealTitle = dealTitle;
        this.dealDescription = dealDescription;
        this.dealCoverUrl = dealCoverUrl;
        this.mitMachenBtn = mitBtn;

    }

    public GutscheineViewHolder(View itemView, final List<GutscheineObject> allDeals, final boolean canEdit) {
        super(itemView);
        context = (Activity) itemView.getContext();
        dealTitle = (TextView) itemView.findViewById(R.id.deal_title);
        dealDescription = (TextView) itemView.findViewById(R.id.deal_description);
        gut_price = (TextView) itemView.findViewById(R.id.gut_price);
        dealCoverUrl = (ImageView) itemView.findViewById(R.id.card_image_gut);
        mitMachenBtn = (ImageView) itemView.findViewById(R.id.action_button);
        mitmachenLay = (LinearLayout) itemView.findViewById(R.id.mitmachenlay);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DealsDetail.class);
                GutscheineObject currDeal = allDeals.get(getAdapterPosition());
                intent.putExtra("currGut", currDeal);
                intent.putExtra("title", currDeal.getGutscheinTitle());
                intent.putExtra("desc", currDeal.getGutscheinDescription());
                intent.putExtra("coverImg", currDeal.getGutscheinImageUrl(context) + "&imagecount=");
                intent.putExtra("lat", Double.parseDouble(currDeal.getShop().getShopLocationLat()));
                intent.putExtra("long", Double.parseDouble(currDeal.getShop().getShopLocationLong()));
                intent.putExtra("imgCount", currDeal.getGutscheinImageCount());
                intent.putExtra("contact", currDeal.getShop().getShopContact());
                intent.putExtra("address", currDeal.getShop().getShopAddress());
                intent.putExtra("shopName", currDeal.getShop().getShopName());
                intent.putExtra("shopCountry", currDeal.getShop().getShopCountry());
                intent.putExtra("shopDetails", currDeal.getShop().getShopDetails());
                intent.putExtra("deleteEnable", canEdit);
                intent.putExtra("isGutschein", true);
                intent.putExtra(DealsDetail.EXTRA_POSITION, getAdapterPosition());
                context.startActivity(intent);
            }
        });
    }

}

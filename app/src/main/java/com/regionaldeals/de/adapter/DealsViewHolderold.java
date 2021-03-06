package com.regionaldeals.de.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.regionaldeals.de.DealsDetail;
import com.regionaldeals.de.R;
import com.regionaldeals.de.entities.DealObject;

import java.util.List;


public class DealsViewHolderold extends RecyclerView.ViewHolder {

    public TextView dealTitle;
    public TextView dealDescription;
    public TextView dealOldPrice;
    public TextView dealPrice;
    public ImageView dealCoverUrl;
    public ImageButton favoriteImageButton;

    public DealsViewHolderold(View itemView, final List<DealObject> allDeals, final boolean canEdit) {
        super(itemView);

        dealTitle = (TextView) itemView.findViewById(R.id.deal_title);
        dealDescription = (TextView) itemView.findViewById(R.id.deal_description);
        dealOldPrice = (TextView) itemView.findViewById(R.id.deal_old_price);
        dealOldPrice.setPaintFlags(dealOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        dealPrice = (TextView) itemView.findViewById(R.id.deal_price);
        dealCoverUrl = (ImageView) itemView.findViewById(R.id.card_image_gut);
        favoriteImageButton = (ImageButton) itemView.findViewById(R.id.favorite_button);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DealsDetail.class);
                DealObject currDeal = allDeals.get(getAdapterPosition());
                //Not added because activity also supports gutscheine
                intent.putExtra("currDeal", currDeal);
                intent.putExtra("title", currDeal.getDealTitle());
                intent.putExtra("desc", currDeal.getDealDescription());
                intent.putExtra("coverImg", currDeal.getDealImageUrl(context) + "&imagecount=");
                intent.putExtra("imgCount", currDeal.getDealImageCount());
                intent.putExtra("lat", Double.parseDouble(currDeal.getShop().getShopLocationLat()));
                intent.putExtra("long", Double.parseDouble(currDeal.getShop().getShopLocationLong()));
                intent.putExtra("contact", currDeal.getShop().getShopContact());
                intent.putExtra("address", currDeal.getShop().getShopAddress());
                intent.putExtra("shopName", currDeal.getShop().getShopName());
                intent.putExtra("shopCountry", currDeal.getShop().getShopCountry());
                intent.putExtra("shopDetails", currDeal.getShop().getShopDetails());
                intent.putExtra("deleteEnable", canEdit);
                intent.putExtra(DealsDetail.EXTRA_POSITION, getAdapterPosition());
                context.startActivity(intent);
            }
        });
    }
}

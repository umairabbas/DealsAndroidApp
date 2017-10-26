package com.dealspok.dealspok.adapter;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dealspok.dealspok.DealsDetail;
import com.dealspok.dealspok.GooglePlacesAutocompleteActivity;
import com.dealspok.dealspok.LoginActivity;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.SplashActivity;
import com.dealspok.dealspok.entities.GutscheineObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class GutscheineViewHolder extends RecyclerView.ViewHolder{

    public TextView dealTitle;
    public TextView dealDescription;
    public ImageView dealCoverUrl;
    public Button mitMachenBtn;

    public GutscheineViewHolder(View itemView, TextView dealTitle, TextView dealDescription, ImageView dealCoverUrl, Button mitBtn) {
        super(itemView);
        this.dealTitle = dealTitle;
        this.dealDescription = dealDescription;
        this.dealCoverUrl = dealCoverUrl;
        this.mitMachenBtn = mitBtn;

    }

    public GutscheineViewHolder(View itemView, final List<GutscheineObject> allDeals) {
        super(itemView);

        dealTitle = (TextView)itemView.findViewById(R.id.deal_title);
        dealDescription = (TextView)itemView.findViewById(R.id.deal_description);
        dealCoverUrl = (ImageView)itemView.findViewById(R.id.card_image_gut);
        mitMachenBtn = (Button) itemView.findViewById(R.id.action_button);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DealsDetail.class);
                GutscheineObject currDeal = allDeals.get(getAdapterPosition());
                intent.putExtra("title", currDeal.getGutscheinTitle());
                intent.putExtra("desc", currDeal.getGutscheinDescription());
                intent.putExtra("coverImg", currDeal.getGutscheinImageUrl(context).toString());
                intent.putExtra("lat", Double.parseDouble(currDeal.getShop().getShopLocationLat()));
                intent.putExtra("long", Double.parseDouble(currDeal.getShop().getShopLocationLong()));
                intent.putExtra("contact", currDeal.getShop().getShopContact());
                intent.putExtra("address", currDeal.getShop().getShopAddress());
                intent.putExtra("shopName", currDeal.getShop().getShopName());
                intent.putExtra("shopCountry", currDeal.getShop().getShopCountry());
                intent.putExtra("shopDetails", currDeal.getShop().getShopDetails());
                intent.putExtra(DealsDetail.EXTRA_POSITION, getAdapterPosition());
                context.startActivity(intent);
            }
        });

        mitMachenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                String restoredText = prefs.getString("userObject", null);
                if (restoredText != null) {
                    Toast.makeText(context,restoredText,Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }

            }
        });
    }
}

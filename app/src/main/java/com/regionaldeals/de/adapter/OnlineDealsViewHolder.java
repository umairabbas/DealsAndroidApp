package com.regionaldeals.de.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.regionaldeals.de.Constants;
import com.regionaldeals.de.DealsDetailActivity;
import com.regionaldeals.de.R;
import com.regionaldeals.de.entities.DealObject;

import java.util.List;

/**
 * Created by Umi on 07.10.2017.
 */

public class OnlineDealsViewHolder extends RecyclerView.ViewHolder {

    public TextView dealTitle;
    public TextView dealDescription;
    public TextView dealOldPrice;
    public TextView dealPrice;
    public ImageView dealCoverUrl;
    public ImageButton favoriteImageButton;

    public OnlineDealsViewHolder(View itemView, final List<DealObject> allDeals) {
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
                Intent intent = new Intent(context, DealsDetailActivity.class);
                DealObject currDeal = allDeals.get(getAdapterPosition());
                intent.putExtra(Constants.DEALS_OBJECT, currDeal);
                intent.putExtra("deleteEnable", false);
                intent.putExtra("isGutschein", false);
                context.startActivity(intent);
            }
        });
    }
}

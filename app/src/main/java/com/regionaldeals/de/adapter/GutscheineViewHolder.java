package com.dealspok.dealspok.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.HttpsConnection;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dealspok.dealspok.DealsDetail;
import com.dealspok.dealspok.GooglePlacesAutocompleteActivity;
import com.dealspok.dealspok.LoginActivity;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.SplashActivity;
import com.dealspok.dealspok.entities.GutscheineObject;
import com.dealspok.dealspok.fragment.Gutscheine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class GutscheineViewHolder extends RecyclerView.ViewHolder {

    public TextView dealTitle;
    public TextView dealDescription;
    public ImageView dealCoverUrl;
    public ImageButton mitMachenBtn;
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
        dealCoverUrl = (ImageView) itemView.findViewById(R.id.card_image_gut);
        mitMachenBtn = (ImageButton) itemView.findViewById(R.id.action_button);

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

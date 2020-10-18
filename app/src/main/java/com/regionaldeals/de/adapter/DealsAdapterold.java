package com.regionaldeals.de.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.regionaldeals.de.LoginActivity;
import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.ColorUtility;
import com.regionaldeals.de.Utils.JSONParser;
import com.regionaldeals.de.Utils.SharedPreferenceUtils;
import com.regionaldeals.de.entities.DealObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.regionaldeals.de.Constants.USER_OBJECT_KEY;

public class DealsAdapterold extends RecyclerView.Adapter<DealsViewHolderold> {

    private Context context;
    private Activity activity;
    private GradientDrawable gradientDrawable;
    private List<DealObject> allDeals;
    private String URLFav = "/web/deals/favourite-click";
    private Boolean favChecked = true;
    private Boolean skipFav = false;


    public DealsAdapterold(Context context, List<DealObject> allDeals) {
        this.context = context;
        activity = (Activity) context;
        this.allDeals = allDeals;
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
    }

    public DealsAdapterold(Context context, List<DealObject> allDeals, Boolean isFromFav, Boolean skipFavBtn) {
        this.context = context;
        activity = (Activity) context;
        this.allDeals = allDeals;
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        fromFav = isFromFav;
        skipFav = skipFavBtn;
    }

    @Override
    public DealsViewHolderold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dealslist_layout, parent, false);
        //skipFav is true from create deals only (used to delete deal). Should be changed later
        return new DealsViewHolderold(view, allDeals, skipFav);
    }

    @Override
    public void onBindViewHolder(DealsViewHolderold holder, int position) {
        if (allDeals.size() <= position) {
            return;
        }
        final DealObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getDealTitle());
        holder.dealDescription.setText(deals.getShop().getShopName() + ", " + deals.getShop().getShopCity().substring(0, 1).toUpperCase() + deals.getShop().getShopCity().substring(1));
        holder.dealOldPrice.setText(Double.toString(deals.getOriginalPrice()) + "€");
        holder.dealPrice.setText(Double.toString(deals.getDealPrice()) + "€");
        String imgUrl = deals.getDealImageUrl(context) + "&imagecount=1&res=470x320";
        Picasso.with(context).load(imgUrl).placeholder(ColorUtility.getColorFromPosition(position)).resize(470, 320).onlyScaleDown().into(holder.dealCoverUrl);

        if (!skipFav) {
            if (deals.getFavourite() == null) {
                holder.favoriteImageButton.setImageResource(R.drawable.not_favorite);
            } else if (deals.getFavourite() == true) {
                holder.favoriteImageButton.setImageResource(R.drawable.favorite);
            }
        } else {
            holder.favoriteImageButton.setVisibility(View.GONE);
        }
        holder.favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavView = v;
                Context context = v.getContext();
                dealId = deals.getDealId();
                dealType = deals.getDealType();
                if (deals.getFavourite() == null) {
                    favChecked = true;
                } else {
                    favChecked = false;
                }
                String restoredText = SharedPreferenceUtils.getInstance(context).getStringValue(USER_OBJECT_KEY, null);

                if (restoredText != null) {
                    try {
                        JSONObject obj = new JSONObject(restoredText);
                        userId = obj.getString("userId");
                        new DealsAdapterold.favClick().execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Throwable t) {
                    }
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

    private DataOutputStream os;
    private HttpURLConnection con;
    private String userId = "";
    private String resultData = "";
    private int dealId;
    private String message = "";
    private String displayMsg = "";
    private JSONObject Result = null;
    JSONParser jsonParser = new JSONParser();
    private Boolean isSuccess = false;
    private View FavView;
    private Boolean fromFav = false;
    private String dealType = "";

    class favClick extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            try {
                message = "";
                displayMsg = "";

                URL url = new URL(context.getString(R.string.apiUrl) + URLFav + "?userid=" + userId +
                        "&dealid=" + Integer.toString(dealId) + "&favcheck=" + Boolean.toString(favChecked));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());

                String response = conn.getResponseMessage();

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer res = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                JSONObject jObject = new JSONObject(res.toString());
                message = jObject.getString("message");

                if (message.equals(activity.getString(R.string.DEALS_FAV_CHECK))) {
                    isSuccess = true;
                    displayMsg = "Deal wurde zu Favoriten hinzugefügt";
                    //onSignupSuccess();
                } else if (message.equals(activity.getString(R.string.DEALS_FAV_UNCHECK))) {
                    isSuccess = true;
                    displayMsg = "Deal ist entfernt";
                } else if (message.equals(activity.getString(R.string.ONLINE_FAV_UNCHECK))) {
                    isSuccess = true;
                    displayMsg = "Deal ist entfernt";
                } else if (message.equals(activity.getString(R.string.DEALS_FAV_ERR))) {
                    isSuccess = false;
                    displayMsg = "Error. Cannot do right now.. Try later";
                } else {
                    isSuccess = false;
                    displayMsg = "Failed! Server error";
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (isSuccess)
                for (int i = 0; i < allDeals.size(); i++) {
                    if (allDeals.get(i).getDealId() == dealId) {
                        if (fromFav) {
                            allDeals.remove(i);
                        } else {
                            if (favChecked) {
                                allDeals.get(i).setFavourite(true);
                            } else {
                                allDeals.get(i).setFavourite(null);
                            }
                        }
                        notifyDataSetChanged();
                        break;
                    }
                }
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Snackbar.make(FavView, displayMsg,
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

}

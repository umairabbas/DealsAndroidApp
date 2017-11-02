package com.dealspok.dealspok.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dealspok.dealspok.LoginActivity;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.Utils.DoubleNameValuePair;
import com.dealspok.dealspok.Utils.IntNameValuePair;
import com.dealspok.dealspok.Utils.JSONParser;
import com.dealspok.dealspok.entities.DealObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class DealsAdapter extends RecyclerView.Adapter<DealsViewHolder>{

    private Context context;
    private Activity activity;
    private GradientDrawable gradientDrawable;
    private List<DealObject> allDeals;
    private int [] androidColors;
    private String URLFav = "/mobile/api/deals/favourite-click";//?userid=7&dealid=5&favcheck=true";

    public DealsAdapter(Context context, List<DealObject> allDeals) {
        this.context = context;
        activity = (Activity)context;
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
        final DealObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getDealTitle());
        holder.dealDescription.setText(deals.getDealDescription());
        holder.dealOldPrice.setText(Long.toString(deals.getOriginalPrice()) + " €");
        holder.dealPrice.setText(Long.toString(deals.getDealPrice()) + " €");
        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gradientDrawable.setColor(androidColors[new Random().nextInt(androidColors.length)]);
        Picasso.with(context).load(deals.getDealImageUrl(context)).placeholder(gradientDrawable).into(holder.dealCoverUrl);

        if(deals.getFavourite() != null) {
            if(deals.getFavourite()==true) {
                holder.favoriteImageButton.setColorFilter(activity.getResources().getColor(R.color.green));
                holder.favoriteImageButton.setEnabled(false);
            }
        } else {
            holder.favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavView = v;
                    Context context = v.getContext();
                    dealId = deals.getDealId();
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        try {
                            JSONObject obj = new JSONObject(restoredText);
                            userId = obj.getString("userId");
                            new DealsAdapter.favClick().execute();
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
                        "&dealid=" + Integer.toString(dealId) + "&favcheck=true");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                //conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                //conn.setDoInput(true);

//                Log.i("JSON", jsonParam.toString());
//                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
//                os.writeBytes(jsonParam.toString());
//
//                os.flush();
//                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

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

                if(message.equals(activity.getString(R.string.DEALS_FAV_CHECK))) {
                    isSuccess = true;
                    displayMsg = "Added to Favourites";
                    //onSignupSuccess();
                }
                else if(message.equals(activity.getString(R.string.DEALS_FAV_ERR))) {
                    isSuccess = true;
                    displayMsg = "Already added";
                }
                else {
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
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Snackbar.make(FavView, "Added to Favorite",
                            Snackbar.LENGTH_LONG).show();
                }
            });
            for(int i=0; i<allDeals.size(); i++){
                if(allDeals.get(i).getDealId() == dealId){
                    allDeals.get(i).setFavourite(true);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

}

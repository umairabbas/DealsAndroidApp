package com.dealspok.dealspok.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dealspok.dealspok.LoginActivity;
import com.dealspok.dealspok.MainActivity;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.GutscheineObject;
import com.dealspok.dealspok.fragment.Gutscheine;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class GutscheineAdapter extends RecyclerView.Adapter<GutscheineViewHolder> {

    private Context context;
    private Activity activity;
    private List<GutscheineObject> allDeals;
    private GradientDrawable gradientDrawable;
    private int [] androidColors;
    private Gutscheine fragment;

    public GutscheineAdapter(Context context, List<GutscheineObject> allDeals, Gutscheine frag) {
        fragment = frag;
        this.context = context;
        activity = (Activity)context;
        this.allDeals = allDeals;
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        androidColors = context.getResources().getIntArray(R.array.androidcolors);
    }

    @Override
    public GutscheineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gutscheinelist_layout, parent, false);
        return new GutscheineViewHolder(view, allDeals);
    }

    @Override
    public void onBindViewHolder(GutscheineViewHolder holder, int position) {
        final GutscheineObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getGutscheinTitle());
        holder.dealDescription.setText(deals.getGutscheinDescription());
        gradientDrawable.setColor(androidColors[new Random().nextInt(androidColors.length)]);
        Picasso.with(context).load(deals.getGutscheinImageUrl(context)).placeholder(gradientDrawable).into(holder.dealCoverUrl);
        dealPosition = position;
        if(deals.isGutscheinAvailed()){
            holder.mitMachenBtn.setEnabled(false);
            holder.mitMachenBtn.setTextColor(Color.GREEN);
        } else {
            holder.mitMachenBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    gutId = deals.getGutscheinId();
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        try {
                            JSONObject obj = new JSONObject(restoredText);
                            userId = obj.getString("userId");
                            new GutscheineAdapter.mitmachenClick().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Throwable t) {
                        }
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((MainActivity) fragment.getActivity()).setShouldRefresh(true);
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
    private int gutId = 0;
    private String userId = "";
    private String resultData = "";
    private int dealPosition;

    class mitmachenClick extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(AlbumsActivity.this);
//            pDialog.setMessage("Listing Albums ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String url = "https://www.regionaldeals.de/mobile/api/gutschein/gutscheinclick";

            try {

                com.dealspok.dealspok.Utils.HttpClient client = new com.dealspok.dealspok.Utils.HttpClient(url);
                client.connectForMultipart();
                client.addFormPart("gutscheinid", Integer.toString(gutId));
                client.addFormPart("userid", userId);
                //client.addFilePart("file", "logo.png", baos.toByteArray());
                client.finishMultipart();
                resultData = client.getResponse();
                resultData.toString();

            }
            catch(Throwable t) {
                t.printStackTrace();
            }

            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            //pDialog.dismiss();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, resultData, Toast.LENGTH_SHORT).show();
                }
            });
            for(int i=0; i<allDeals.size(); i++){
                if(allDeals.get(i).getGutscheinId() == gutId){
                    allDeals.get(i).setGutscheinAvailed(true);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

}

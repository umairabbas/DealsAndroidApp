package com.regionaldeals.de.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.regionaldeals.de.LoginActivity;
import com.regionaldeals.de.MainActivity;
import com.regionaldeals.de.R;
import com.regionaldeals.de.Utils.HttpClient;
import com.regionaldeals.de.entities.GutscheineObject;
import com.regionaldeals.de.fragment.Gutscheine;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GutscheineAdapter extends RecyclerView.Adapter<GutscheineViewHolder> {

    private Context context;
    private Activity activity;
    private List<GutscheineObject> allDeals;
    private GradientDrawable gradientDrawable;
    private int [] androidColors;
    private Gutscheine fragment;
    private int gutId = 0;
    private String userId = "";
    private String resultData = "";
    private int dealPosition;
    //from creategutscheine
    private Boolean isEdit = false;

    public GutscheineAdapter(Context context, List<GutscheineObject> allDeals, Gutscheine frag) {
        fragment = frag;
        this.context = context;
        activity = (Activity)context;
        this.allDeals = allDeals;
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        androidColors = context.getResources().getIntArray(R.array.androidcolors);
    }
    public GutscheineAdapter(Context context, List<GutscheineObject> allDeals, Boolean editGutscheien) {
        isEdit = editGutscheien;
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
        return new GutscheineViewHolder(view, allDeals, isEdit);
    }

    @Override
    public void onBindViewHolder(GutscheineViewHolder holder, int position) {
        final GutscheineObject deals = allDeals.get(position);
        holder.dealTitle.setText(deals.getGutscheinTitle());
        holder.dealDescription.setText(deals.getGutscheinDescription());
        //gradientDrawable.setColor(androidColors[new Random().nextInt(androidColors.length)]);
        String imgUrl = deals.getGutscheinImageUrl(context) + "&imagecount=1&res=470x320";
        Picasso.with(context).load(imgUrl).placeholder(R.drawable.placeholder_2_300x200).into(holder.dealCoverUrl);
        dealPosition = position;
        if(!isEdit){
            if(deals.isGutscheinAvailed()){
                holder.mitMachenBtn.setEnabled(false);
                holder.mitMachenBtn.setColorFilter(activity.getResources().getColor(R.color.colorAccent));
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
        } else {
            holder.mitMachenBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

    class mitmachenClick extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String url = "https://www.regionaldeals.de/mobile/api/gutschein/gutscheinclick";

            try {

                HttpClient client = new HttpClient(url);
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

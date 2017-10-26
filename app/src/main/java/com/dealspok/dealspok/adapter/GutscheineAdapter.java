package com.dealspok.dealspok.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dealspok.dealspok.LoginActivity;
import com.dealspok.dealspok.R;
import com.dealspok.dealspok.entities.GutscheineObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GutscheineAdapter extends RecyclerView.Adapter<GutscheineViewHolder> {

    private Context context;
    private Activity activity;
    private List<GutscheineObject> allDeals;

    public GutscheineAdapter(Context context, List<GutscheineObject> allDeals) {
        this.context = context;
        activity = (Activity)context;
        this.allDeals = allDeals;
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
        Picasso.with(context).load(deals.getGutscheinImageUrl(context)).placeholder(R.drawable.menucover).into(holder.dealCoverUrl);
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
            String url = "https://www.regionaldeals.de:80/mobile/api/gutschein/gutscheinclick";

            try {
//                    String url = params[0];
//                    String param1 = params[1];
//                    String param2 = params[2];
//                    Bitmap b = BitmapFactory.decodeResource(UploadActivity.this.getResources(), R.drawable.logo);
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    b.compress(CompressFormat.PNG, 0, baos);

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

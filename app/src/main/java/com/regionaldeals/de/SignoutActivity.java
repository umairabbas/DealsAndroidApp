package com.regionaldeals.de;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Umi on 11.03.2018.
 */

public class SignoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Activity activity;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signout_activity);

        toolbar = (Toolbar) findViewById(com.regionaldeals.de.R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = this;
        context = this;
        final SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("userObject", null);
        String restoredSub = prefs.getString("subscriptionObject", null);

        TextView email = findViewById(R.id.textEmail);

        if(getIntent().hasExtra("uemail")) {
            email.setText(getIntent().getStringExtra("uemail"));
        }

        Button signout = findViewById(R.id.b_signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Signing out.", Toast.LENGTH_SHORT).show();

                Intent intent = activity.getIntent();
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

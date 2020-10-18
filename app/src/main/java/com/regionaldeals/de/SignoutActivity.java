package com.regionaldeals.de;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.regionaldeals.de.Utils.PrefsHelper;

/**
 * Created by Umi on 11.03.2018.
 */

public class SignoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signout_activity);

        toolbar = (Toolbar) findViewById(com.regionaldeals.de.R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        final PrefsHelper prefHelper = PrefsHelper.Companion.getInstance(context);


        TextView email = findViewById(R.id.textEmail);

        if (getIntent().hasExtra("uemail")) {
            email.setText(getIntent().getStringExtra("uemail"));
        }

        Button signout = findViewById(R.id.b_signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Signing out.", Toast.LENGTH_SHORT).show();
                prefHelper.setEmail("");
                prefHelper.setUserId("");
                Intent intent = getIntent();
                setResult(Activity.RESULT_OK, intent);
                finish();
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

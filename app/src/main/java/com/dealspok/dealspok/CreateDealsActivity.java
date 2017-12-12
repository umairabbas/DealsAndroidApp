package com.dealspok.dealspok;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dealspok.dealspok.fragment.Favourite;

/**
 * Created by Umi on 10.12.2017.
 */

public class CreateDealsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_deals_main);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_deals);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startActivityIntent = new Intent(CreateDealsActivity.this, AddDealActivity.class);
                startActivity(startActivityIntent);
            }
        });
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
    }
}

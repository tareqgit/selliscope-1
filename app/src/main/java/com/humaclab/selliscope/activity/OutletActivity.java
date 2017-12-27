package com.humaclab.selliscope.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.adapters.OutletRecyclerViewAdapter;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.VerticalSpaceItemDecoration;

public class OutletActivity extends AppCompatActivity {
    private EditText tv_search_outlet;
    private RecyclerView recyclerView;
    private OutletRecyclerViewAdapter outletRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet);
        databaseHandler = new DatabaseHandler(this);

        FloatingActionButton addOutlet = (FloatingActionButton) findViewById(R.id.fab_add_outlet);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_outlet);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(OutletActivity.this)) {
                    getOutlets();
                } else {
                    getOutlets();
                    Toast.makeText(getApplicationContext(), "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.outle));
        setSupportActionBar(toolbar);
        addOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OutletActivity.this, AddOutletActivity.class));
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rv_outlet);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        });
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));
        if (NetworkUtility.isNetworkAvailable(this)) {
            getOutlets();
        } else {
            getOutlets();
            Toast.makeText(this, "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
        }

        tv_search_outlet = (EditText) findViewById(R.id.tv_search_outlet);
        tv_search_outlet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Outlets.Successful.OutletsResult outletsResult = databaseHandler.getSearchedOutlet(String.valueOf(s));
                outletRecyclerViewAdapter = new OutletRecyclerViewAdapter(OutletActivity.this, OutletActivity.this, outletsResult);
                recyclerView.setAdapter(outletRecyclerViewAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void getOutlets() {
        Outlets.Successful.OutletsResult outletsResult = databaseHandler.getAllOutlet();

        if (!outletsResult.outlets.isEmpty()) {
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            outletRecyclerViewAdapter = new OutletRecyclerViewAdapter(OutletActivity.this, OutletActivity.this, outletsResult);
            recyclerView.setAdapter(outletRecyclerViewAdapter);
        } else {
            Toast.makeText(getApplicationContext(), "You don't have any outlet in your list.", Toast.LENGTH_LONG).show();
        }
    }
}
package com.humaclab.selliscope.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.OutletRecyclerViewAdapter;
import com.humaclab.selliscope.databinding.ActivityOutletBinding;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.route_plan.RouteDetailsResponse;
import com.humaclab.selliscope.model.route_plan.RouteResponse;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.LoadLocalIntoBackground;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;
import com.humaclab.selliscope.utils.VerticalSpaceItemDecoration;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletActivity extends AppCompatActivity {
    private ActivityOutletBinding binding;
    private SelliscopeApiEndpointInterface apiService;
    private OutletRecyclerViewAdapter outletRecyclerViewAdapter;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private Outlets.OutletsResult outletsResult;
    private LoadLocalIntoBackground loadLocalIntoBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet);
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);

        loadLocalIntoBackground = new LoadLocalIntoBackground(this);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton addOutlet = findViewById(R.id.fab_add_outlet);

        addOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OutletActivity.this, AddOutletActivity.class));
            }
        });

        getRoute(); // For getting route plan data

        binding.rvOutlet.addItemDecoration(new VerticalSpaceItemDecoration(20));
        binding.rvOutlet.setLayoutManager(new LinearLayoutManager(this));

        if (!NetworkUtility.isNetworkAvailable(this)) {
            Toast.makeText(this, "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
        }

        binding.tvSearchOutlet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Outlets.OutletsResult outletsResult = databaseHandler.getSearchedOutlet(String.valueOf(s));
                outletRecyclerViewAdapter = new OutletRecyclerViewAdapter(OutletActivity.this, OutletActivity.this, outletsResult);
                binding.rvOutlet.setAdapter(outletRecyclerViewAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.srlOutlet.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtility.isNetworkAvailable(OutletActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
                }
                getOutlets();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getOutlets();
        getRoute(); // For getting route plan data
    }

    public void getOutlets() {
        outletsResult = databaseHandler.getAllOutlet();
        if (!outletsResult.outlets.isEmpty()) {
            if (binding.srlOutlet.isRefreshing())
                binding.srlOutlet.setRefreshing(false);
            outletRecyclerViewAdapter = new OutletRecyclerViewAdapter(OutletActivity.this, OutletActivity.this, outletsResult);
            binding.rvOutlet.setAdapter(outletRecyclerViewAdapter);
        } else {
            Toast.makeText(getApplicationContext(), "You don't have any outlet in your list.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * For getting route-plan data
     */
    public void getRoute() {
        Call<RouteResponse> call = apiService.getRoutes();
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (!response.body().getResult().getRoute().isEmpty()) {
                    Toast.makeText(OutletActivity.this, ""+response.body().getResult().getRoute().get(0).getName(), Toast.LENGTH_SHORT).show();
                    binding.tvToolbarTitle.setText(response.body().getResult().getRoute().get(0).getName());
                    getRouteDetails(response.body().getResult().getRoute().get(0).getId());
                } else {
                    binding.tvToolbarTitle.setText(getString(R.string.outlet));
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * For getting the details of route-plan by its ID
     *
     * @param routeID int
     */
    private void getRouteDetails(int routeID) {
        Call<RouteDetailsResponse> call = apiService.getRouteDetails(routeID);
        call.enqueue(new Callback<RouteDetailsResponse>() {
            @Override
            public void onResponse(Call<RouteDetailsResponse> call, Response<RouteDetailsResponse> response) {
                final List<RouteDetailsResponse.OutletItem> check = response.body().getResult().getOutletItemList();
                if (response.isSuccessful()) {
                    binding.tvCheckInCount.setText(response.body().getResult().getCheckedOutlet() + " / " + response.body().getResult().getTotalOutlet());
                    loadLocalIntoBackground.saveOutletRoutePlan(check);
                    getOutlets(); //For reloading the outlet recycler view
                }
            }

            @Override
            public void onFailure(Call<RouteDetailsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
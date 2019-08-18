package com.humaclab.selliscope.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

        addOutlet.setOnClickListener(v -> startActivity(new Intent(OutletActivity.this, AddOutletActivity.class)));

        binding.rvOutlet.addItemDecoration(new VerticalSpaceItemDecoration(20));
        binding.rvOutlet.setLayoutManager(new LinearLayoutManager(this));



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
binding.srlOutlet.setColorSchemeColors(Color.parseColor("#EA5455"), Color.parseColor("#FCCF31"), Color.parseColor("#F55555"));

        binding.srlOutlet.setOnRefreshListener(() -> {
            if (NetworkUtility.isNetworkAvailable(OutletActivity.this)) {
                LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(this);
                binding.srlOutlet.setRefreshing(true);
                loadLocalIntoBackground.loadOutlet(new LoadLocalIntoBackground.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        getOutlets();
                        binding.srlOutlet.setRefreshing(false);
                        Log.d("tareq_test" , "Outlets updated from Server");
                    }

                    @Override
                    public void onLoadFailed(String reason) {
                        Log.d("tareq_test" , "Outlets updated from Server");
                    }
                });



            }else{
                Toast.makeText(getApplicationContext(), "Connect to Wifi or Mobile Data for better performance.", Toast.LENGTH_SHORT).show();
            }


        });

   //if network is Available then update the data again
        if (NetworkUtility.isNetworkAvailable(this)) {
            LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(this);
            binding.srlOutlet.setRefreshing(true);
            loadLocalIntoBackground.loadOutlet(new LoadLocalIntoBackground.LoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    getOutlets();
                    Log.d("tareq_test" , "Outlets updated from Server");
                    binding.srlOutlet.setRefreshing(false);
                }

                @Override
                public void onLoadFailed(String reason) {
                    Log.d("tareq_test" , "Outlets updated from Server");
                }
            });
        }

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

            outletRecyclerViewAdapter = new OutletRecyclerViewAdapter(OutletActivity.this, OutletActivity.this, outletsResult);
            binding.rvOutlet.setAdapter(outletRecyclerViewAdapter);
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
                   // Toast.makeText(OutletActivity.this, ""+response.body().getResult().getRoute().get(0).getName(), Toast.LENGTH_SHORT).show();
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
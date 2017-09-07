package com.humaclab.selliscope;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope.Utils.NetworkUtility;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.adapters.DeliveryRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityDeliveryDetailsBinding;
import com.humaclab.selliscope.model.DeliveryResponse;
import com.humaclab.selliscope.model.GodownRespons;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDetailsActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private ActivityDeliveryDetailsBinding binding;

    private DeliveryResponse.DeliveryList deliveryList;
    private DeliveryRecyclerAdapter deliveryRecyclerAdapter;
    private List<GodownRespons.Godown> godownList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Delivery Product");
        setSupportActionBar(toolbar);

        deliveryList = (DeliveryResponse.DeliveryList) getIntent().getSerializableExtra("deliveryList");

        binding.rvDeliveryDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.setVariable(BR.deliveryDetails, deliveryList);

        binding.srlDeliveryDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(DeliveryDetailsActivity.this)) {
                    loadDeliveryDetails();
                } else {
                    Toast.makeText(DeliveryDetailsActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadDeliveryDetails();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDeliveryDetails() {
        if (binding.srlDeliveryDetails.isRefreshing())
            binding.srlDeliveryDetails.setRefreshing(false);
        loadGodown();
    }

    private void loadGodown() {
        SessionManager sessionManager = new SessionManager(DeliveryDetailsActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        Call<GodownRespons> call = apiService.getGodown();
        call.enqueue(new Callback<GodownRespons>() {
            @Override
            public void onResponse(Call<GodownRespons> call, Response<GodownRespons> response) {
                if (response.code() == 200) {
                    if (binding.srlDeliveryDetails.isRefreshing())
                        binding.srlDeliveryDetails.setRefreshing(false);
                    godownList = response.body().getResult().getGodownList();

                    binding.rvDeliveryDetails.setAdapter(new DeliveryRecyclerAdapter(DeliveryDetailsActivity.this, deliveryList, godownList));
                } else if (response.code() == 401) {
                    Toast.makeText(DeliveryDetailsActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeliveryDetailsActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GodownRespons> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

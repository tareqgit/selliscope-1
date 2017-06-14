package com.humaclab.selliscope;

import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope.Utils.NetworkUtility;
import com.humaclab.selliscope.adapters.SellsReturnDetailsRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivitySellsReturnDetailsBinding;
import com.humaclab.selliscope.model.DeliveryResponse;

public class SellsReturnDetailsActivity extends AppCompatActivity {
    private ActivitySellsReturnDetailsBinding binding;
    private DeliveryResponse.DeliveryList deliveryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sells_return_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sells_return_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Return Product");
        setSupportActionBar(toolbar);

        deliveryList = (DeliveryResponse.DeliveryList) getIntent().getSerializableExtra("deliveryList");

        binding.rvDeliveryDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.setVariable(com.humaclab.selliscope.BR.deliveryDetails, deliveryList);

        binding.srlDeliveryDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(SellsReturnDetailsActivity.this)) {
                    loadReturnDetails();
                } else {
                    Toast.makeText(SellsReturnDetailsActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (NetworkUtility.isNetworkAvailable(this)) {
            loadReturnDetails();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReturnDetails() {
        if (binding.srlDeliveryDetails.isRefreshing())
            binding.srlDeliveryDetails.setRefreshing(false);
        binding.rvDeliveryDetails.setAdapter(new SellsReturnDetailsRecyclerAdapter(SellsReturnDetailsActivity.this, deliveryList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

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
import com.humaclab.selliscope.adapters.OrderDetailsRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityOrderDetailsBinding;
import com.humaclab.selliscope.model.OrderResponse;

public class OrderDetailsActivity extends AppCompatActivity {
    private ActivityOrderDetailsBinding binding;

    private OrderResponse.OrderList orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Delivery Product");
        setSupportActionBar(toolbar);

        orderList = (OrderResponse.OrderList) getIntent().getSerializableExtra("orderList");

        binding.rvOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.setVariable(BR.orderDetails, orderList);

        binding.srlOrderDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(OrderDetailsActivity.this)) {
                    loadOrderDetails();
                } else {
                    Toast.makeText(OrderDetailsActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadOrderDetails();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrderDetails() {
        if (binding.srlOrderDetails.isRefreshing())
            binding.srlOrderDetails.setRefreshing(false);
        binding.rvOrderDetails.setAdapter(new OrderDetailsRecyclerAdapter(OrderDetailsActivity.this, orderList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

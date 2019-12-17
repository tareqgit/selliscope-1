package com.easyopstech.easyops.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.adapters.SellsReturnDetailsRecyclerAdapter;
import com.easyopstech.easyops.databinding.ActivitySalesReturnDetailsBinding;
import com.easyopstech.easyops.model.sales_return.SalesReturnResponse;
import com.easyopstech.easyops.utils.NetworkUtility;

public class SalesReturnDetailsActivity extends AppCompatActivity {
    private ActivitySalesReturnDetailsBinding binding;
    private SalesReturnResponse.DeliveryList deliveryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales_return_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Return Product");
        setSupportActionBar(toolbar);

        deliveryList = (SalesReturnResponse.DeliveryList) getIntent().getSerializableExtra("deliveryList");

        binding.rvDeliveryDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.setVariable(com.easyopstech.easyops.BR.deliveryDetails, deliveryList);

        binding.srlDeliveryDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(SalesReturnDetailsActivity.this)) {
                    loadReturnDetails();
                } else {
                    Toast.makeText(SalesReturnDetailsActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
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
        binding.rvDeliveryDetails.setAdapter(new SellsReturnDetailsRecyclerAdapter(SalesReturnDetailsActivity.this, deliveryList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

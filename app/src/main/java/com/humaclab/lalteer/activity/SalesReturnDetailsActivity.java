package com.humaclab.lalteer.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.adapters.SellsReturnDetailsRecyclerAdapter;
import com.humaclab.lalteer.databinding.ActivitySalesReturnDetailsBinding;
import com.humaclab.lalteer.model.DeliveryResponse;
import com.humaclab.lalteer.utils.NetworkUtility;

public class SalesReturnDetailsActivity extends AppCompatActivity {
    private ActivitySalesReturnDetailsBinding binding;
    private DeliveryResponse.DeliveryList deliveryList;

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

        deliveryList = (DeliveryResponse.DeliveryList) getIntent().getSerializableExtra("deliveryList");

        binding.rvDeliveryDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.setVariable(com.humaclab.lalteer.BR.deliveryDetails, deliveryList);

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

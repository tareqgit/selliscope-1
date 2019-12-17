package com.easyopstech.easyops.activity.activity_salesReturnOld;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.adapters.adapter_selesReturnOld.SellsReturnDetailsRecyclerAdapterOld;

import com.easyopstech.easyops.databinding.ActivitySalesReturnDetailsoldBinding;
import com.easyopstech.easyops.model.model_sales_return_old.DeliveryResponseOld;
import com.easyopstech.easyops.utils.NetworkUtility;

public class SalesReturnDetailsActivityOld extends AppCompatActivity {
    private ActivitySalesReturnDetailsoldBinding binding;
    private DeliveryResponseOld.DeliveryList deliveryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_detailsold);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales_return_detailsold);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Return Product");
        setSupportActionBar(toolbar);

        deliveryList = (DeliveryResponseOld.DeliveryList) getIntent().getSerializableExtra("deliveryList");

        binding.rvDeliveryDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.setVariable(com.easyopstech.easyops.BR.deliveryDetails, deliveryList);

        binding.srlDeliveryDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(SalesReturnDetailsActivityOld.this)) {
                    loadReturnDetails();
                } else {
                    Toast.makeText(SalesReturnDetailsActivityOld.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
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
        binding.rvDeliveryDetails.setAdapter(new SellsReturnDetailsRecyclerAdapterOld(SalesReturnDetailsActivityOld.this, deliveryList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

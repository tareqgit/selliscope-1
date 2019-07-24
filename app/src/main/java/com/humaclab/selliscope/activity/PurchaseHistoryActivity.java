package com.humaclab.selliscope.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.PurchaseHistoryRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityPurchaseHistoryBinding;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.purchase_history.PurchaseHistoryResponse;
import com.humaclab.selliscope.sales_return.model.get.DataItem;
import com.humaclab.selliscope.sales_return.model.get.SalesReturnGetResponse;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseHistoryActivity extends AppCompatActivity {
    private ActivityPurchaseHistoryBinding binding;
    private Outlets.Outlet outlet;
    private SelliscopeApiEndpointInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_history);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.purchase_history) + outlet.outletName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SessionManager sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        binding.rlPurchaseHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.srlPurchaseHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPurchaseHistory();
            }
        });
        binding.srlPurchaseHistory.setRefreshing(true);

        binding.btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchaseHistoryActivity.this, PaymentActivity.class);
                intent.putExtra("outletID",outlet.outletId);
                startActivity(intent);
            }
        });

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchaseHistoryActivity.this, OrderActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", String.valueOf(outlet.outletId));
                intent.putExtra("outletType", String.valueOf(outlet.outletType));
                startActivity(intent);
            }
        });
      getReturnProducts(); //this calls getPurchaseProducts
    }

    private void getPurchaseHistory() {
        Call<PurchaseHistoryResponse> call = apiService.getPurchaseHistory(outlet.outletId);
        call.enqueue(new Callback<PurchaseHistoryResponse>() {
            @Override
            public void onResponse(Call<PurchaseHistoryResponse> call, Response<PurchaseHistoryResponse> response) {
                if (response.code() == 200) {



                    binding.tvTotalPaid.setText(response.body().getResult().getTotalPaid());
                    binding.tvTotalDue.setText(response.body().getResult().getTotalDue());
                    binding.srlPurchaseHistory.setRefreshing(false);
                    binding.rlPurchaseHistory.setAdapter(new PurchaseHistoryRecyclerAdapter(PurchaseHistoryActivity.this, response.body().getResult().getPurchaseHistory(),salesReturnDataItems, outlet));
                } else {
                    Toast.makeText(PurchaseHistoryActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PurchaseHistoryResponse> call, Throwable t) {
                binding.srlPurchaseHistory.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }



    List<DataItem> salesReturnDataItems = new ArrayList<>();

    private void getReturnProducts() {
        Call<SalesReturnGetResponse> call = apiService.getSalesReturn(outlet.outletId);
        call.enqueue(new Callback<SalesReturnGetResponse>() {
            @Override
            public void onResponse(Call<SalesReturnGetResponse> call, Response<SalesReturnGetResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    salesReturnDataItems.addAll(response.body().getResult().getData());

                    getPurchaseHistory();
                } else {
                    Toast.makeText(PurchaseHistoryActivity.this, "Server error: on Sales Return Items " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SalesReturnGetResponse> call, Throwable t) {
               Log.e("tareq_test" , "Sales return get: "+ t.getMessage());
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

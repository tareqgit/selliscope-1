package com.humaclab.akij_selliscope.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.adapters.PurchaseHistoryRecyclerAdapter;
import com.humaclab.akij_selliscope.databinding.ActivityPurchaseHistoryBinding;
import com.humaclab.akij_selliscope.model.Outlets;
import com.humaclab.akij_selliscope.model.PurchaseHistory.PurchaseHistoryResponse;
import com.humaclab.akij_selliscope.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseHistoryActivity extends AppCompatActivity {
    private ActivityPurchaseHistoryBinding binding;
    private Outlets.Outlet outlet;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_history);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Purchase History-" + outlet.outletName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        binding.rlPurchaseHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.srlPurchaseHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPurchaseHistory();
            }
        });
        binding.srlPurchaseHistory.setRefreshing(true);

        /*binding.btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchaseHistoryActivity.this, PaymentActivity.class);
                intent.putExtra("outletID", outlet.outletId);
                startActivity(intent);
            }
        });*/
        if(sessionManager.getKeyAudit().equals("1")){
            binding.btnOrder.setVisibility(View.GONE);
        }

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outlet.line.equals("A")) {

                    Intent intent = new Intent(PurchaseHistoryActivity.this, ActivityLineA.class);
                    intent.putExtra("outletName", outlet.outletName);
                    intent.putExtra("outletID", String.valueOf(outlet.outletId));
                    intent.putExtra("outletType", String.valueOf(outlet.outletType));
                    startActivity(intent);

                }
                if (outlet.line.equals("B")) {
                    Intent intent = new Intent(PurchaseHistoryActivity.this, ActivityLineB.class);
                    intent.putExtra("outletName", outlet.outletName);
                    intent.putExtra("outletID", String.valueOf(outlet.outletId));
                    intent.putExtra("outletType", String.valueOf(outlet.outletType));
                    startActivity(intent);
                }
                //Intent intent = new Intent(PurchaseHistoryActivity.this, OrderActivity.class);
                //intent.putExtra("outletName", outlet.outletName);
                //intent.putExtra("outletID", String.valueOf(outlet.outletId));
                //intent.putExtra("outletType", String.valueOf(outlet.outletType));
                //startActivity(intent);
            }
        });
        getPurchaseHistory();
    }

    private void getPurchaseHistory() {
        Call<PurchaseHistoryResponse> call = apiService.getPurchaseHistory(outlet.outletId);
        call.enqueue(new Callback<PurchaseHistoryResponse>() {
            @Override
            public void onResponse(Call<PurchaseHistoryResponse> call, Response<PurchaseHistoryResponse> response) {
                //binding.tvTotalPaid.setText(response.body().getResult().getTotalPaid());
                //binding.tvTotalDue.setText(response.body().getResult().getTotalDue());
                binding.srlPurchaseHistory.setRefreshing(false);
                binding.rlPurchaseHistory.setAdapter(new PurchaseHistoryRecyclerAdapter(PurchaseHistoryActivity.this, response.body().getResult().getPurchaseHistory(), outlet));
            }

            @Override
            public void onFailure(Call<PurchaseHistoryResponse> call, Throwable t) {
                binding.srlPurchaseHistory.setRefreshing(false);
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

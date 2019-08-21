package com.humaclab.lalteer.activity;

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

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.PurchaseHistoryRecyclerAdapter;
import com.humaclab.lalteer.databinding.ActivityPurchaseHistoryBinding;
import com.humaclab.lalteer.model.Outlets;
import com.humaclab.lalteer.model.purchase_history.PurchaseHistoryResponse;
import com.humaclab.lalteer.utils.SessionManager;

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
        toolbarTitle.setText(getString(R.string.Purchase_History) + outlet.outletName);
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
                intent.putExtra("outletType", outlet.outletType);
                startActivity(intent);
            }
        });
        getPurchaseHistory();
    }

    private void getPurchaseHistory() {
        Call<PurchaseHistoryResponse> call = apiService.getPurchaseHistory(outlet.outletId);
        call.enqueue(new Callback<PurchaseHistoryResponse>() {
            @Override
            public void onResponse(Call<PurchaseHistoryResponse> call, Response<PurchaseHistoryResponse> response) {
                binding.tvTotalPaid.setText(response.body() != null ? response.body().getResult().getTotalPaid() : null);
                binding.tvTotalDue.setText(response.body() != null ? response.body().getResult().getTotalDue() : null);
                binding.srlPurchaseHistory.setRefreshing(false);
                binding.rlPurchaseHistory.setAdapter(new PurchaseHistoryRecyclerAdapter(PurchaseHistoryActivity.this, response.body() != null ? response.body().getResult().getPurchaseHistory() : null, outlet));
                Log.d("tareq_test" , "Res "+ response.code() +"  "+ new Gson().toJson(response.body() != null ? response.body().getResult() : null));
            }

            @Override
            public void onFailure(Call<PurchaseHistoryResponse> call, Throwable t) {
                binding.srlPurchaseHistory.setRefreshing(false);
                t.printStackTrace();
                Log.e("tareq_test" , "Error: "+ t.getMessage());
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

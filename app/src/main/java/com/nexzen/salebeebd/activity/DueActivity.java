package com.nexzen.salebeebd.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nexzen.salebeebd.R;
import com.nexzen.salebeebd.SelliscopeApiEndpointInterface;
import com.nexzen.salebeebd.SelliscopeApplication;
import com.nexzen.salebeebd.adapters.DueRecyclerViewAdapter;
import com.nexzen.salebeebd.model.Payment;
import com.nexzen.salebeebd.utils.DatabaseHandler;
import com.nexzen.salebeebd.utils.NetworkUtility;
import com.nexzen.salebeebd.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DueActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private RecyclerView rv_due;
    private SwipeRefreshLayout srl_due;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Dues");
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);

        rv_due = (RecyclerView) findViewById(R.id.rv_due);
        rv_due.setLayoutManager(new LinearLayoutManager(this));
        srl_due = (SwipeRefreshLayout) findViewById(R.id.srl_due);
        srl_due.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(DueActivity.this)) {
                    loadDues();
                } else {
                    Toast.makeText(DueActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadDues();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDues() {
        SessionManager sessionManager = new SessionManager(DueActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        Call<Payment> call = apiService.getPayment();
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.code() == 200) {
                    try {
                        if (srl_due.isRefreshing())
                            srl_due.setRefreshing(false);

                        System.out.println("Response " + new Gson().toJson(response.body()));
                        List<Payment.OrderList> orders = response.body().result.orderList;

                        rv_due.setAdapter(new DueRecyclerViewAdapter(getApplication(), orders));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(DueActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DueActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

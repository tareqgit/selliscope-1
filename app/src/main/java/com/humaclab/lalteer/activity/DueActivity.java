package com.humaclab.lalteer.activity;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.DueRecyclerViewAdapter;
import com.humaclab.lalteer.model.Payment;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

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

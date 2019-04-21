package com.humaclab.selliscope.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.SellsReturnRecyclerAdapter;
import com.humaclab.selliscope.model.sales_return.SalesReturnResponse;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesReturnActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private SwipeRefreshLayout srl_sells_return;
    private RecyclerView rv_return_list;
    private int outletID;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Sells Return List");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(SalesReturnActivity.this);
        pd = new ProgressDialog(this);

        outletID = getIntent().getIntExtra("outletID", 0);

        rv_return_list = (RecyclerView) findViewById(R.id.rv_return_list);
        rv_return_list.setLayoutManager(new LinearLayoutManager(this));

        srl_sells_return = (SwipeRefreshLayout) findViewById(R.id.srl_sells_return);
        srl_sells_return.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(SalesReturnActivity.this)) {
                    loadReturns();
                } else {
                    Toast.makeText(SalesReturnActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadReturns();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReturns() {
        pd.setMessage("Loading sales returns.....");
        pd.show();

        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
       // Call<DeliveryResponse> call = apiService.getSalesReturn();
       Call<SalesReturnResponse> call = apiService.getSalesReturnDAta();
        call.enqueue(new Callback<SalesReturnResponse>() {
            @Override
            public void onResponse(Call<SalesReturnResponse> call, Response<SalesReturnResponse> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    try {
                        if (srl_sells_return.isRefreshing())
                            srl_sells_return.setRefreshing(false);

                        System.out.println("Return Response " + new Gson().toJson(response.body()));
                        List<SalesReturnResponse.DeliveryList> salesReturnOrder = response.body().result.deliveryList;
                        rv_return_list.setAdapter(new SellsReturnRecyclerAdapter(getApplication(), salesReturnOrder));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(SalesReturnActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SalesReturnActivity.this,
                            response.code()+" Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SalesReturnResponse> call, Throwable t) {
                pd.dismiss();
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
            case R.id.actionHistory:
                Intent intent = new Intent(this, SalesReturnHistoryActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return true;
    }
}

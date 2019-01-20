package com.humaclab.akij_selliscope.activity.ActivitySalesReturnOld;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.adapters.AdapterSelesReturnOld.SellsReturnRecyclerAdapterOld;
import com.humaclab.akij_selliscope.model.ModelSalesReturnOld.DeliveryResponseOld;
import com.humaclab.akij_selliscope.utils.NetworkUtility;
import com.humaclab.akij_selliscope.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesReturnActivityOld extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private SwipeRefreshLayout srl_sells_return;
    private RecyclerView rv_return_list;
    private int outletID;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_old);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Sells Return List");
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(SalesReturnActivityOld.this);
        pd = new ProgressDialog(this);

        outletID = getIntent().getIntExtra("outletID", 0);

        rv_return_list = (RecyclerView) findViewById(R.id.rv_return_list);
        rv_return_list.setLayoutManager(new LinearLayoutManager(this));

        srl_sells_return = (SwipeRefreshLayout) findViewById(R.id.srl_sells_return);
        srl_sells_return.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(SalesReturnActivityOld.this)) {
                    loadReturns();
                } else {
                    Toast.makeText(SalesReturnActivityOld.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
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
        Call<DeliveryResponseOld> call = apiService.getSalesReturnOld(outletID);
        call.enqueue(new Callback<DeliveryResponseOld>() {
            @Override
            public void onResponse(Call<DeliveryResponseOld> call, Response<DeliveryResponseOld> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    try {
                        if (srl_sells_return.isRefreshing())
                            srl_sells_return.setRefreshing(false);

                        System.out.println("Return Response " + new Gson().toJson(response.body()));
                        List<DeliveryResponseOld.DeliveryList> delivers = response.body().result.deliveryList;
                        rv_return_list.setAdapter(new SellsReturnRecyclerAdapterOld(getApplication(), delivers));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(SalesReturnActivityOld.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SalesReturnActivityOld.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeliveryResponseOld> call, Throwable t) {
                pd.dismiss();
                t.printStackTrace();
            }
        });
    }
}

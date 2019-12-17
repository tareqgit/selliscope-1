package com.easyopstech.easyops.activity.activity_salesReturnOld;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.easyopstech.easyops.RootApplication;
import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.adapters.adapter_selesReturnOld.SellsReturnRecyclerAdapterOld;
import com.easyopstech.easyops.model.model_sales_return_old.DeliveryResponseOld;
import com.easyopstech.easyops.utils.NetworkUtility;
import com.easyopstech.easyops.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesReturnActivityOld extends AppCompatActivity {
    private RootApiEndpointInterface apiService;
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
        toolbarTitle.setText(getString(R.string.sells_return_list));
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

        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(RootApiEndpointInterface.class);
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

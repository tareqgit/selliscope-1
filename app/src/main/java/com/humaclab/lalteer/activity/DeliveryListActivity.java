package com.humaclab.lalteer.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.DeliveryListRecyclerAdapter;
import com.humaclab.lalteer.model.DeliveryResponse;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryListActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private SwipeRefreshLayout srl_delivery;
    private RecyclerView rv_delivery_list;
    private Spinner sp_outlet_list;
    private DatabaseHandler databaseHandler;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Delivery List");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHandler = new DatabaseHandler(DeliveryListActivity.this);
        sessionManager = new SessionManager(DeliveryListActivity.this);
        pd = new ProgressDialog(this);

        rv_delivery_list = (RecyclerView) findViewById(R.id.rv_delivery_list);
        rv_delivery_list.setLayoutManager(new LinearLayoutManager(this));

        srl_delivery = (SwipeRefreshLayout) findViewById(R.id.srl_delivery);
        srl_delivery.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(DeliveryListActivity.this)) {
                    loadDeliveries();
                } else {
                    Toast.makeText(DeliveryListActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadDeliveries();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }

        sp_outlet_list = (Spinner) findViewById(R.id.sp_outlet_list);
    }

    private void loadDeliveries() {
        pd.setMessage("Loading delivery list.....");
        pd.show();

        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        Call<DeliveryResponse> call = apiService.getDelivery();
        call.enqueue(new Callback<DeliveryResponse>() {
            @Override
            public void onResponse(Call<DeliveryResponse> call, Response<DeliveryResponse> response) {
                pd.dismiss();
                System.out.println("Response " + new Gson().toJson(response.body()));
                if (response.code() == 200) {
                    try {
                        if (srl_delivery.isRefreshing())
                            srl_delivery.setRefreshing(false);

                        List<DeliveryResponse.DeliveryList> delivers = response.body().result.deliveryList;
                        if (!delivers.isEmpty()) {
                            rv_delivery_list.setAdapter(new DeliveryListRecyclerAdapter(getApplication(), delivers));
                        } else {
                            Toast.makeText(getApplicationContext(), "You don't have any deliveries yet.", Toast.LENGTH_LONG).show();
                        }
                        storeDeliveriesIntoLocal(delivers);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(DeliveryListActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeliveryListActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeliveryResponse> call, Throwable t) {
                pd.dismiss();
                t.printStackTrace();
            }
        });
    }

    private void storeDeliveriesIntoLocal(List<DeliveryResponse.DeliveryList> delivers) {
        databaseHandler.removeDeliveryAndDeliveryProduct();
        databaseHandler.addDeliveryList(delivers);
        populateOutletList(databaseHandler.getOutlets());
    }

    private void populateOutletList(Map<String, List<String>> outlets) {
        final List<String> outletIDs = outlets.get("outletID");
        List<String> outletNames = outlets.get("outletName");

        sp_outlet_list.setAdapter(new ArrayAdapter<>(DeliveryListActivity.this, R.layout.spinner_item, outletNames));
        sp_outlet_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position != 0)
                        rv_delivery_list.setAdapter(new DeliveryListRecyclerAdapter(getApplication(),
                                databaseHandler.getDeliveries(Integer.parseInt(outletIDs.get(position)))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                pd.dismiss();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

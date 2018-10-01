package com.humaclab.selliscope.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.DeliveryRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityDeliveryDetailsBinding;
import com.humaclab.selliscope.model.DeliverProductResponse;
import com.humaclab.selliscope.model.DeliveryResponse;
import com.humaclab.selliscope.model.GodownRespons;
import com.humaclab.selliscope.utils.ImportentFunction;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDetailsActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private ActivityDeliveryDetailsBinding binding;

    private DeliveryResponse.DeliveryList deliveryList;
    private DeliveryRecyclerAdapter deliveryRecyclerAdapter;
    private List<GodownRespons.Godown> godownList = new ArrayList<>();
    //private ProgressDialog pd;
    String etQuantity;
    int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_details);
        ImportentFunction.deliveryArrayList.clear();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Delivery Product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deliveryList = (DeliveryResponse.DeliveryList) getIntent().getSerializableExtra("deliveryList");

        binding.rvDeliveryDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.setVariable(BR.deliveryDetails, deliveryList);

        binding.srlDeliveryDetails.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(DeliveryDetailsActivity.this)) {
                    loadDeliveryDetails();
                } else {
                    Toast.makeText(DeliveryDetailsActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadDeliveryDetails();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i<deliveryList.productList.size();i++){
            ImportentFunction.deliveryArrayList.add(Integer.valueOf(deliveryList.productList.get(i).qty));
        }


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliverProductResponse deliverProductResponse = new DeliverProductResponse();
                DeliverProductResponse.Order order = new DeliverProductResponse.Order();


                List<DeliverProductResponse.Order.Product> products = new ArrayList<>();

                for (int i = 0; i < deliveryList.productList.size(); i++) {
                    //DeliverProductResponse.Order.Product product1 = new DeliverProductResponse.Order.Product();

                    DeliverProductResponse.Order.Product product1 = new DeliverProductResponse.Order.Product();
                    product1.productId = deliveryList.productList.get(i).productId;
                    product1.variantRow = deliveryList.productList.get(i).variantRow;
                    //etQuantity = ((EditText) binding.rvDeliveryDetails.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.et_qty)).getText().toString();
                    etQuantity = ImportentFunction.deliveryArrayList.get(i).toString();
                    quantity = Integer.parseInt(etQuantity);
                    product1.qty = quantity;

                    product1.godownId = 0;
                    products.add(product1);
                }

                // order.products.add(product1);
                order.orderId = deliveryList.deliveryId;
                order.outletId = deliveryList.outletId;
                order.products = products;
                deliverProductResponse.order = order;


/*                pd = new ProgressDialog(context);
                pd.setMessage("Delivering your product....");
                pd.setCancelable(false);*/
                SessionManager sessionManager = new SessionManager(DeliveryDetailsActivity.this);
                SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
                Call<DeliverProductResponse> call = apiService.deliverProduct(deliverProductResponse);
                System.out.println("Delivery : " + new Gson().toJson(deliverProductResponse));
                call.enqueue(new Callback<DeliverProductResponse>() {
                    @Override
                    public void onResponse(Call<DeliverProductResponse> call, Response<DeliverProductResponse> response) {
                        // pd.dismiss();
                        if (response.code() == 201) {
                            Toast.makeText(DeliveryDetailsActivity.this, "Product delivered successfully", Toast.LENGTH_SHORT).show();
                            ((Activity) DeliveryDetailsActivity.this).finish();
                        } else if (response.code() == 200) {
                            Toast.makeText(DeliveryDetailsActivity.this, response.body().result, Toast.LENGTH_LONG).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(DeliveryDetailsActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DeliveryDetailsActivity.this, response.code() + "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DeliverProductResponse> call, Throwable t) {
                        //  pd.dismiss();
                        t.printStackTrace();
                    }
                });


            }
        });
    }

    private void loadDeliveryDetails() {
        if (binding.srlDeliveryDetails.isRefreshing())
            binding.srlDeliveryDetails.setRefreshing(false);
        loadGodown();
    }

    private void loadGodown() {
        SessionManager sessionManager = new SessionManager(DeliveryDetailsActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        Call<GodownRespons> call = apiService.getGodown();
        call.enqueue(new Callback<GodownRespons>() {
            @Override
            public void onResponse(Call<GodownRespons> call, Response<GodownRespons> response) {
                if (response.code() == 200) {
                    if (binding.srlDeliveryDetails.isRefreshing())
                        binding.srlDeliveryDetails.setRefreshing(false);
                    godownList = response.body().getResult().getGodownList();

                    binding.rvDeliveryDetails.setAdapter(new DeliveryRecyclerAdapter(DeliveryDetailsActivity.this, deliveryList, godownList));
                } else if (response.code() == 401) {
                    Toast.makeText(DeliveryDetailsActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeliveryDetailsActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GodownRespons> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
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

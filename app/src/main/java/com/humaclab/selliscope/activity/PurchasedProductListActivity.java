package com.humaclab.selliscope.activity;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.PurchaseHistoryRecyclerAdapter;
import com.humaclab.selliscope.adapters.PurchasedProductRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityPurchasedProductListBinding;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.purchase_history.PurchaseHistoryItem;
import com.humaclab.selliscope.model.purchase_history.PurchaseHistoryResponse;
import com.humaclab.selliscope.sales_return.model.get.DataItem;
import com.humaclab.selliscope.sales_return.model.get.SalesReturnGetResponse;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Purches History specific All Data
public class PurchasedProductListActivity extends AppCompatActivity {
    private ActivityPurchasedProductListBinding binding;
    private Outlets.Outlet outlet;
    private PurchaseHistoryItem purchaseHistoryItem;
    private SelliscopeApiEndpointInterface apiService;

    private List<DataItem> mSalesReturnItems =new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchased_product_list);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");
        purchaseHistoryItem = (PurchaseHistoryItem) getIntent().getSerializableExtra("product_list");
        List<DataItem> salesReturnItems = (List<DataItem>) getIntent().getSerializableExtra("salesReturnItems");

        for(DataItem dataItem : salesReturnItems){
            if(String.valueOf(dataItem.getOrder_id()).equals(purchaseHistoryItem.getOrderId()))
            mSalesReturnItems.add(dataItem);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(String.format("%s%s", getString(R.string.order_no), purchaseHistoryItem.getOrderId()));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        SessionManager sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        binding.rlPurchasedProduct.setLayoutManager(new LinearLayoutManager(this));

//        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PurchasedProductListActivity.this, OrderActivity.class);
//                intent.putExtra("outletName", outlet.outletName);
//                intent.putExtra("outletID", outlet.outletId);
//                startActivity(intent);
//            }
//        });


        getPurchasedProducts();
    }

    private void getPurchasedProducts() {
        binding.rlPurchasedProduct.setAdapter(new PurchasedProductRecyclerAdapter(PurchasedProductListActivity.this, purchaseHistoryItem.getOrderDetails(),mSalesReturnItems));
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

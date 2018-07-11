package com.nexzen.salebeebd.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nexzen.salebeebd.R;
import com.nexzen.salebeebd.adapters.PurchasedProductRecyclerAdapter;
import com.nexzen.salebeebd.databinding.ActivityPurchasedProductListBinding;
import com.nexzen.salebeebd.model.Outlets;
import com.nexzen.salebeebd.model.PurchaseHistory.PurchaseHistoryItem;
import com.nexzen.salebeebd.utils.SessionManager;
// Purches History specific All Data
public class PurchasedProductListActivity extends AppCompatActivity {
    private ActivityPurchasedProductListBinding binding;
    private SessionManager sessionManager;
    private Outlets.Outlet outlet;
    private PurchaseHistoryItem purchaseHistoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchased_product_list);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");
        purchaseHistoryItem = (PurchaseHistoryItem) getIntent().getSerializableExtra("product_list");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Order No: " + purchaseHistoryItem.getOrderId());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
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
        binding.rlPurchasedProduct.setAdapter(new PurchasedProductRecyclerAdapter(PurchasedProductListActivity.this, purchaseHistoryItem.getOrderDetails()));
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

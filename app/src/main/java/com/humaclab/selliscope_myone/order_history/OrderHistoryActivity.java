/*
 * Created by Tareq Islam on 5/28/19 9:44 AM
 *
 *  Last modified 5/28/19 9:44 AM
 */

package com.humaclab.selliscope_myone.order_history;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.activity.OrderActivity;
import com.humaclab.selliscope_myone.databinding.ActivityOrderHistoryBinding;
import com.humaclab.selliscope_myone.order_history.api.response_model.OrdersItem;
import com.humaclab.selliscope_myone.order_history.api.response_model.ProductsItem;
import com.humaclab.selliscope_myone.utils.SessionManager;

import java.io.Serializable;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OnOrderItemClickListener {

    private String outletName;
    private String outletID;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;


    private ActivityOrderHistoryBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Order History");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);


        //Get String  Extra from prev intent
        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");




      mBinding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OrderActivity.class);
                intent.putExtra("outletName", outletName);
                intent.putExtra("outletID", outletID);
                v.getContext().startActivity(intent);
            }

        });
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

    @Override
    public void onOrderClick(OrdersItem order) {
        Intent intent=new Intent(this,OrderHistoryProductActivity.class);
        //   Toast.makeText(this, ""+order.getProducts().get(0).getName(), Toast.LENGTH_SHORT).show();
        List<ProductsItem> mlist=order.getProducts();
        intent.putExtra("products", (Serializable) mlist);
        startActivity(intent);
    }
}

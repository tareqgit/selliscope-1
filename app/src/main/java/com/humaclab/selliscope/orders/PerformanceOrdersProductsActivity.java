/*
 * Created by Tareq Islam on 3/27/19 3:47 PM
 *
 *  Last modified 3/27/19 3:47 PM
 */

package com.humaclab.selliscope.orders;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ActivityPerformanceOrdersProductsBinding;
import com.humaclab.selliscope.model.PerformanceOrderModel.Product;

import java.util.ArrayList;
import java.util.List;

public class PerformanceOrdersProductsActivity extends AppCompatActivity {

    ActivityPerformanceOrdersProductsBinding mBinding;
    private List<Product> mProductList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      mBinding= DataBindingUtil.setContentView(PerformanceOrdersProductsActivity.this,R.layout.activity_performance_orders_products);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Orders");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.productRecycler.setLayoutManager(new LinearLayoutManager(this));
        mProductList= (List<Product>) getIntent().getSerializableExtra("products");
        mBinding.productRecycler.setAdapter(new PerformanceOrdersProductsActivityAdapter(this,mProductList));
    }
}

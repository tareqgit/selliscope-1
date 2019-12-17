/*
 * Created by Tareq Islam on 3/27/19 3:47 PM
 *
 *  Last modified 3/27/19 3:47 PM
 */

package com.easyopstech.easyops.performance.orders;

import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivityPerformanceOrdersProductsBinding;
import com.easyopstech.easyops.model.performance.orders_model.Product;

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
        toolbarTitle.setText(getString(R.string.products));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.productRecycler.setLayoutManager(new LinearLayoutManager(this));
        mProductList= (List<Product>) getIntent().getSerializableExtra("products");
        mBinding.productRecycler.setAdapter(new PerformanceOrdersProductsActivityAdapter(this,mProductList));
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

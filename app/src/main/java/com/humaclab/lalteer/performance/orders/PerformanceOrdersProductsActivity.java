/*
 * Created by Tareq Islam on 4/3/19 2:35 PM
 *
 *  Last modified 3/31/19 2:16 PM
 */

/*
 * Created by Tareq Islam on 3/27/19 3:47 PM
 *
 *  Last modified 3/27/19 3:47 PM
 */

package com.humaclab.lalteer.performance.orders;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.databinding.ActivityPerformanceOrdersProductsBinding;
import com.humaclab.lalteer.model.performance.OrdersModel.Product;

import java.util.ArrayList;
import java.util.List;

public class PerformanceOrdersProductsActivity extends AppCompatActivity {

    ActivityPerformanceOrdersProductsBinding mBinding;
    private List<Product> mProductList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      mBinding= DataBindingUtil.setContentView(PerformanceOrdersProductsActivity.this, R.layout.activity_performance_orders_products);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Products");
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

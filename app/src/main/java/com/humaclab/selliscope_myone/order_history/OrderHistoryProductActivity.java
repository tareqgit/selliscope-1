/*
 * Created by Tareq Islam on 5/28/19 3:51 PM
 *
 *  Last modified 5/28/19 3:50 PM
 */

package com.humaclab.selliscope_myone.order_history;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.databinding.ActivityOrderHistoryProductBinding;
import com.humaclab.selliscope_myone.order_history.api.response_model.ProductsItem;

import java.util.List;

public class OrderHistoryProductActivity extends AppCompatActivity {


   private ActivityOrderHistoryProductBinding mBinding;

    private OrderHistoryProductAdapter mOrderHistoryProductAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_history_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Ordered Products");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        mOrderHistoryProductAdapter=new OrderHistoryProductAdapter(this, (List< ProductsItem >) getIntent().getSerializableExtra("products"));

        mBinding.recyclerview.setAdapter(mOrderHistoryProductAdapter);
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

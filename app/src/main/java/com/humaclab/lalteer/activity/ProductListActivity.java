package com.humaclab.lalteer.activity;

import android.content.Context;
import android.content.res.Configuration;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.adapters.ProductListRecyclerAdapter;
import com.humaclab.lalteer.databinding.ActivityProductListBinding;
import com.humaclab.lalteer.helper.SelectedProductHelper;
import com.humaclab.lalteer.model.variant_product.Brand;
import com.humaclab.lalteer.model.variant_product.Category;
import com.humaclab.lalteer.model.variant_product.ProductsItem;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity  {
    private ActivityProductListBinding binding;
    private Context context;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private String outletName, outletID;
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();

    private List<SelectedProductHelper> selectedProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list);
        context = ProductListActivity.this;

        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.product_list));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(ProductListActivity.this);

        binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 2));

        getCategory();
        getBrand();
        getProducts();

        binding.etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()), s.toString());
                    binding.rvProduct.setAdapter(new ProductListRecyclerAdapter(context, ProductListActivity.this, productsItemList, selectedProductList));
                } else {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                    binding.rvProduct.setAdapter(new ProductListRecyclerAdapter(context, ProductListActivity.this, productsItemList, selectedProductList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.srlProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts();
            }
        });
        binding.srlProduct.setRefreshing(true);
    }

    private void getCategory() {
        List<Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add(getString(R.string.select_category));
        categoryID.add(0);
        for (Category result : categories) {
            categoryName.add(result.getName());
            categoryID.add(Integer.valueOf(result.getId()));
        }
        binding.spProductCategory.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, categoryName));
        binding.spProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.srlProduct.setRefreshing(false);
                List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(position), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                binding.rvProduct.setAdapter(new ProductListRecyclerAdapter(context, ProductListActivity.this, productsItemList, selectedProductList));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getBrand() {
        List<Brand> brands = databaseHandler.getBrand();
        brandName.clear();
        brandName.add(getString(R.string.select_brand));
        brandID.add(0);
        for (Brand result : brands) {
            brandName.add(result.getName());
            brandID.add(Integer.valueOf(result.getId()));
        }
        binding.spProductBrand.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, brandName));
        binding.spProductBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.srlProduct.setRefreshing(false);
                List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(position));
                binding.rvProduct.setAdapter(new ProductListRecyclerAdapter(context, ProductListActivity.this, productsItemList, selectedProductList));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getProducts() {
        binding.srlProduct.setRefreshing(false);
        List<ProductsItem> productsItemList = databaseHandler.getProduct(0, 0);
        binding.rvProduct.setAdapter(new ProductListRecyclerAdapter(context, ProductListActivity.this, productsItemList, selectedProductList));
        //if this activity called from product activity
    }





    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 3));
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
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

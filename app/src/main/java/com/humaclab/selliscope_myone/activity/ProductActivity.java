package com.humaclab.selliscope_myone.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.adapters.ProductRecyclerViewAdapter;
import com.humaclab.selliscope_myone.model.variantProduct.Brand;
import com.humaclab.selliscope_myone.model.variantProduct.Category;
import com.humaclab.selliscope_myone.model.variantProduct.ProductsItem;

import com.humaclab.selliscope_myone.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout srl_product;
    private Spinner sp_category, sp_brand;
    private EditText et_search_product;
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();
    private DatabaseHandler databaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.products));
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);


        sp_category = findViewById(R.id.sp_category);
        sp_brand = findViewById(R.id.sp_brand);
        et_search_product = findViewById(R.id.et_search_product);
        et_search_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getApplication(), databaseHandler.getSearchedProduct(s.toString())));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRecyclerView = findViewById(R.id.rv_product);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        srl_product = findViewById(R.id.srl_product);
        srl_product.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*if (NetworkUtility.isNetworkAvailable(ProductActivity.this)) {
                    getCategory();
                    getBrand();
                    getProducts();
                } else {*/
                getFromLocal();
                    /*Toast.makeText(ProductActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
        /*if (NetworkUtility.isNetworkAvailable(this)) {
            getCategory();
            getBrand();
            getProducts();
        } else {*/
        getFromLocal();
        /*Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }*/

        //Populating spinner
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getApplication(), databaseHandler.getProduct(categoryID.get(position), brandID.get(sp_brand.getSelectedItemPosition()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getApplication(), databaseHandler.getProduct(categoryID.get(sp_category.getSelectedItemPosition()), brandID.get(position))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Populating spinner
    }

    /*private void getProducts() {
        final SessionManager sessionManager = new SessionManager(ProductActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ProductSearchResponse> call = apiService.getProducts();
        call.enqueue(new Callback<ProductSearchResponse>() {
            @Override
            public void onResponse(Call<ProductSearchResponse> call, Response<ProductSearchResponse> response) {
                if (response.code() == 200) {
                    try {
                        if (srl_product.isRefreshing())
                            srl_product.setRefreshing(false);

                        getFromLocal();
                        List<ProductSearchResponse.ProductResult> products = response.body().result.productResults;
                        if (products.size() == databaseHandler.getSizeOfProduct()) {
                            //for removing previous data
                            databaseHandler.removeProductCategoryBrand();
                            for (ProductSearchResponse.ProductResult result : products) {
                                databaseHandler.addProduct(result.id, result.name, result.price, result.img, result.brand.id, result.brand.name, result.category.id, result.category.name);
                            }
                            recyclerview.setAdapter(new ProductRecyclerViewAdapter(getApplication(), products));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(ProductActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductSearchResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }*/

    private void getFromLocal() {
        if (srl_product.isRefreshing())
            srl_product.setRefreshing(false);
        List<ProductsItem> products = databaseHandler.getProduct(0, 0);
        mRecyclerView.setAdapter(new ProductRecyclerViewAdapter(getApplication(), products));

        //For spinner
        List<Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add("Select Category");
        categoryID.add(0);
        for (Category result : categories) {
            categoryName.add(result.getName());
            categoryID.add(Integer.valueOf(result.getId()));
        }
        sp_category.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categoryName));
        sp_category.setSelection(0);

        List<Brand> brands = databaseHandler.getBrand();
        brandName.clear();
        brandName.add("Select Brand");
        brandID.add(0);
        for (Brand result : brands) {
            brandName.add(result.getName());
            brandID.add(Integer.valueOf(result.getId()));
        }
        sp_brand.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, brandName));
        sp_brand.setSelection(0);
        //For spinner
    }



}

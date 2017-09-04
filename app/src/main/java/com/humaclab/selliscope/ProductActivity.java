package com.humaclab.selliscope;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.humaclab.selliscope.Utils.DatabaseHandler;
import com.humaclab.selliscope.adapters.ProductRecyclerViewAdapter;
import com.humaclab.selliscope.model.ProductResponse;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private RecyclerView rv_product;
    private SwipeRefreshLayout srl_product;
    private Spinner sp_category, sp_brand;
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.products));
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);
        sp_category = (Spinner) findViewById(R.id.sp_category);
        sp_brand = (Spinner) findViewById(R.id.sp_brand);

        rv_product = (RecyclerView) findViewById(R.id.rv_product);
        rv_product.setLayoutManager(new LinearLayoutManager(this));
        srl_product = (SwipeRefreshLayout) findViewById(R.id.srl_product);
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
                    rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), databaseHandler.getProduct(categoryID.get(position), brandID.get(sp_brand.getSelectedItemPosition()))));
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
                    rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), databaseHandler.getProduct(categoryID.get(sp_category.getSelectedItemPosition()), brandID.get(position))));
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
        Call<ProductResponse> call = apiService.getProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.code() == 200) {
                    try {
                        if (srl_product.isRefreshing())
                            srl_product.setRefreshing(false);

                        getFromLocal();
                        List<ProductResponse.ProductResult> products = response.body().result.productResults;
                        if (products.size() == databaseHandler.getSizeOfProduct()) {
                            //for removing previous data
                            databaseHandler.removeProductCategoryBrand();
                            for (ProductResponse.ProductResult result : products) {
                                databaseHandler.addProduct(result.id, result.name, result.price, result.img, result.brand.id, result.brand.name, result.category.id, result.category.name);
                            }
                            rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), products));
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
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }*/

    private void getFromLocal() {
        if (srl_product.isRefreshing())
            srl_product.setRefreshing(false);
        List<ProductResponse.ProductResult> products = databaseHandler.getProduct(0, 0);
        rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), products));

        //For spinner
        List<ProductResponse.Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add("Select Category");
        categoryID.add(0);
        for (ProductResponse.Category result : categories) {
            categoryName.add(result.name);
            categoryID.add(result.id);
        }
        sp_category.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categoryName));
        sp_category.setSelection(0);

        List<ProductResponse.Brand> brands = databaseHandler.getBrand();
        brandName.clear();
        brandName.add("Select Brand");
        brandID.add(0);
        for (ProductResponse.Brand result : brands) {
            brandName.add(result.name);
            brandID.add(result.id);
        }
        sp_brand.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, brandName));
        sp_brand.setSelection(0);
        //For spinner
    }
}

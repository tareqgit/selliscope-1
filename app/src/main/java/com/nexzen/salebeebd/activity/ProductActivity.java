package com.nexzen.salebeebd.activity;

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

import com.nexzen.salebeebd.R;
import com.nexzen.salebeebd.SelliscopeApiEndpointInterface;
import com.nexzen.salebeebd.adapters.ProductRecyclerViewAdapter;
import com.nexzen.salebeebd.model.VariantProduct.Brand;
import com.nexzen.salebeebd.model.VariantProduct.Category;
import com.nexzen.salebeebd.model.VariantProduct.ProductsItem;
import com.nexzen.salebeebd.utils.DatabaseHandler;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.products));
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);
        sp_category = findViewById(R.id.sp_category);
        sp_brand = findViewById(R.id.sp_brand);

        rv_product = findViewById(R.id.rv_product);
        rv_product.setLayoutManager(new LinearLayoutManager(this));
        srl_product = findViewById(R.id.srl_product);
        srl_product.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFromLocal();
            }
        });
        getFromLocal();

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

    private void getFromLocal() {
        if (srl_product.isRefreshing())
            srl_product.setRefreshing(false);
        List<ProductsItem> products = databaseHandler.getProduct(0, 0);
        rv_product.setAdapter(new ProductRecyclerViewAdapter(getApplication(), products));

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

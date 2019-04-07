package com.humaclab.lalteer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.adapters.OrderProductRecyclerAdapter;
import com.humaclab.lalteer.databinding.ActivityOrderBinding;
import com.humaclab.lalteer.helper.SelectedProductHelper;
import com.humaclab.lalteer.interfaces.OnSelectProduct;
import com.humaclab.lalteer.model.variant_product.Brand;
import com.humaclab.lalteer.model.variant_product.Category;
import com.humaclab.lalteer.model.variant_product.ProductsItem;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.SessionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements OnSelectProduct {
    private ActivityOrderBinding binding;
    private Context context;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private String outletName, outletID;
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();

    public static List<SelectedProductHelper> selectedProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        context = OrderActivity.this;

        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(outletName + "-" + getResources().getString(R.string.order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(OrderActivity.this);

        binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 2));

        getCategory();
        getBrand();
        //  getProducts();

        binding.etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()), s.toString());
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList));
                } else {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.srlProduct.setOnRefreshListener(() -> {
            getProducts();
        });
        binding.srlProduct.setRefreshing(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getProducts();

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
                if (position != 0) {

                    binding.srlProduct.setRefreshing(false);
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(position), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList));
                }
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
                if (position != 0) {

                    binding.srlProduct.setRefreshing(false);
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(position));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getProducts() {

        binding.srlProduct.setRefreshing(false);
        List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
        binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList));
        //if this activity called from product activity
        updateTotal_Discount_Grnd();
    }

    void updateTotal_Discount_Grnd() {

        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
            //totalDiscount += Double.valueOf(selectedProductHelper.getTotalPrice());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));

        //binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt)));

    }

    @Override
    public void onSetSelectedProduct(SelectedProductHelper selectedProduct) {
        for (SelectedProductHelper selected : selectedProductList) {
            if (selected.getProductName().equalsIgnoreCase(selectedProduct.getProductName()) && selected.getProductID().equalsIgnoreCase(selectedProduct.getProductID())) {
                Log.d("tareq_test", "product matched" + selected.getProductName());

                selectedProductList.remove(selected);
            }
        }
     /*   if (selectedProductList.contains(selectedProduct)) {
            selectedProductList.remove(selectedProductList.indexOf(selectedProduct));
            selectedProductList.add(selectedProductList.indexOf(selectedProduct), selectedProduct);
        } else*/
        selectedProductList.add(selectedProduct);

        System.out.println("Product list:" + new Gson().toJson(selectedProductList) + "\nIndex: " + selectedProductList.indexOf(selectedProduct));

        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
            //totalDiscount += Double.valueOf(selectedProductHelper.getTotalPrice());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));

        //binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt)));

        getProducts();//for refreshing
    }

    @Override
    public void onRemoveSelectedProduct(SelectedProductHelper selectedProduct) {
        if (selectedProductList.contains(selectedProduct)) {
            selectedProductList.remove(selectedProductList.indexOf(selectedProduct));
        }

        System.out.println("Product list:" + new Gson().toJson(selectedProductList) + "\nIndex: " + selectedProductList.indexOf(selectedProduct));

        Double totalAmt = 0.00;

        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());

        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));

        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt)));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvProduct.setLayoutManager(new GridLayoutManager(context, 2));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        //clear selected Item list
        selectedProductList.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //clear selected Item list
        selectedProductList.clear();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_cart:
                if (!selectedProductList.isEmpty()) {
                    Intent intent = new Intent(OrderActivity.this, ActivityCart.class);
                    intent.putExtra("outletID", outletID);
                    intent.putExtra("outletName", outletName);
                    intent.putExtra("products", (Serializable) selectedProductList);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "No product selected yet.\nPlease select some product first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.humaclab.selliscope.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.Toolbar;
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
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.adapters.OrderProductRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityOrderBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;
import com.humaclab.selliscope.model.variant_product.Brand;
import com.humaclab.selliscope.model.variant_product.Category;
import com.humaclab.selliscope.model.variant_product.ProductsItem;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static androidx.core.view.MenuItemCompat.getActionView;
import static com.humaclab.selliscope.sales_return.SalesReturn_2019_Activity.sSalesReturn2019SelectedProducts;

public class OrderActivity extends AppCompatActivity implements OrderProductRecyclerAdapter.OnSelectProductListener {
    private ActivityOrderBinding binding;
    private Context context;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private String outletName, outletID, outletType;
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();

    public static List<SelectedProductHelper> selectedProductList = new ArrayList<>();

    // Save state
    private Parcelable recyclerViewState; //for storing recycler scroll postion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        context = OrderActivity.this;

        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");
        outletType = getIntent().getStringExtra("outletType");

        Toolbar toolbar = findViewById(R.id.toolbar);
       /* toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(outletName + "-" + getResources().getString(R.string.order));*/
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
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList, outletType,OrderActivity.this));
                } else {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList, outletType,OrderActivity.this));
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

        getProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mOrderProductRecyclerAdapter.notifyDataSetChanged(); //we are Notify to update recycler view cz if we delete any Item from cart should be live in this activity also
        // Restore state
        binding.rvProduct.getLayoutManager().onRestoreInstanceState(recyclerViewState); //we are restoring recycler position
        update_Total_Discount_Grnd();

    }

    private void getCategory() {
        List<Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add("Category");
        categoryID.add(0);
        for (Category result : categories) {
            categoryName.add(result.getName());
            categoryID.add(Integer.valueOf(result.getId()));
        }
        binding.spProductCategory.setAdapter(new ArrayAdapter<>(context, R.layout.color_spinner_layout_white, categoryName));
        binding.spProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    binding.srlProduct.setRefreshing(false);
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(position), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList, outletType,OrderActivity.this));
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
        brandName.add("Brand");
        brandID.add(0);
        for (Brand result : brands) {
            brandName.add(result.getName());
            brandID.add(Integer.valueOf(result.getId()));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.color_spinner_layout_white, brandName);
       // ArrayAdapter adapter= (ArrayAdapter) ArrayAdapter.createFromResource(this,brandName,R.layout.color_spinner_layout_white);
        binding.spProductBrand.setAdapter(adapter);
        binding.spProductBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    binding.srlProduct.setRefreshing(false);
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(position));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList, outletType,OrderActivity.this));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    OrderProductRecyclerAdapter mOrderProductRecyclerAdapter;

    public void getProducts() {
        binding.srlProduct.setRefreshing(false);
        List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
        mOrderProductRecyclerAdapter =  new OrderProductRecyclerAdapter(context, OrderActivity.this, productsItemList, selectedProductList, outletType,this);
        binding.rvProduct.setAdapter(mOrderProductRecyclerAdapter);
        //if this activity called from product activity
        update_Total_Discount_Grnd();
    }

    private void update_Total_Discount_Grnd() {

        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
            totalDiscount += Double.valueOf(selectedProductHelper.getTpDiscount());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));
        binding.tvTotalDiscnt.setText(String.format("%.2f", totalDiscount));
        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        updateBadge();
    }

    @Override
    public void onSetSelectedProduct(SelectedProductHelper selectedProduct) {

        for (Iterator<SelectedProductHelper> iterator = selectedProductList.iterator(); iterator.hasNext(); ) {
            SelectedProductHelper selected = iterator.next();
            if (selected.getProductName().equalsIgnoreCase(selectedProduct.getProductName()) && selected.getProductID().equalsIgnoreCase(selectedProduct.getProductID())) {
                Log.d("tareq_test", "product matched" + selected.getProductName());
                iterator.remove();
            }
        }


        /*for (SelectedProductHelper selected : selectedProductList) {
            if (selected.getProductName().equalsIgnoreCase(selectedProduct.getProductName()) && selected.getProductID().equalsIgnoreCase(selectedProduct.getProductID())) {
                Log.d("tareq_test", "product matched" + selected.getProductName());

                selectedProductList.remove(selected);
            }
        }*/
     /*   if (selectedProductList.contains(selectedProduct)) {
            selectedProductList.remove(selectedProductList.indexOf(selectedProduct));
            selectedProductList.add(selectedProductList.indexOf(selectedProduct), selectedProduct);
        } else
     */
        selectedProductList.add(selectedProduct);

        System.out.println("Product list:" + new Gson().toJson(selectedProductList) + "\nIndex: " + selectedProductList.indexOf(selectedProduct));

        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
            totalDiscount += Double.valueOf(selectedProductHelper.getTpDiscount());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));
        binding.tvTotalDiscnt.setText(String.format("%.2f", totalDiscount));
        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        Log.d("tareq_test", "" + totalAmt + " " + totalDiscount + " ");

       // getProducts();//for refreshing

        updateBadge();
    }

    @Override
    public void onRemoveSelectedProduct(SelectedProductHelper selectedProduct) {
        if (selectedProductList.contains(selectedProduct)) {
            selectedProductList.remove(selectedProductList.indexOf(selectedProduct));
        }

        System.out.println("Product list:" + new Gson().toJson(selectedProductList) + "\nIndex: " + selectedProductList.indexOf(selectedProduct));


        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
            totalDiscount += Double.valueOf(selectedProductHelper.getTpDiscount());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));
        binding.tvTotalDiscnt.setText(String.format("%2f", totalDiscount));

        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        Log.d("tareq_test", "" + totalAmt + " " + totalDiscount + " ");

        updateBadge();
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

    TextView textCartItemCount;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        updateBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    public void updateBadge() {

        if (textCartItemCount != null) {
            if (selectedProductList.size() == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(selectedProductList.size(), 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        //clear selected Item list
        sSalesReturn2019SelectedProducts.clear();
        selectedProductList.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //clear selected Item list
        sSalesReturn2019SelectedProducts.clear();
        selectedProductList.clear();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                Intent intent1 = new Intent(OrderActivity.this, OutletActivity.class);
                startActivity(intent1);
                return true;
            case R.id.action_cart:
                if (!selectedProductList.isEmpty()) {
                    Intent intent = new Intent(OrderActivity.this, ActivityCart.class);
                    intent.putExtra("outletID", outletID);
                    intent.putExtra("outletName", outletName);
                    intent.putExtra("products", (Serializable) selectedProductList);


                    recyclerViewState = binding.rvProduct.getLayoutManager().onSaveInstanceState(); //before moving to another activity store recycler state

                    startActivity(intent);
                } else {
                    Toast.makeText(context, "No product selected yet.\nPlease select some product first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

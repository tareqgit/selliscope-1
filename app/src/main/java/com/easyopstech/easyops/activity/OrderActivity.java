package com.easyopstech.easyops.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;

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

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.adapters.OrderProductRecyclerAdapter;
import com.easyopstech.easyops.databinding.ActivityOrderBinding;
import com.easyopstech.easyops.helper.SelectedProductHelper;
import com.easyopstech.easyops.model.variant_product.Brand;
import com.easyopstech.easyops.model.variant_product.Category;
import com.easyopstech.easyops.model.variant_product.ProductsItem;
import com.easyopstech.easyops.utils.DatabaseHandler;
import com.easyopstech.easyops.utils.LoadLocalIntoBackground;
import com.easyopstech.easyops.utils.NetworkUtility;
import com.easyopstech.easyops.utils.SessionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.easyopstech.easyops.sales_return.SalesReturn_2019_Activity.sSalesReturn2019SelectedProducts;

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
        binding.srlProduct.setColorSchemeColors(Color.parseColor("#EA5455"), Color.parseColor("#FCCF31"), Color.parseColor("#F55555"));

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
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, productsItemList, selectedProductList, outletType, OrderActivity.this));
                } else {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, productsItemList, selectedProductList, outletType, OrderActivity.this));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(OrderActivity.this);

        getProducts();
        binding.srlProduct.setRefreshing(false);

        binding.srlProduct.setOnRefreshListener(() -> {
            //if network is Available then update the data again
            if (NetworkUtility.isNetworkAvailable(OrderActivity.this)) {

                binding.srlProduct.setRefreshing(true);
                loadLocalIntoBackground.loadProduct(new LoadLocalIntoBackground.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        getProducts();
                        getCategory();
                        getBrand();
                        binding.srlProduct.setRefreshing(false);
                        Log.d("tareq_test" , "Loading data complete");
                    }

                    @Override
                    public void onLoadFailed(String reason) {
                        Log.e("tareq_test" , "Loading data failed");
                        binding.srlProduct.setRefreshing(false);
                    }
                });


            }

        });


        //if network is Available then update the data again
       /* if (NetworkUtility.isNetworkAvailable(OrderActivity.this)) {

            binding.srlProduct.setRefreshing(true);
            loadLocalIntoBackground.loadProduct(new LoadLocalIntoBackground.LoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    getProducts();
                    getCategory();
                    getBrand();
                    binding.srlProduct.setRefreshing(false);
                    Log.d("tareq_test" , "Loading data complete");
                }

                @Override
                public void onLoadFailed(String reason) {
                    Log.e("tareq_test" , "Loading data failed");
                    binding.srlProduct.setRefreshing(false);
                }
            });


        }*/
        // getProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getProducts();
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
                    //  binding.srlProduct.setRefreshing(false);
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(position), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, productsItemList, selectedProductList, outletType, OrderActivity.this));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getBrand() {
        List<Brand> brands = databaseHandler.getBrand();
        Log.d("tareq_test", "Brand Size: " + brands.size());
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
                    //   binding.srlProduct.setRefreshing(false);
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(position));
                    binding.rvProduct.setAdapter(new OrderProductRecyclerAdapter(context, productsItemList, selectedProductList, outletType, OrderActivity.this));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    OrderProductRecyclerAdapter mOrderProductRecyclerAdapter;

    public void getProducts() {
        // binding.srlProduct.setRefreshing(false);
        List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(binding.spProductCategory.getSelectedItemPosition()), brandID.get(binding.spProductBrand.getSelectedItemPosition()));
        Log.d("tareq_test", "product size" + productsItemList.size());
        mOrderProductRecyclerAdapter = new OrderProductRecyclerAdapter(context, productsItemList, selectedProductList, outletType, this);
        binding.rvProduct.setAdapter(mOrderProductRecyclerAdapter);
        //if this activity called from product activity
        update_Total_Discount_Grnd();
    }

    private void update_Total_Discount_Grnd() {

        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice().replace(",",""));
            totalDiscount += Double.valueOf(selectedProductHelper.getTpDiscount().replace(",",""));
        }
        binding.tvTotalAmt.setText(String.format(Locale.ENGLISH, "%.2f", totalAmt));
        binding.tvTotalDiscnt.setText(String.format(Locale.ENGLISH, "%.2f", totalDiscount));
        binding.tvTotalGr.setText(String.format(Locale.ENGLISH, "%.2f", (totalAmt - totalDiscount)));
        updateBadge();
    }

    @Override
    public void onSetSelectedProduct(List<SelectedProductHelper> selectedProducts) {

        List<SelectedProductHelper> tempProductList = new ArrayList<>(selectedProductList);

        for (SelectedProductHelper selected : tempProductList) {


            if (selected.getProductName().equalsIgnoreCase(selectedProducts.get(0).getProductName())//as 0 no product of selected product list os ordered product
                    && selected.getProductID().equalsIgnoreCase(selectedProducts.get(0).getProductID())
                    && !selected.isFree()//if the products in the cart is not free then we replace the product.
            ) {
                Log.d("tareq_test", "product matched" + selected.getProductName());


                //region Free product should be removed along with Ordered product
                //if next product is free product then remove that too
                try {
                    int indexOfSelectedProduct = selectedProductList.indexOf(selected);
                    if (selectedProductList.get(indexOfSelectedProduct + 1).isFree())
                        selectedProductList.remove(indexOfSelectedProduct + 1);
                } catch (Exception e) {
                    Log.e("tareq_test", "Error while removing next free product");
                }

                selectedProductList.remove(selected); //as we removing free item according to index with the help of SelectedProduct we can't remove this first. First remove free Item then Order Item
                //endregion

            }
        }


        selectedProductList.addAll(selectedProducts);


        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice().equals("")?"0":selectedProductHelper.getTotalPrice().replace(",","")) ;
            totalDiscount += Double.valueOf(selectedProductHelper.getTpDiscount().replace(",",""));
        }
        binding.tvTotalAmt.setText(String.format(Locale.ENGLISH, "%.2f", totalAmt));
        binding.tvTotalDiscnt.setText(String.format(Locale.ENGLISH, "%.2f", totalDiscount));
        binding.tvTotalGr.setText(String.format(Locale.ENGLISH, "%.2f", (totalAmt - totalDiscount)));
        Log.d("tareq_test", "" + totalAmt + " " + totalDiscount + " ");


        updateBadge(); //on product add in the cart list update badge
    }

    @Override
    public void onRemoveSelectedProduct(SelectedProductHelper selectedProduct) {

        //region Remove the free product along with ordered product
        //if next product is free product then remove that too
        try {
            int indexOfSelectedProduct = selectedProductList.indexOf(selectedProduct);
            if (selectedProductList.get(indexOfSelectedProduct + 1).isFree())
                selectedProductList.remove(indexOfSelectedProduct + 1);
        } catch (Exception e) {
            Log.e("tareq_test", "Error while removing next free product");
        }

        selectedProductList.remove(selectedProduct);//as we removing free item according to index with the help of SelectedProduct we can't remove this first. First remove free Item then Order Item
        //endregion


        Double totalAmt = 0.00;
        Double totalDiscount = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice().replace(",",""));
            totalDiscount += Double.valueOf(selectedProductHelper.getTpDiscount());
        }
        binding.tvTotalAmt.setText(String.format("%.2f", totalAmt));
        binding.tvTotalDiscnt.setText(String.format("%2f", totalDiscount));

        binding.tvTotalGr.setText(String.format("%.2f", (totalAmt - totalDiscount)));
        Log.d("tareq_test", "" + totalAmt + " " + totalDiscount + " ");

        updateBadge(); //on remove item need to update the badge
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
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

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

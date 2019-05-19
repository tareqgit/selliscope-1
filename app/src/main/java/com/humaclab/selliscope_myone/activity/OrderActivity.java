package com.humaclab.selliscope_myone.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.databinding.ActivityOrderBinding;
import com.humaclab.selliscope_myone.databinding.NewOrderBinding;
import com.humaclab.selliscope_myone.model.AddNewOrder;
import com.humaclab.selliscope_myone.model.Outlets;
import com.humaclab.selliscope_myone.model.VariantProduct.Brand;
import com.humaclab.selliscope_myone.model.VariantProduct.Category;
import com.humaclab.selliscope_myone.model.VariantProduct.ProductsItem;
import com.humaclab.selliscope_myone.model.promotion.PromotionQuantityResponse;
import com.humaclab.selliscope_myone.model.promotion.PromotionValueResponse;
import com.humaclab.selliscope_myone.productDialog.ProductDialogFragment;
import com.humaclab.selliscope_myone.utils.DatabaseHandler;
import com.humaclab.selliscope_myone.utils.SessionManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ScheduledExecutorService scheduler;

    private ActivityOrderBinding binding;
    private SelliscopeApiEndpointInterface apiService;

    private ProgressDialog pd;

    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private List<String> productName = new ArrayList<>(), productFlag = new ArrayList<>(), outletName = new ArrayList<>(), productID = new ArrayList<>(), outletID = new ArrayList<>(), categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>(), productDiscount = new ArrayList<>();
    private List<Double> productPrice = new ArrayList<>();

    private double variantPrice = 0;
    private int selectedPosition = 0, tableRowCount = 1;
    private double totalAmt = 0, totalDiscnt = 0, grandTotal = 0;
    private boolean outOfStock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(OrderActivity.this);

        pd = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.order));
        setSupportActionBar(toolbar);

        binding.btnIncrease.setOnClickListener(this);
        binding.btnDecrease.setOnClickListener(this);

        getOutlets();
        getCategory();
        getBrand();
        getProducts();

        binding.btnOrder.setOnClickListener(this);

        //For calculate total discount, amount and grand total in 1 second interval
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        double totalAmount = 0;
                        double totalDiscount = 0;
                        for (int i = 0; i < binding.tblOrders.getChildCount(); i++) {
                            View view = binding.tblOrders.getChildAt(i);
                            if (view instanceof TableRow) {
                                TableRow row = (TableRow) view;
                                TextView tvTotalAmount = row.findViewById(R.id.tv_amount);
                                totalAmount += Double.parseDouble(tvTotalAmount.getText().toString());
                                EditText tvTotalDiscnt = row.findViewById(R.id.et_discount);
                                if (!tvTotalDiscnt.getText().toString().equals(""))
                                    totalDiscount += Double.parseDouble(tvTotalDiscnt.getText().toString());
                            }
                        }
                        totalAmt = totalAmount;
                        totalDiscnt = totalDiscount;
                        grandTotal = totalAmount - totalDiscount;
                        binding.tvTotalAmt.setText(String.valueOf(totalAmt));
                        binding.tvTotalDiscnt.setText(String.valueOf(totalDiscnt));
                        binding.tvTotalGr.setText(String.valueOf(grandTotal));
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);
        //For calculate total discount, amount and grand total in 1 second interval
    }

    private void getCategory() {
        List<Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add("Select Category");
        categoryID.add(0);
        for (Category result : categories) {
            categoryName.add(result.getName());
            categoryID.add(Integer.valueOf(result.getId()));
        }
    }

    private void getBrand() {
        List<Brand> brands = databaseHandler.getBrand();
        brandName.clear();
        brandName.add("Select Brand");
        brandID.add(0);
        for (Brand result : brands) {
            brandName.add(result.getName());
            brandID.add(Integer.valueOf(result.getId()));
        }
    }

    private void getProducts() {
        List<ProductsItem> productsItemList = databaseHandler.getProduct(0, 0);
        for (ProductsItem result : productsItemList) {
            productName.add(result.getName());
            productID.add(result.getId());
            productDiscount.add(result.getDiscount());
            productPrice.add(Double.parseDouble(result.getPrice().replace(",", "")));
        }
        binding.spProduct.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
        //if this activity called from product activity
        if (getIntent().hasExtra("productID")) {
            addFirstProduct(getIntent().getStringExtra("productID"), false);
            tableRowCount++;
        } else {
            showProductSelectionDialog();
        }
        //if this activity called from product activity
    }

    private void getOutlets() {
        Outlets.Successful.OutletsResult outletsResult = databaseHandler.getAllOutlet();
        for (Outlets.Successful.Outlet outlet : outletsResult.outlets) {
            outletName.add(outlet.outletName);
            outletID.add(outlet.outletId);
        }
        binding.spOutlet.setAdapter(new ArrayAdapter<>(getApplication(), R.layout.spinner_item, outletName));

        //if this activity called from outlet activity
        if (getIntent().hasExtra("outletID")) {
            int position = outletID.indexOf(getIntent().getStringExtra("outletID"));
            binding.spOutlet.setSelection(position);
        }
        //if this activity called from outlet activity
    }

    @Override
    public void onClick(View v) {
        int qty = Integer.parseInt(binding.etQty.getText().toString());
        switch (v.getId()) {
            case R.id.btn_increase:
                try {
                    binding.etQty.setText(String.valueOf(qty + 1));
                    qty = qty + 1;
                    binding.tvAmount.setText(String.valueOf(qty * productPrice.get(selectedPosition)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_decrease:
                try {
                    if (qty > 1) {
                        binding.etQty.setText(String.valueOf(qty - 1));
                        qty = qty - 1;
                        binding.tvAmount.setText(String.valueOf(qty * productPrice.get(selectedPosition)));
                    } else {
                        binding.etQty.setText("1");
                        qty = 1;
                        binding.tvAmount.setText(String.valueOf(qty * productPrice.get(selectedPosition)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_order:
                pd.setMessage("Creating order....");
                pd.setCancelable(false);
                pd.show();

                try {
                    AddNewOrder addNewOrder = new AddNewOrder();
                    AddNewOrder.NewOrder newOrder = new AddNewOrder.NewOrder();
                    List<AddNewOrder.NewOrder.Product> products = new ArrayList<>();
                    newOrder.outletId = outletID.get(binding.spOutlet.getSelectedItemPosition());

                    for (int i = 1; i < binding.tblOrders.getChildCount(); i++) {
                        View view = binding.tblOrders.getChildAt(i);
                        if (view instanceof TableRow) {
                            AddNewOrder.NewOrder.Product product = new AddNewOrder.NewOrder.Product();
                            TableRow row = (TableRow) view;
                            Spinner sp = row.findViewById(R.id.sp_product);
                            EditText etQty = row.findViewById(R.id.et_qty);
                            EditText etDiscount = row.findViewById(R.id.et_discount);

                            product.id = productID.get(sp.getSelectedItemPosition());
                            if (etDiscount.getText().toString().equals("")) {
                                product.discount = 0.00;
                            } else {
                                product.discount = Double.parseDouble(etDiscount.getText().toString());
                            }
                            product.qty = Integer.parseInt(etQty.getText().toString());
                            product.price = String.valueOf(productPrice.get(sp.getSelectedItemPosition()));
                            product.flag = productFlag.get(i - 1);
                            products.add(product);
                        }
                    }

                    newOrder.products = products;
                    addNewOrder.newOrder = newOrder;

                    apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                            sessionManager.getUserPassword(), false)
                            .create(SelliscopeApiEndpointInterface.class);
                    System.out.println(new Gson().toJson(addNewOrder));

                    Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
                    call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                        @Override
                        public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                            pd.dismiss();
                            if (response.code() == 201) {
//                                System.out.println(new Gson().toJson(response.body()));
                                Toast.makeText(OrderActivity.this, "Order created successfully", Toast.LENGTH_LONG).show();

                               finish();
                               startActivity(new Intent(OrderActivity.this, OutletActivity.class));
                            } else if (response.code() == 401) {
//                                System.out.println(new Gson().toJson(response.body()));
                                Toast.makeText(OrderActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                            } else {
//                                System.out.println(new Gson().toJson(response.body()));
                                Toast.makeText(OrderActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                            pd.dismiss();
                            t.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_order) {
            showProductSelectionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProductSelectionDialog() {
        outOfStock = false;




        final boolean[] isVariant = {false};
        final AlertDialog builder = new AlertDialog.Builder(this, R.style.Theme_Design_Light).create();
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.product_selection_dialog, null);
        builder.setView(dialogView);

        ImageView civ_cancel = dialogView.findViewById(R.id.civ_cancel);
        civ_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        final Spinner sp_product_category = dialogView.findViewById(R.id.sp_product_category);
        final Spinner sp_product_brand = dialogView.findViewById(R.id.sp_product_brand);
        final SearchableSpinner sp_product_name = dialogView.findViewById(R.id.sp_product_name);
        sp_product_name.setTitle("Select Product");
        sp_product_name.setPositiveButton("OK");
        final TextView tv_simple_product_stock = dialogView.findViewById(R.id.tv_simple_product_stock);
        final Button btn_select_product = dialogView.findViewById(R.id.btn_select_product);

        final List<String> productName = new ArrayList<>();
        final List<String> productID = new ArrayList<>();

        //For category
        sp_product_category.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categoryName));
        sp_product_category.setSelection(0);
        sp_product_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(position), brandID.get(sp_product_brand.getSelectedItemPosition()));
                    productName.clear();
                    productName.add("Select Product");
                    productID.clear();
                    productID.add("0");
                    for (ProductsItem result : productsItemList) {
                        productName.add(result.getName());
                        productID.add(result.getId());
                    }
                    sp_product_name.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //For category

        //For brand
        sp_product_brand.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, brandName));
        sp_product_brand.setSelection(0);
        sp_product_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(categoryID.get(sp_product_category.getSelectedItemPosition()), brandID.get(position));
                    productName.clear();
                    productName.add("Select Product");
                    productID.clear();
                    productID.add("0");
                    for (ProductsItem result : productsItemList) {
                        productName.add(result.getName());
                        productID.add(result.getId());
                    }
                    sp_product_name.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //For brand

        //For product selection in variant product
        sp_product_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (databaseHandler.isVariantExists()) {
                        tv_simple_product_stock.setVisibility(View.VISIBLE);
                        tv_simple_product_stock.setText(getString(R.string.stock, databaseHandler.getProductStock(productID.get(sp_product_name.getSelectedItemPosition()))));
                        isVariant[0] = false;
                        List<String> list = new ArrayList<>();
                        list.add("0");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //For product selection in variant product

        btn_select_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outOfStock) {
                    Toast.makeText(v.getContext(), "Due to out of stock this product cannot be to added to product list.", Toast.LENGTH_LONG).show();
                } else {
                    addProduct(productID.get(sp_product_name.getSelectedItemPosition()), isVariant[0], false, null);
                    builder.dismiss();
                }
            }
        });

       // builder.show(); //by tarerq
        // *tareq*//*
        List<ProductsItem> productsItemList=new ArrayList<>();
        try {
         productsItemList = databaseHandler.getProduct(0, 0);
            productName.clear();

            productID.clear();

            for (ProductsItem result : productsItemList) {
                productName.add(result.getName());
                productID.add(result.getId());
            }
        //    sp_product_name.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final FragmentManager fm=getSupportFragmentManager();
      //  Toast.makeText(this, ""+ productsItemList.size(), Toast.LENGTH_SHORT).show();
      //  ProductDialogFragment.products.addAll(productsItemList);

        final ProductDialogFragment productDialogFragment = new ProductDialogFragment();
        productDialogFragment.show(fm,"Product_Tag");
    }

    private void addFirstProduct(final String productId, final boolean fromVariant) {
        binding.spProduct.setSelection(productID.indexOf(productId));
        binding.spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                productFlag.add("orderQty");
                if (fromVariant) {
                    binding.tvPrice.setText(String.valueOf(variantPrice));
                    binding.tvAmount.setText(String.valueOf(variantPrice));
                    productPrice.remove(position);
                    productPrice.add(position, variantPrice);
                } else {
                    binding.tvPrice.setText(String.valueOf(productPrice.get(position)));
                    binding.tvAmount.setText(String.valueOf(productPrice.get(position)));
                }
                binding.etDiscount.setText(String.valueOf(productDiscount.get(position)));
                binding.etQty.setText("1");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    binding.tvAmount.setText(String.valueOf(Integer.parseInt(s.toString()) * productPrice.get(selectedPosition)));

                    //For product promotion
                    String outletId = outletID.get(binding.spOutlet.getSelectedItemPosition());
                    String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    Map<String, Object> promotion = databaseHandler.getPromotion(outletId, productId, todayDate, String.format("%.3f", Double.parseDouble(s.toString())));
                    if (promotion.get("hasPromotion").equals(true)) {
                        if (promotion.get("promotionType").equals("quantity")) {
                            List<PromotionQuantityResponse.PromotionQuantityItem> promotionQuantityItems = (List<PromotionQuantityResponse.PromotionQuantityItem>) promotion.get("quantity");
                            for (PromotionQuantityResponse.PromotionQuantityItem promotionQuantityItem : promotionQuantityItems) {
                                addProduct(promotionQuantityItem.getFreeProductId(), false, true, promotionQuantityItem);
                            }
                        } else {
                            List<PromotionValueResponse.PromotionValueItem> promotionValueItems = (List<PromotionValueResponse.PromotionValueItem>) promotion.get("value");
                            if (promotionValueItems.get(0).getDiscountType().equals("Flat")) {
                                binding.etDiscount.setText(String.valueOf(
                                        Double.valueOf(binding.etDiscount.getText().toString())
                                                +
                                                (Double.valueOf(s.toString()) / Double.valueOf(promotionValueItems.get(0).getSoldQuantity())) * Double.valueOf(promotionValueItems.get(0).getDiscountAmount())
                                ));
                            } else {
                                binding.etDiscount.setText(
                                        Integer.valueOf(binding.etDiscount.getText().toString())
                                                + (Integer.valueOf(binding.tvAmount.getText().toString()) * (Integer.valueOf(promotionValueItems.get(0).getDiscountAmount()) / 100))
                                );
                            }
                        }
                    }
                    //For product promotion
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void addProduct(final String productId, final boolean fromVariant, final boolean isFreeProduct, final PromotionQuantityResponse.PromotionQuantityItem promotionQuantityItem) {
        if (tableRowCount == 1) {
            addFirstProduct(productId, fromVariant);
            tableRowCount++;
        } else {
            tableRowCount++;

            final int[] qty = {1}, selectedPosition = {0};
            final LayoutInflater inflater = (LayoutInflater) OrderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final NewOrderBinding newOrder = DataBindingUtil.inflate(inflater, R.layout.new_order, null, true);
            newOrder.spProduct.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
            newOrder.spProduct.setSelection(productID.indexOf(productId));
            newOrder.spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedPosition[0] = position;
                    if (fromVariant) {
                        newOrder.tvPrice.setText(String.valueOf(variantPrice));
                        newOrder.tvAmount.setText(String.valueOf(variantPrice));
                        productPrice.remove(position);
                        productPrice.add(position, variantPrice);
                    } else {
                        newOrder.tvPrice.setText(String.valueOf(productPrice.get(position)));
                        newOrder.tvAmount.setText(String.valueOf(productPrice.get(position)));
                    }
                    if (isFreeProduct) {
                        productFlag.add("offerQty");
                        newOrder.tvPrice.setText("0");
                        newOrder.etDiscount.setText(String.valueOf(productPrice.get(position)));
                        try {
                            newOrder.etQty.setText(String.valueOf(Integer.parseInt(String.format("%.0f", Double.parseDouble(promotionQuantityItem.getFreeQuantity())))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        productFlag.add("orderQty");
                    }
                    newOrder.etDiscount.setText(String.valueOf(Double.valueOf(newOrder.etDiscount.getText().toString()) + productDiscount.get(position)));
                    newOrder.tvAmount.setText(String.valueOf(productPrice.get(position)));
//                    newOrder.etQty.setText("1");
                    qty[0] = 1;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            newOrder.etQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        qty[0] = Integer.parseInt(s.toString());
                        newOrder.tvAmount.setText(String.valueOf(Integer.parseInt(s.toString()) * productPrice.get(selectedPosition[0])));

                        //For product promotion
                        String outletId = outletID.get(binding.spOutlet.getSelectedItemPosition());
                        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        Map<String, Object> promotion = databaseHandler.getPromotion(outletId, productId, todayDate, String.format("%.3f", Double.parseDouble(s.toString())));
                        if (promotion.get("hasPromotion").equals(true)) {
                            if (promotion.get("promotionType").equals("quantity")) {
                                List<PromotionQuantityResponse.PromotionQuantityItem> promotionQuantityItems = (List<PromotionQuantityResponse.PromotionQuantityItem>) promotion.get("quantity");
                                for (PromotionQuantityResponse.PromotionQuantityItem promotionQuantityItem : promotionQuantityItems) {
                                    addProduct(promotionQuantityItem.getFreeProductId(), false, true, promotionQuantityItem);
                                }
                            } else {
                                List<PromotionValueResponse.PromotionValueItem> promotionValueItems = (List<PromotionValueResponse.PromotionValueItem>) promotion.get("value");
                                if (promotionValueItems.get(0).getDiscountType().equals("Flat")) {
                                    newOrder.etDiscount.setText(String.valueOf(
                                            Double.valueOf(newOrder.etDiscount.getText().toString())
                                                    +
                                                    Double.valueOf(promotionValueItems.get(0).getDiscountAmount())
                                    ));
                                } else {
                                    newOrder.etDiscount.setText(String.valueOf(
                                            Integer.valueOf(newOrder.etDiscount.getText().toString())
                                                    + (Integer.valueOf(newOrder.tvAmount.getText().toString()) * (Integer.valueOf(promotionValueItems.get(0).getDiscountAmount()) / 100))
                                    ));
                                }
                            }
                        }
                        //For product promotion
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            newOrder.btnIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qty[0] += 1;
                    newOrder.etQty.setText(String.valueOf(qty[0]));
                    newOrder.tvAmount.setText(String.valueOf(qty[0] * productPrice.get(selectedPosition[0])));
                }
            });
            newOrder.btnDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty[0] > 1) {
                        qty[0] -= 1;
                        newOrder.etQty.setText(String.valueOf(qty[0]));
                        newOrder.tvAmount.setText(String.valueOf(qty[0] * productPrice.get(selectedPosition[0])));
                    } else {
                        qty[0] = 1;
                        newOrder.etQty.setText(String.valueOf(qty[0]));
                        newOrder.tvAmount.setText(String.valueOf(qty[0] * productPrice.get(selectedPosition[0])));
                    }
                }
            });
            try {
                newOrder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tableRowCount--;
                        binding.tblOrders.removeView(newOrder.getRoot());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.tblOrders.addView(newOrder.getRoot());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void cancelOrder(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduler.shutdown();
    }
}

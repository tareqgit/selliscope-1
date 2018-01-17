package com.humaclab.selliscope_mohammadi.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_mohammadi.SelliscopeApplication;
import com.humaclab.selliscope_mohammadi.databinding.ActivityOrderBinding;
import com.humaclab.selliscope_mohammadi.databinding.NewOrderBinding;
import com.humaclab.selliscope_mohammadi.model.AddNewOrder;
import com.humaclab.selliscope_mohammadi.model.Outlets;
import com.humaclab.selliscope_mohammadi.model.VariantProduct.ProductsItem;
import com.humaclab.selliscope_mohammadi.model.VariantProduct.VariantItem;
import com.humaclab.selliscope_mohammadi.utils.DatabaseHandler;
import com.humaclab.selliscope_mohammadi.utils.NetworkUtility;
import com.humaclab.selliscope_mohammadi.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
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
    private List<String> productName = new ArrayList<>(), outletName = new ArrayList<>(), outletCreditBalance = new ArrayList<>();
    private List<Integer> productID = new ArrayList<>(), productDiscount = new ArrayList<>(), outletID = new ArrayList<>();
    private List<Double> productPrice = new ArrayList<>();
    private List<View> variantViews = new ArrayList<>();
    private List<List<String>> variantNameList = new ArrayList<>();
    private List<List<Integer>> variantIDList = new ArrayList<>();
    private List<List<String>> variantRows = new ArrayList<>();

    private double variantPrice = 0;
    private String price, quantity;
    private int selectedPosition = 0, tableRowCount = 1;
    private double totalAmt = 0, totalDiscnt = 0, grandTotal = 0;
    private boolean outOfStock = false;
    private int selectedOutletPosition;

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

        getOutlets();
        getProducts();

        binding.spOutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.tvBgAmount.setText(outletCreditBalance.get(position));
                selectedOutletPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btnOrder.setOnClickListener(this);

        //For calculate total discount, amount and grand total in 1 second interval
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        double totalAmount = 0;
                        int totalQty = 0;
                        for (int i = 1; i < binding.tblOrders.getChildCount(); i++) {
                            View view = binding.tblOrders.getChildAt(i);
                            if (view instanceof TableRow) {
                                TableRow row = (TableRow) view;
                                TextView tvTotalAmount = row.findViewById(R.id.tv_price);
                                EditText etQty = row.findViewById(R.id.et_qty);
                                totalAmount += Double.parseDouble(tvTotalAmount.getText().toString());
                                totalQty += Integer.parseInt(etQty.getText().toString());
                            }
                        }
                        try {
                            totalAmt = totalAmount;
                            totalDiscnt = Double.parseDouble(binding.etDiscount.getText().toString());
                            grandTotal = (totalAmount + Double.parseDouble(binding.etTruckFare.getText().toString())) - totalDiscnt;
                            binding.tvQty.setText(String.valueOf(totalQty));
                            binding.tvTotal.setText(String.valueOf(totalAmt));
                            binding.tvGrandTotal.setText(String.valueOf(grandTotal));
                            binding.tvBgAmount.setText(String.valueOf(Double.parseDouble(outletCreditBalance.get(selectedOutletPosition)) - grandTotal));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);
        //For calculate total discount, amount and grand total in 1 second interval
    }

    private void getProducts() {
        List<ProductsItem> productsItemList = databaseHandler.getProduct(0, 0);
        for (ProductsItem result : productsItemList) {
            productName.add(result.getName());
            productID.add(result.getId());
            productDiscount.add(result.getDiscount());
            productPrice.add(Double.parseDouble(result.getPrice().replace(",", "")));
        }

        showProductSelectionDialog();
        //if this activity called from product activity
    }

    private void getOutlets() {
        Outlets.Successful.OutletsResult outletsResult = databaseHandler.getAllOutlet();
        for (Outlets.Successful.Outlet outlet : outletsResult.outlets) {
            outletName.add(outlet.outletName);
            outletCreditBalance.add(String.valueOf(outlet.outletCreditBalance));
            outletID.add(outlet.outletId);
        }
        binding.spOutlet.setAdapter(new ArrayAdapter<>(getApplication(), R.layout.spinner_item, outletName));

        //if this activity called from outlet activity
        if (getIntent().hasExtra("outletID")) {
            int position = outletID.indexOf(getIntent().getIntExtra("outletID", 0));
            binding.spOutlet.setSelection(position);
        }
        //if this activity called from outlet activity
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                            TextView tv_rate = row.findViewById(R.id.tv_rate);

                            product.id = productID.get(sp.getSelectedItemPosition());
                            product.discount = 0.00;
                            product.qty = Integer.parseInt(etQty.getText().toString());
                            product.row = (variantRows.size() != 0) ? Integer.parseInt(variantRows.get(i).get(0)) : 0;
                            product.price = tv_rate.getText().toString();
                            products.add(product);
                        }
                    }

                    newOrder.additionalCharge = Double.valueOf(binding.etTruckFare.getText().toString());
                    if (binding.rbUBend.isChecked()) {
                        newOrder.bend = 1;
                    } else {
                        newOrder.bend = 2;
                    }
                    if (binding.rbCap.isChecked()) {
                        newOrder.cap = 1;
                    } else {
                        newOrder.cap = 2;
                    }
                    newOrder.discount = Double.parseDouble(binding.etDiscount.getText().toString());
                    newOrder.remarks = binding.etRemarks.getText().toString();
                    newOrder.deliveryAddress = binding.etDeliveryAddress.getText().toString();
                    newOrder.products = products;
                    addNewOrder.newOrder = newOrder;

                    apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                            sessionManager.getUserPassword(), false)
                            .create(SelliscopeApiEndpointInterface.class);

                    System.out.println("Order: " + new Gson().toJson(addNewOrder));

                    if (NetworkUtility.isNetworkAvailable(OrderActivity.this)) {
                        Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
                        call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                            @Override
                            public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                                pd.dismiss();
                                System.out.println(new Gson().toJson(response.body()));
                                if (response.code() == 201) {
                                    System.out.println(new Gson().toJson(response.body()));
                                    Toast.makeText(OrderActivity.this, "Order created successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(OrderActivity.this, PaymentActivity.class));
                                    finish();
                                } else if (response.code() == 401) {
                                    System.out.println(new Gson().toJson(response.body()));
                                    Toast.makeText(OrderActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                                } else {
                                    System.out.println(new Gson().toJson(response.body()));
                                    Toast.makeText(OrderActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                                pd.dismiss();
                                t.printStackTrace();
                            }
                        });
                    } else {
                        databaseHandler.setOrder(addNewOrder);
                        Toast.makeText(OrderActivity.this, "Order created successfully", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        finish();
                    }
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
        for (int i = 0; i < 50; i++) {
            variantRows.add(new ArrayList<String>());
        }

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

        final LinearLayout ll_spinner_layout = dialogView.findViewById(R.id.ll_spinner_layout);
        final Spinner sp_product_name = dialogView.findViewById(R.id.sp_product_name);
        final TextView tv_simple_product_stock = dialogView.findViewById(R.id.tv_simple_product_stock);
        final EditText et_quantity = dialogView.findViewById(R.id.et_quantity);
        final EditText et_rate = dialogView.findViewById(R.id.et_rate);
        final TextView tv_total = dialogView.findViewById(R.id.tv_total);
        final Button btn_select_product = dialogView.findViewById(R.id.btn_select_product);

        final List<String> productName = new ArrayList<>();
        final List<Integer> productID = new ArrayList<>();

        //Load product
        List<ProductsItem> productsItemList = databaseHandler.getProduct(0, 0);
        productName.clear();
        productName.add("Select Product");
        productID.clear();
        productID.add(0);
        for (ProductsItem result : productsItemList) {
            productName.add(result.getName());
            productID.add(result.getId());
        }
        sp_product_name.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
        sp_product_name.setSelection(1);
        sp_product_name.setSelected(true);
        //Load product

        //For product selection in variant product
        sp_product_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (productID.size() <= 2)
                    sp_product_name.setVisibility(View.GONE);
                if (position != 0) {
                    if (databaseHandler.isVariantExists()) {
                        if (databaseHandler.isThereAnyVariantForProduct(productID.get(position))) {
                            tv_simple_product_stock.setVisibility(View.GONE);
                            variantViews.clear();
                            variantIDList.clear();
                            variantNameList.clear();
                            ll_spinner_layout.removeAllViews();
                            addVariantSpinner(dialogView.getContext());
                            isVariant[0] = true;
                        } else {
                            tv_simple_product_stock.setVisibility(View.VISIBLE);
                            tv_simple_product_stock.setText(getString(R.string.stock, databaseHandler.getProductStock(productID.get(sp_product_name.getSelectedItemPosition()))));
                            variantViews.clear();
                            variantIDList.clear();
                            variantNameList.clear();
                            ll_spinner_layout.removeAllViews();
                            isVariant[0] = false;
                            List<String> list = new ArrayList<>();
                            list.add("0");
                            variantRows.add(tableRowCount, list);
                        }
                    }
                }
            }

            private void addVariantSpinner(final Context context) {
                final List<VariantItem> variantsItems = databaseHandler.getVariantCategories();
                final List<Integer> price_id = new ArrayList<>();
                for (int i = 0; i < variantsItems.size(); i++) {
                    if (!variantsItems.get(i).getType().toLowerCase().equals("input")) {
                        Spinner spinner = new Spinner(context);
                        final ArrayAdapter<String> arrayAdapter;
                        spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                        List<String> variantNames = new ArrayList<>();
                        List<Integer> variantNameIDs = new ArrayList<>();
                        final String variantName = "Select " + variantsItems.get(i).getName();
                        int variantID = variantsItems.get(i).getId();
                        variantNames.add(variantName);
                        variantNameIDs.add(variantID);

                        for (String s : databaseHandler.getVariants(variantsItems.get(i).getId(), productID.get(sp_product_name.getSelectedItemPosition()))) {
                            variantNames.add(s);
                        }
                        arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, variantNames);
                        spinner.setAdapter(arrayAdapter);
                        final int finalI; //This variable is uses to track the positions of variantID and variantName list items.
                        if (i != variantsItems.size() - 1) {
                            finalI = i + 1; //if i is less then variant item size - 1 then it will execute
                        } else {
                            finalI = i;
                        }
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    List<String> variantDetailsNames = new ArrayList<>();
                                    variantDetailsNames.add(variantNameList.get(finalI).get(0));

                                    if (finalI == 1) {
                                        variantRows.add(
                                                tableRowCount,
                                                databaseHandler.getVariantRows(
                                                        productID.get(sp_product_name.getSelectedItemPosition()),
                                                        variantIDList.get(finalI - 1).get(0),
                                                        variantNameList.get(finalI - 1).get(position)
                                                )
                                        );
                                    } else {
                                        variantRows.add(
                                                tableRowCount,
                                                databaseHandler.getVariantRows(
                                                        productID.get(sp_product_name.getSelectedItemPosition()),
                                                        variantIDList.get(finalI - 1).get(0),
                                                        variantNameList.get(finalI - 1).get(position),
                                                        variantRows.get(tableRowCount)
                                                )
                                        );
                                    }
                                    for (String s : databaseHandler.getAssociatedVariants(
                                            productID.get(sp_product_name.getSelectedItemPosition()),
                                            variantIDList.get(finalI).get(0),
                                            variantNameList.get(finalI - 1).get(position),
                                            variantRows.get(tableRowCount))
                                            ) {
                                        variantDetailsNames.add(s);
                                    }
                                    variantNameList.remove(finalI);
                                    variantNameList.add(finalI, variantDetailsNames);
                                    ((Spinner) variantViews.get(finalI)).setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, variantNameList.get(finalI)));
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        variantViews.add(spinner);
                        variantIDList.add(variantNameIDs);
                        variantNameList.add(variantNames);
                    } else {
                        price_id.add(i);
                    }
                }

                //For rate
                et_rate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            tv_total.setText(String.valueOf(Double.parseDouble(s.toString()) * Double.parseDouble(et_quantity.getText().toString())));
                            price = s.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                //For rate

                //For quantity
                et_quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            tv_total.setText(String.valueOf(Double.parseDouble(s.toString()) * Double.parseDouble(et_rate.getText().toString())));
                            quantity = s.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                //For quantity

                //For loading price dropdown
                final List<String> variantPriceTypes = new ArrayList<>();
                final List<Integer> variantIDs = new ArrayList<>();

                //For product stock
                final TextView productStock = new TextView(context);
                productStock.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                productStock.setTextSize(20);
                //For product stock
                variantPriceTypes.add("Select price");
                for (Integer integer : price_id) {
                    variantIDs.add(variantsItems.get(integer).getId());
                    variantPriceTypes.add(variantsItems.get(integer).getName());
                    variantNameList.add(variantPriceTypes);
                    variantIDList.add(variantIDs);
                }

                final ArrayAdapter<String> arrayAdapter;
                Spinner spinner = new Spinner(context);
                spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, variantPriceTypes);
                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            for (String s : databaseHandler.getAssociatedVariants(
                                    productID.get(sp_product_name.getSelectedItemPosition()),
                                    variantIDs.get(position - 1),
                                    "Price",//this is not used in query. its optional
                                    variantRows.get(tableRowCount))) {
                                et_rate.setText(s);
                                price = s;
                                variantPrice = Double.parseDouble(s);
                            }
                            //For product stock
                            String stock = databaseHandler.getVariantProductStock(productID.get(sp_product_name.getSelectedItemPosition()), variantRows.get(tableRowCount).get(0));
                            if (stock.equals("0")) {
                                productStock.setTextColor(Color.RED);
                                productStock.setText("Out of Stock");
                                outOfStock = true;
                            } else {
                                productStock.setTextColor(Color.GREEN);
                                productStock.setText("Stock: " + stock);
                                outOfStock = false;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                variantViews.add(spinner);
                //For loading price dropdown

                variantViews.add(productStock);
                for (View view : variantViews)
                    ll_spinner_layout.addView(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //For product selection in variant product

        btn_select_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> productVariantList = new ArrayList<>();
  /*  if (outOfStock) {
                  Toast.makeText(v.getContext(), "Due to out of stock this product cannot be to added to product list.", Toast.LENGTH_LONG).show();
                } else {*/
                for (int i = 0; i < variantViews.size(); i++) {
                    View view = variantViews.get(i);
                    if (view instanceof Spinner) {
                        productVariantList.add(variantNameList.get(i).get(((Spinner) view).getSelectedItemPosition()));
                    }
                }
                System.out.println("Selected variants: " + productVariantList);
                if (!et_quantity.getText().toString().isEmpty()) {
                    addProduct(productVariantList, price, quantity, productID.get(sp_product_name.getSelectedItemPosition()), isVariant[0]);
                    builder.dismiss();
                } else {
                    View view = et_quantity;
                    et_quantity.setError("This field is required.");
                    view.requestFocus();
                }
            }
//            }
        });

        builder.show();
    }

    private void addProduct(List<String> productVariantList, final String rate, String quantity, Integer productId, final boolean fromVariant) {
        try {
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
                        productPrice.remove(position);
                        productPrice.add(position, variantPrice);
                    } else {
                        newOrder.tvPrice.setText(String.valueOf(productPrice.get(position)));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            newOrder.spProduct.setSelection(productID.indexOf(productId));

            newOrder.tvDia.setText(productVariantList.get(0));
            newOrder.tvGrade.setText(productVariantList.get(1));
            newOrder.tvRate.setText(rate);
            newOrder.tvPrice.setText(String.valueOf(Double.parseDouble(rate) * Double.parseDouble(quantity)));
            newOrder.etQty.setText(quantity);
            newOrder.etQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        newOrder.tvPrice.setText(String.valueOf(Double.parseDouble(rate) * Double.parseDouble(s.toString())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            newOrder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tableRowCount--;
                    binding.tblOrders.removeView(newOrder.getRoot());
                }
            });
            binding.tblOrders.addView(newOrder.getRoot());
            tableRowCount++;
        } catch (Exception e) {
            e.printStackTrace();
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

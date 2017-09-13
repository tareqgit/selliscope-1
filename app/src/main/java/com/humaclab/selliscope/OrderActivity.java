package com.humaclab.selliscope;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.Utils.DatabaseHandler;
import com.humaclab.selliscope.Utils.NetworkUtility;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.databinding.ActivityOrderBinding;
import com.humaclab.selliscope.databinding.NewOrderBinding;
import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.ProductResponse;
import com.humaclab.selliscope.model.VariantProduct.VariantItem;

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
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();
    private List<String> productName = new ArrayList<>(), outletName = new ArrayList<>();
    private List<Integer> productID = new ArrayList<>(), productDiscount = new ArrayList<>(), outletID = new ArrayList<>();
    private List<String> categoryName = new ArrayList<>(), brandName = new ArrayList<>();
    private List<Integer> categoryID = new ArrayList<>(), brandID = new ArrayList<>();
    private List<Double> productPrice = new ArrayList<>();
    private List<View> variantViews = new ArrayList<>();
    private List<List<String>> variantNameDetailsList = new ArrayList<>();
    private List<List<Integer>> variantIDDetailsList = new ArrayList<>();

    private int selectedPosition = 0, tableRowCount = 2;
    private double totalAmt = 0, totalDiscnt = 0, grandTotal = 0;
    private int firstCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(OrderActivity.this);

        pd = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.order));
        setSupportActionBar(toolbar);

        binding.btnIncrease.setOnClickListener(this);
        binding.btnDecrease.setOnClickListener(this);

        binding.etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    binding.tvAmount.setText(String.valueOf(Integer.parseInt(s.toString()) * productPrice.get(selectedPosition)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (NetworkUtility.isNetworkAvailable(this)) {
            getOutlets();
            getCategory();
            getBrand();
            getProducts();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data",
                    Toast.LENGTH_SHORT).show();
        }

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
                        for (int i = 1; i < binding.tblOrders.getChildCount(); i++) {
                            View view = binding.tblOrders.getChildAt(i);
                            if (view instanceof TableRow) {
                                TableRow row = (TableRow) view;
                                TextView tvTotalAmount = (TextView) row.findViewById(R.id.tv_amount);
                                totalAmount += Double.parseDouble(tvTotalAmount.getText().toString());
                                EditText tvTotalDiscnt = (EditText) row.findViewById(R.id.et_discount);
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
        List<ProductResponse.Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add("Select Category");
        categoryID.add(0);
        for (ProductResponse.Category result : categories) {
            categoryName.add(result.name);
            categoryID.add(result.id);
        }
    }

    private void getBrand() {
        List<ProductResponse.Brand> brands = databaseHandler.getBrand();
        brandName.clear();
        brandName.add("Select Brand");
        brandID.add(0);
        for (ProductResponse.Brand result : brands) {
            brandName.add(result.name);
            brandID.add(result.id);
        }
    }

    private void getProducts() {
        List<ProductResponse.ProductResult> products = databaseHandler.getProduct(0, 0);
        for (ProductResponse.ProductResult result : products) {
            productName.add(result.name);
            productID.add(result.id);
            productDiscount.add(result.discount);
            productPrice.add(Double.parseDouble(result.price.replace(",", "")));
        }
        binding.spProduct.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
        //if this activity called from product activity
        if (getIntent().hasExtra("productID")) {
            addFirstProduct(getIntent().getIntExtra("productID", 0));
            firstCount++;
        } else {
            showProductSelectionDialog();
        }
        //if this activity called from product activity

        /*
        SessionManager sessionManager = new SessionManager(OrderActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ProductResponse> call = apiService.getProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.code() == 200) {
                    try {
                        List<ProductResponse.ProductResult> products = response.body().result.productResults;
                        for (ProductResponse.ProductResult result : products) {
                            productName.add(result.name);
                            productID.add(result.id);
                            productDiscount.add(result.discount);
                            productPrice.add(Double.parseDouble(result.price.replace(",", "")));
                        }
                        binding.spProduct.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(OrderActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });*/
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
            int position = outletID.indexOf(getIntent().getIntExtra("outletID", 0));
            binding.spOutlet.setSelection(position);
        }
        //if this activity called from outlet activity

        /* SessionManager sessionManager = new SessionManager(OrderActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getOutlets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        Outlets.Successful getOutletListSuccessful = gson.fromJson(response.body()
                                .string(), Outlets.Successful.class);
                        for (Outlets.Successful.Outlet outlet : getOutletListSuccessful.outletsResult.outlets) {
                            outletName.add(outlet.outletName);
                            outletID.add(outlet.outletId);
                        }
                        binding.spOutlet.setAdapter(new ArrayAdapter<>(getApplication(), R.layout.spinner_item, outletName));

                        //if this activity called from outlet activity
                        if (getIntent().hasExtra("outletID")) {
                            int position = outletID.indexOf(getIntent().getIntExtra("outletID", 0));
                            binding.spOutlet.setSelection(position);
                        }
                        //if this activity called from outlet activity
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(OrderActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());

            }
        });*/
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
                            Spinner sp = (Spinner) row.findViewById(R.id.sp_product);
                            EditText etQty = (EditText) row.findViewById(R.id.et_qty);
                            EditText etDiscount = (EditText) row.findViewById(R.id.et_discount);

                            product.id = productID.get(sp.getSelectedItemPosition());
                            product.discount = Integer.parseInt(etDiscount.getText().toString());
                            product.qty = Integer.parseInt(etQty.getText().toString());
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
                                System.out.println(new Gson().toJson(response.body()));
                                Toast.makeText(OrderActivity.this, "Order created successfully", Toast.LENGTH_LONG).show();
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
        final AlertDialog builder = new AlertDialog.Builder(this, R.style.Theme_Design_Light).create();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.product_selection_dialog, null);
        builder.setView(dialogView);

        ImageView civ_cancel = (ImageView) dialogView.findViewById(R.id.civ_cancel);
        civ_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        final LinearLayout ll_spinner_layout = (LinearLayout) dialogView.findViewById(R.id.ll_spinner_layout);
        final Spinner sp_product_category = (Spinner) dialogView.findViewById(R.id.sp_product_category);
        final Spinner sp_product_brand = (Spinner) dialogView.findViewById(R.id.sp_product_brand);
        final Spinner sp_product_name = (Spinner) dialogView.findViewById(R.id.sp_product_name);
        final Button btn_select_product = (Button) dialogView.findViewById(R.id.btn_select_product);

        //For category
        List<ProductResponse.Category> categories = databaseHandler.getCategory();
        categoryName.clear();
        categoryName.add("Select Category");
        categoryID.add(0);
        for (ProductResponse.Category result : categories) {
            categoryName.add(result.name);
            categoryID.add(result.id);
        }
        sp_product_category.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categoryName));
        sp_product_category.setSelection(0);
        sp_product_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    List<ProductResponse.ProductResult> productResults = databaseHandler.getProduct(categoryID.get(position), brandID.get(sp_product_brand.getSelectedItemPosition()));
                    productName.clear();
                    productName.add("Select Product");
                    productID.clear();
                    productID.add(0);
                    productDiscount.clear();
                    productDiscount.add(0);
                    productPrice.clear();
                    productPrice.add((double) 0);
                    for (ProductResponse.ProductResult result : productResults) {
                        productName.add(result.name);
                        productID.add(result.id);
                        productDiscount.add(result.discount);
                        productPrice.add(Double.parseDouble(result.price.replace(",", "")));
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
        List<ProductResponse.Brand> brands = databaseHandler.getBrand();
        brandName.clear();
        brandName.add("Select Brand");
        brandID.add(0);
        for (ProductResponse.Brand result : brands) {
            brandName.add(result.name);
            brandID.add(result.id);
        }
        sp_product_brand.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, brandName));
        sp_product_brand.setSelection(0);
        sp_product_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    List<ProductResponse.ProductResult> productResults = databaseHandler.getProduct(categoryID.get(sp_product_category.getSelectedItemPosition()), brandID.get(position));
                    productName.clear();
                    productName.add("Select Product");
                    productID.clear();
                    productID.add(0);
                    productDiscount.clear();
                    productDiscount.add(0);
                    productPrice.clear();
                    productPrice.add((double) 0);
                    for (ProductResponse.ProductResult result : productResults) {
                        productName.add(result.name);
                        productID.add(result.id);
                        productDiscount.add(result.discount);
                        productPrice.add(Double.parseDouble(result.price.replace(",", "")));
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

        //For product selection
        sp_product_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (databaseHandler.isVariantExists()) {
                        variantViews.clear();
                        ll_spinner_layout.removeAllViews();
                        addVariantSpinner(view.getContext());
                    }
                }
            }

            private void addVariantSpinner(Context context) {
                List<VariantItem> variantsItems = databaseHandler.getVariantCategories();
                int price_id = 0;
                for (int i = 0; i < variantsItems.size(); i++) {
                    if (!variantsItems.get(i).getType().toLowerCase().equals("input")) {
                        Spinner spinner = new Spinner(context);
                        spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                        List<String> variantNames = new ArrayList<>();
                        List<Integer> variantNameIDs = new ArrayList<>();
                        String variantName = "Select " + variantsItems.get(i).getName();
                        int variantID = variantsItems.get(i).getId();
                        variantNames.add(variantName);
                        variantNameIDs.add(variantID);

                        for (String s : databaseHandler.getVariants(variantsItems.get(i).getId(), productID.get(sp_product_name.getSelectedItemPosition()))) {
                            variantNames.add(s);
                        }
                        spinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, variantNames));
                        variantViews.add(spinner);
                        variantNameDetailsList.add(variantNames);
                        variantIDDetailsList.add(variantNameIDs);
                    } else {
                        price_id = i;
                    }
                }
                if (price_id != 0) {
                    EditText editText = new EditText(context);
                    editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    editText.setInputType(InputType.TYPE_NULL);
                    editText.setText(variantsItems.get(price_id).getName());
                    variantViews.add(editText);
                }
                for (View view : variantViews)
                    ll_spinner_layout.addView(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //For product selection

        btn_select_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct(productID.get(sp_product_name.getSelectedItemPosition()));
                builder.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void addProduct(int productId) {
        final int[] qty = {1}, selectedPosition = {0};
        final LayoutInflater inflater = (LayoutInflater) OrderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NewOrderBinding newOrder = DataBindingUtil.inflate(inflater, R.layout.new_order, null, true);
        newOrder.spProduct.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
        newOrder.spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition[0] = position;
                newOrder.tvPrice.setText(String.valueOf(productPrice.get(position)));
                newOrder.tvAmount.setText(String.valueOf(productPrice.get(position)));
                newOrder.etDiscount.setText(String.valueOf(productDiscount.get(position)));
                newOrder.etQty.setText("1");
                qty[0] = 1;
            }
            showProductSelectionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProductSelectionDialog() {
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.product_selection_dialog, null);
        builder.setView(dialogView);

        ImageView civ_cancel = (ImageView) dialogView.findViewById(R.id.civ_cancel);
        civ_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        final Spinner sp_product_category = (Spinner) dialogView.findViewById(R.id.sp_product_category);
        final Spinner sp_product_brand = (Spinner) dialogView.findViewById(R.id.sp_product_brand);
        final Spinner sp_product_name = (Spinner) dialogView.findViewById(R.id.sp_product_name);
        final Button btn_select_product = (Button) dialogView.findViewById(R.id.btn_select_product);

        final List<String> productName = new ArrayList<>();
        final List<Integer> productID = new ArrayList<>();

        //For category
        sp_product_category.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, categoryName));
        sp_product_category.setSelection(0);
        sp_product_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    List<ProductResponse.ProductResult> productResults = databaseHandler.getProduct(categoryID.get(position), brandID.get(sp_product_brand.getSelectedItemPosition()));
                    productName.clear();
                    productName.add("Select Product");
                    productID.clear();
                    productID.add(0);
                    for (ProductResponse.ProductResult result : productResults) {
                        productName.add(result.name);
                        productID.add(result.id);
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
                    List<ProductResponse.ProductResult> productResults = databaseHandler.getProduct(categoryID.get(sp_product_category.getSelectedItemPosition()), brandID.get(position));
                    productName.clear();
                    productName.add("Select Product");
                    productID.clear();
                    productID.add(0);
                    for (ProductResponse.ProductResult result : productResults) {
                        productName.add(result.name);
                        productID.add(result.id);
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

        btn_select_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct(productID.get(sp_product_name.getSelectedItemPosition()));
                builder.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void addFirstProduct(Integer productId) {
        binding.spProduct.setSelection(productID.indexOf(productId));
        binding.spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                binding.tvPrice.setText(String.valueOf(productPrice.get(position)));
                binding.tvAmount.setText(String.valueOf(productPrice.get(position)));
                binding.etDiscount.setText(String.valueOf(productDiscount.get(position)));
                binding.etQty.setText("1");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addProduct(Integer productId) {
        if (firstCount == 2) {
            addFirstProduct(productId);
            firstCount++;
        } else {
            final int[] qty = {1}, selectedPosition = {0};
            final LayoutInflater inflater = (LayoutInflater) OrderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final NewOrderBinding newOrder = DataBindingUtil.inflate(inflater, R.layout.new_order, null, true);
            newOrder.spProduct.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productName));
            newOrder.spProduct.setSelection(productID.indexOf(productId));
            newOrder.spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedPosition[0] = position;
                    newOrder.tvPrice.setText(String.valueOf(productPrice.get(position)));
                    newOrder.tvAmount.setText(String.valueOf(productPrice.get(position)));
                    newOrder.etDiscount.setText(String.valueOf(productDiscount.get(position)));
                    newOrder.etQty.setText("1");
                    qty[0] = 1;
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        newOrder.spProduct.setSelection(productID.indexOf(productId));
        newOrder.etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    qty[0] = Integer.parseInt(s.toString());
                    newOrder.tvAmount.setText(String.valueOf(Integer.parseInt(s.toString()) * productPrice.get(selectedPosition[0])));
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
                newOrder.etQty.setText(String.valueOf(qty[0] + 1));
                qty[0] = qty[0] + 1;
                newOrder.tvAmount.setText(String.valueOf(qty[0] * productPrice.get(selectedPosition[0])));
            }
        });
        newOrder.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty[0] > 1) {
                    newOrder.etQty.setText(String.valueOf(qty[0] - 1));
                    qty[0] = qty[0] - 1;
                    newOrder.tvAmount.setText(String.valueOf(qty[0] * productPrice.get(selectedPosition[0])));
                } else {
                    newOrder.etQty.setText("1");
                    qty[0] = 1;
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
        binding.tblOrders.addView(newOrder.getRoot(), tableRowCount++);
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            newOrder.btnIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newOrder.etQty.setText(String.valueOf(qty[0] + 1));
                    qty[0] = qty[0] + 1;
                    newOrder.tvAmount.setText(String.valueOf(qty[0] * productPrice.get(selectedPosition[0])));
                }
            });
            newOrder.btnDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty[0] > 1) {
                        newOrder.etQty.setText(String.valueOf(qty[0] - 1));
                        qty[0] = qty[0] - 1;
                        newOrder.tvAmount.setText(String.valueOf(qty[0] * productPrice.get(selectedPosition[0])));
                    } else {
                        newOrder.etQty.setText("1");
                        qty[0] = 1;
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
            binding.tblOrders.addView(newOrder.getRoot(), tableRowCount++);
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

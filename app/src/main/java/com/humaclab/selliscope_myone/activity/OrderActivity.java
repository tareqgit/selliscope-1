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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

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

import com.humaclab.selliscope_myone.productDialog.ProductDialogFragment;
import com.humaclab.selliscope_myone.utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope_myone.utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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


    private SessionManager sessionManager;


    private int  tableRowCount = 1;
    private double totalAmt = 0, totalDiscnt = 0, grandTotal = 0;


    private static String sOutletName = "";
    private static String sOutletID = "";
    public List<SelectedProduct> mSelectedProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);

        sessionManager = new SessionManager(OrderActivity.this);

        sOutletName = getIntent().getStringExtra("outletName").toString();
        sOutletID = getIntent().getStringExtra("outletID");

        pd = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(sOutletName + "\t -\t" + getResources().getString(R.string.order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        binding.btnOrder.setOnClickListener(this);

        //For calculate total discount, amount and grand total in 1 second interval
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> runOnUiThread(() -> {
            double totalAmount = 0;
            double totalDiscount = 0;
            for (int i = 0; i < binding.tblOrders.getChildCount(); i++) {
                View view = binding.tblOrders.getChildAt(i);
                if (view instanceof TableRow) {
                    TableRow row = (TableRow) view;
                    TextView tvTotalAmount = row.findViewById(R.id.tv_amount);
                    totalAmount += Double.parseDouble(tvTotalAmount.getText().toString().replace(",", "").trim());

                    EditText tvTotalDiscnt = row.findViewById(R.id.et_discount);
                    if (!tvTotalDiscnt.getText().toString().equals(""))
                        totalDiscount += Double.parseDouble(tvTotalDiscnt.getText().toString());
                }
            }
            totalAmt = totalAmount;
            totalDiscnt = totalDiscount;
            grandTotal = totalAmount - totalDiscount;
            binding.textviewTotal.setText("Total: " + String.valueOf(totalAmount));
            binding.tvTotalAmt.setText(String.valueOf(totalAmt));
            binding.tvTotalDiscnt.setText(String.valueOf(totalDiscnt));
            Double dis=binding.editTextDiscount.getText().toString().isEmpty()?0: Double.parseDouble(binding.editTextDiscount.getText().toString().trim());

            binding.tvTotalGr.setText(String.valueOf(grandTotal - dis ) );
        }), 0, 1, TimeUnit.SECONDS);


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_order:

                if(mSelectedProducts.size()>0) {
                    pd.setMessage("Creating order....");
                    pd.setCancelable(false);
                    pd.show();


                    AddNewOrder addNewOrder = new AddNewOrder();
                    AddNewOrder.NewOrder newOrder = new AddNewOrder.NewOrder();
                    List<AddNewOrder.NewOrder.Product> products = new ArrayList<>();
                    newOrder.outletId = sOutletID;
                    newOrder.date= CurrentTimeUtilityClass.getCurrentTimeStamp();
                    newOrder.discount=binding.editTextDiscount.getText().toString().isEmpty()?0: Double.parseDouble(binding.editTextDiscount.getText().toString().trim());

                    Log.d("tareq_test", "" + binding.tblOrders.getChildCount());
                    for (int i = 0; i < binding.tblOrders.getChildCount(); i++) {
                        View view = binding.tblOrders.getChildAt(i);
                        if (view instanceof TableRow) {
                            AddNewOrder.NewOrder.Product product = new AddNewOrder.NewOrder.Product();
                            TableRow row = (TableRow) view;
                            Spinner sp = row.findViewById(R.id.sp_product);
                            EditText etQty = row.findViewById(R.id.et_qty);
                            EditText etDiscount = row.findViewById(R.id.et_discount);

                            product.id = mSelectedProducts.get(i).id;
                           /* if (etDiscount.getText().toString().equals("")) {
                                product.discount = 0.00;
                            } else {
                                product.discount = Double.parseDouble(etDiscount.getText().toString());
                            }*/
                            product.qty = Integer.parseInt(etQty.getText().toString());
                            product.price = mSelectedProducts.get(i).price.toString();
                            product.stockType = mSelectedProducts.get(i).stockType.toString();
                            //   product.flag = productFlag.get(i - 1);
                            products.add(product);
                        }
                    }

                    newOrder.products = products;
                    addNewOrder.newOrder = newOrder;

                    apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                            sessionManager.getUserPassword(), false)
                            .create(SelliscopeApiEndpointInterface.class);

                    Log.d("tareq_test", "tareq" + new Gson().toJson(addNewOrder));
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
                }else{
                    Toast.makeText(this, "Please Select A product first", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_add_order:
                showProductSelectionDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProductSelectionDialog() {

        final FragmentManager fm = getSupportFragmentManager();


        final ProductDialogFragment productDialogFragment = new ProductDialogFragment();
        productDialogFragment.show(fm, "Product_Tag");
    }


    public void addProduct(final com.humaclab.selliscope_myone.product_paging.model.ProductsItem product, Double stockTotal) {



        {


            final int[] qty = {1}, selectedPosition = {0};
            final LayoutInflater inflater = (LayoutInflater) OrderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final NewOrderBinding newOrder = DataBindingUtil.inflate(inflater, R.layout.new_order, null, true);
            newOrder.spProduct.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, new ArrayList<String>(Arrays.asList(product.id))));
            newOrder.tvPrice.setText(product.price);
            newOrder.tvAmount.setText(product.price);

            newOrder.etQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        qty[0] = Integer.parseInt(s.toString());
                        if (stockTotal >= qty[0]) { //if we do have enough in stock
                            newOrder.tvAmount.setText(String.valueOf(Integer.parseInt(s.toString()) * Double.parseDouble(product.price.toString().replace(",", "").trim())));
                        } else { //if we are entering more than stock then set the target to stock max

                            newOrder.etQty.setText(Math.round(stockTotal) + "");
                            newOrder.tvAmount.setText(String.valueOf(Math.round(stockTotal) * Double.parseDouble(product.price.toString().replace(",", "").trim())));
                            Toast.makeText(OrderActivity.this, "Stock exceed:-  " + stockTotal, Toast.LENGTH_SHORT).show();

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
                    if (stockTotal > qty[0]) {
                        qty[0] += 1;
                        newOrder.etQty.setText(String.valueOf(qty[0]));
                        newOrder.tvAmount.setText(String.valueOf(qty[0] * Double.parseDouble(product.price.toString().replace(",", "").trim())));
                    } else {
                        Toast.makeText(OrderActivity.this, "Stock exceed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            newOrder.btnDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty[0] > 1) {
                        qty[0] -= 1;
                        newOrder.etQty.setText(String.valueOf(qty[0]));
                        newOrder.tvAmount.setText(String.valueOf(qty[0] * Double.parseDouble(product.price.toString().replace(",", "").trim())));
                    } else {
                        qty[0] = 1;
                        newOrder.etQty.setText(String.valueOf(qty[0]));
                        newOrder.tvAmount.setText(String.valueOf(qty[0] * Double.parseDouble(product.price.toString().replace(",", "").trim())));
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



                 mSelectedProducts.add(new SelectedProduct(product.id,  Double.parseDouble(product.price.toString().replace(",", "").trim()),  Double.parseDouble(newOrder.etQty.getText().toString().replace(",", "").trim()), product.stockType.toString()));
            tableRowCount++;
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


   public class SelectedProduct{
        public String id;
        public Double price;
        public Double quantity;
        public String stockType;

        public SelectedProduct(String id, Double price, Double quantity, String stockType) {
            this.id = id;
            this.price = price;
            this.quantity = quantity;
            this.stockType = stockType;
        }
    }

}

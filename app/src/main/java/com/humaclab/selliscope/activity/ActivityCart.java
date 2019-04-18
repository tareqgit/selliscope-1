package com.humaclab.selliscope.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import static com.humaclab.selliscope.activity.OrderActivity.selectedProductList;


import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.SelectedProductRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityCartBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;
import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SendUserLocationData;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCart extends AppCompatActivity implements  SelectedProductRecyclerAdapter.OnRemoveFromCartListener{
    Double total = 0.0;
    private ActivityCartBinding binding;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;
    private ProgressDialog pd;
    private SendUserLocationData sendUserLocationData;
    private Double lat = 0.0, lon = 0.0;
    private String outletName, outletID;
    SelectedProductRecyclerAdapter selectedProductRecyclerAdapter;
    // private List<SelectedProductHelper> selectedProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);

        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");
       // selectedProductList = (List<SelectedProductHelper>) getIntent().getSerializableExtra("products");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(outletName + "-" + getResources().getString(R.string.order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        pd = new ProgressDialog(this);
        databaseHandler = new DatabaseHandler(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);

        //For getting location
        sendUserLocationData = new SendUserLocationData(this);
        sendUserLocationData.getInstantLocation(this, new SendUserLocationData.OnGetLocation() {
            @Override
            public void getLocation(Double latitude, Double longitude) {
                lat = latitude;
                lon = longitude;
            }
        });
        //For getting location

        //For showing total amount
        for (SelectedProductHelper selectedProduct : selectedProductList) {
          //this Segment Used for calculation of total price without promotion discount
            //  total += Double.valueOf(selectedProduct.getTotalPrice());
            //this Segment Used for calculation of total price with promotion discount
            total += Double.valueOf(selectedProduct.getTppromotionGrandPrice());
        }
        binding.tvTotal.setText(String.valueOf(total));
        if (!binding.etDiscount.getText().toString().equals("")){
            binding.tvGrandTotal.setText(String.valueOf(
                    total - Double.parseDouble(binding.etDiscount.getText().toString())
            ));
        }

        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    binding.tvGrandTotal.setText(String.valueOf(
                            total - Double.parseDouble(s.toString())
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //For showing total amount

        binding.tvOutletName.setText(outletName);
        binding.rlSelectedProducts.setLayoutManager(new LinearLayoutManager(ActivityCart.this));
        selectedProductRecyclerAdapter = new SelectedProductRecyclerAdapter(ActivityCart.this, ActivityCart.this,  ActivityCart.this);
        binding.rlSelectedProducts.setAdapter(selectedProductRecyclerAdapter);

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderNow();
            }
        });
    }

    private void orderNow() {
        pd.setMessage("Creating order....");
        pd.setCancelable(false);
        pd.show();

        try {
            AddNewOrder addNewOrder = new AddNewOrder();
            AddNewOrder.NewOrder newOrder = new AddNewOrder.NewOrder();
            List<AddNewOrder.NewOrder.Product> products = new ArrayList<>();
           // newOrder.discount = 0;
            newOrder.outletId = Integer.parseInt(outletID);
            newOrder.latitude = String.valueOf(lat);
            newOrder.longitude = String.valueOf(lon);
            newOrder.comment = binding.etComments.getText().toString();
            newOrder.orderTotal = Double.parseDouble(binding.tvTotal.getText().toString());
            newOrder.orderGrandTotal = Double.parseDouble(binding.tvGrandTotal.getText().toString());
            if (binding.etDiscount.getText().toString().equals("")) {
                newOrder.discount = 0.0;
            } else {
                newOrder.discount =  Double.parseDouble(binding.etDiscount.getText().toString());
               // newOrder.discount = Double.parseDouble(binding.etDiscount.getText().toString());
            }

            for (SelectedProductHelper selectedProduct : selectedProductList) {
                AddNewOrder.NewOrder.Product product = new AddNewOrder.NewOrder.Product();

                product.id = Integer.parseInt(selectedProduct.getProductID());
                if (binding.etDiscount.getText().toString().equals("")) {
                    product.discount = 0.00;
                } else {
                    product.discount = 0.00;
                }
                product.qty = Integer.parseInt(selectedProduct.getProductQuantity());
                product.row = Integer.parseInt(selectedProduct.getProductRow());
                product.price = selectedProduct.getProductPrice();
                product.tpDiscount = Double.parseDouble(selectedProduct.getTpDiscount());
                product.productTotal = Double.parseDouble(selectedProduct.getTotalPrice());
                product.productSubTotal = Double.parseDouble(selectedProduct.getTppromotionGrandPrice());
                products.add(product);
            }

            newOrder.products = products;
            addNewOrder.newOrder = newOrder;

            apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                    sessionManager.getUserPassword(), false)
                    .create(SelliscopeApiEndpointInterface.class);

            System.out.println("Order: " + new Gson().toJson(addNewOrder));

            if (NetworkUtility.isNetworkAvailable(ActivityCart.this)) {
                Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
                call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                    @Override
                    public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                        pd.dismiss();
                        if (response.code() == 201) {
                            System.out.println(new Gson().toJson(response.body()));
                            Toast.makeText(ActivityCart.this, "Order created successfully", Toast.LENGTH_LONG).show();
                           //clear selected Item list
                            selectedProductList.clear();
                            Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            finish();
                            //startActivity(new Intent(ActivityCart.this, OutletActivity.class));
                        } else if (response.code() == 401) {
                            System.out.println(new Gson().toJson(response.body()));
                            Toast.makeText(ActivityCart.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println(new Gson().toJson(response.body()));
                            Toast.makeText(ActivityCart.this, response.code()+" Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ActivityCart.this, "Order created successfully", Toast.LENGTH_LONG).show();
                pd.dismiss();
                //clear selected Item list
                selectedProductList.clear();
                //if net is not available que the task and get back to Outlet
                Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        binding.tvTotal.setText(String.valueOf(totalAmt));

        if (!binding.etDiscount.getText().toString().equals("")) {
            binding.tvGrandTotal.setText(String.valueOf(
                    totalAmt - Double.parseDouble(binding.etDiscount.getText().toString())
            ));
        }else{
            binding.tvGrandTotal.setText(String.valueOf(totalAmt));
        }
        selectedProductRecyclerAdapter.notifyDataSetChanged();

    }
}

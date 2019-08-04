package com.humaclab.lalteer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.SelectedProductRecyclerAdapter;
import com.humaclab.lalteer.databinding.ActivityCartBinding;
import com.humaclab.lalteer.helper.SelectedProductHelper;

import com.humaclab.lalteer.model.AddNewOrder;
import com.humaclab.lalteer.utils.CurrentTimeUtilityClass;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.GetAddressFromLatLang;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SendUserLocationData;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.humaclab.lalteer.activity.OrderActivity.selectedProductList;

public class ActivityCart extends AppCompatActivity implements SelectedProductRecyclerAdapter.OnRemoveFromCartListener {
    Double total = 0.0;
    private ActivityCartBinding mCartBinding;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;
    private ProgressDialog pd;
    private SendUserLocationData sendUserLocationData;
    private Double lat = 0.0, lon = 0.0;
    private String outletName, outletID;
    SelectedProductRecyclerAdapter selectedProductRecyclerAdapter;

   // private List<SelectedProductHelper> selectedProductList;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCartBinding= DataBindingUtil.setContentView(this, R.layout.activity_cart,null);
        mContext=this;
        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");
      //  selectedProductList = (List<SelectedProductHelper>) getIntent().getSerializableExtra("products");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(String.format("%s-%s", outletName, getResources().getString(R.string.order)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        pd = new ProgressDialog(this);
        databaseHandler = new DatabaseHandler(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);

        //For getting location
        sendUserLocationData = new SendUserLocationData(this);
        sendUserLocationData.getInstantLocation(this, (latitude, longitude) -> {
            lat = latitude;
            lon = longitude;
        });
        //For getting location

        //For showing total amount
        for (SelectedProductHelper selectedProduct : selectedProductList) {
            total += Double.valueOf(selectedProduct.getTotalPrice());
        }
        mCartBinding.tvTotal.setText(String.valueOf(total));
        if (!mCartBinding.etDiscount.getText().toString().equals(""))
            mCartBinding.tvGrandTotal.setText(String.valueOf(
                    total - Double.parseDouble(mCartBinding.etDiscount.getText().toString())
            ));
        mCartBinding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    mCartBinding.tvGrandTotal.setText(String.valueOf(
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

        mCartBinding.tvOutletName.setText(outletName);
        mCartBinding.rlSelectedProducts.setLayoutManager(new LinearLayoutManager(ActivityCart.this));
        selectedProductRecyclerAdapter=new SelectedProductRecyclerAdapter(ActivityCart.this, ActivityCart.this, selectedProductList, ActivityCart.this);
        mCartBinding.rlSelectedProducts.setAdapter(selectedProductRecyclerAdapter);

        mCartBinding.btnOrder.setOnClickListener(v -> orderNow());

        setPaymentTypeSprinner();
    }

    private void setPaymentTypeSprinner() {
        List<String>  paymentTypes =new ArrayList<>();
        paymentTypes.clear();
        paymentTypes.add(getString(R.string.select_payment));
        paymentTypes.add(getString(R.string.select_payment_cash));
        paymentTypes.add(getString(R.string.select_payment_credit));
        paymentTypes.add(getString(R.string.select_payment_advance));


        mCartBinding.spinnerPaymentType.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, paymentTypes));
        mCartBinding.spinnerPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       //         Toast.makeText(mContext, ""+mCartBinding.spinnerPaymentType.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void orderNow() {
        pd.setMessage(getString(R.string.creatingOrder));
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
            newOrder.comment = mCartBinding.etComments.getText().toString();
            newOrder.transport=mCartBinding.etTransport.getText().toString();
            newOrder.paymentType=mCartBinding.spinnerPaymentType.getSelectedItemPosition();
            newOrder.orderTimeStamp= CurrentTimeUtilityClass.getCurrentTimeStamp();

            if (mCartBinding.etDiscount.getText().toString().equals("")) {
                newOrder.discount = 0;
            } else {
                newOrder.discount =  Integer.parseInt(mCartBinding.etDiscount.getText().toString());
               // newOrder.discount = Double.parseDouble(mCartBinding.etDiscount.getText().toString());
            }

            for (SelectedProductHelper selectedProduct : selectedProductList) {
                AddNewOrder.NewOrder.Product product = new AddNewOrder.NewOrder.Product();

                product.id = Integer.parseInt(selectedProduct.getProductID());
                if (mCartBinding.etDiscount.getText().toString().equals("")) {
                    product.discount = 0.00;
                } else {
                    product.discount = 0.00;
                }
                product.qty = Integer.parseInt(selectedProduct.getProductQuantity());
             //   product.row = Integer.parseInt(selectedProduct.getProductRow());
                product.price = selectedProduct.getProductPrice();
                products.add(product);
            }

            newOrder.products = products;
            addNewOrder.newOrder = newOrder;

            apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                    sessionManager.getUserPassword(), false)
                    .create(SelliscopeApiEndpointInterface.class);


            if (NetworkUtility.isNetworkAvailable(ActivityCart.this)) {
               addNewOrder.newOrder.address= String.valueOf(GetAddressFromLatLang.getAddressFromLatLan(this, lat,lon));

                Log.d("tareq_test" , ""+new Gson().toJson(addNewOrder));
                Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
                call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                    @Override
                    public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {

                        if (response.code() == 200) {
                          Log.d("tareq_test" , ""+new Gson().toJson(response.body()));
                            Toast.makeText(ActivityCart.this, "Order created successfully", Toast.LENGTH_LONG).show();

                            //clear selected Item list
                            selectedProductList.clear();

                            Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            //startActivity(new Intent(ActivityCart.this, OutletActivity.class));
                        } else if (response.code() == 401) {
                             Log.d("tareq_test" , ""+new Gson().toJson(response.body()));
                            Toast.makeText(ActivityCart.this, response.code()+" Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                         Log.d("tareq_test" , ""+new Gson().toJson(response.body()));
                            Toast.makeText(ActivityCart.this, response.code()+" Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }

                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                        pd.dismiss();
                        t.printStackTrace();
                    }
                });
            } else {
                Log.d("tareq_test" , "Order has been queued in database");
                databaseHandler.setOrder(addNewOrder);
                Toast.makeText(ActivityCart.this, "Order created successfully", Toast.LENGTH_LONG).show();
                pd.dismiss();
                //clear selected Item list
                selectedProductList.clear();

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
        Log.d("tareq_test" , "Item removed from cart");
        
        if (selectedProductList.contains(selectedProduct)) {
            selectedProductList.remove(selectedProductList.indexOf(selectedProduct));
        }

        System.out.println("Product list:" + new Gson().toJson(selectedProductList) + "\nIndex: " + selectedProductList.indexOf(selectedProduct));

        Double totalAmt = 0.00;
        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
            totalAmt += Double.valueOf(selectedProductHelper.getTotalPrice());
        }
        mCartBinding.tvTotal.setText(String.valueOf(totalAmt));

        if (!mCartBinding.etDiscount.getText().toString().equals("")) {
            mCartBinding.tvGrandTotal.setText(String.valueOf(
                    totalAmt - Double.parseDouble(mCartBinding.etDiscount.getText().toString())
            ));
        }else{
            mCartBinding.tvGrandTotal.setText(String.valueOf(totalAmt));
        }

        selectedProductRecyclerAdapter.notifyDataSetChanged();
    }
}

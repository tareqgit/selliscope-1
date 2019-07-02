/*
 * Created by Tareq Islam on 7/1/19 1:52 PM
 *
 *  Last modified 7/1/19 12:28 PM
 */

/*
 * Created by Tareq Islam on 7/1/19 12:25 PM
 *
 *  Last modified 7/1/19 12:25 PM
 */

package com.humaclab.selliscope_mohammadi.cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_mohammadi.SelliscopeApplication;
import com.humaclab.selliscope_mohammadi.activity.OrderActivity;
import com.humaclab.selliscope_mohammadi.activity.PaymentActivity;
import com.humaclab.selliscope_mohammadi.cart.model.CartObject;
import com.humaclab.selliscope_mohammadi.databinding.ActivityCartBinding;
import com.humaclab.selliscope_mohammadi.model.AddNewOrder;
import com.humaclab.selliscope_mohammadi.utils.DatabaseHandler;
import com.humaclab.selliscope_mohammadi.utils.NetworkUtility;
import com.humaclab.selliscope_mohammadi.utils.SessionManager;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.humaclab.selliscope_mohammadi.order.OrderNewActivity.s_CartObjects;

public class CartActivity extends AppCompatActivity {
    private String outletId, outletName, outletCreditBalance;
    DatabaseHandler databaseHandler;
    RecyclerView mRecyclerView;
    ActivityCartBinding mBinding;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        databaseHandler = new DatabaseHandler(this);
        outletId = getIntent().getStringExtra("outletID");
        Log.d("tareq_test" , "id"+ outletId);
        outletName = getIntent().getStringExtra("outletName");
        outletCreditBalance = getIntent().getStringExtra("outletCreditBalance");
        pd = new ProgressDialog(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cart - " + outletName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = mBinding.recyclerView;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new CartAdapter(this, s_CartObjects, new CartAdapter.OnItemRemove() {
            @Override
            public void onRemove() {
                updateTotal();
            }
        }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotal();


        mBinding.editTextTruck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    if (!mBinding.editTextTruck.getText().toString().isEmpty()) {
                        double truckFare = Double.parseDouble(mBinding.editTextTruck.getText().toString());
                        double Gratotal = Double.parseDouble(mBinding.textViewTotalTk.getText().toString()) + truckFare - Double.parseDouble(mBinding.editTextDiscount.getText().toString().isEmpty() ? "0" : mBinding.editTextDiscount.getText().toString());
                        mBinding.textViewGrand.setText(String.format(Locale.US, "%.2f", Gratotal));
                        mBinding.textViewBgTotal.setText(String.format(Locale.US, "%.2f", Double.parseDouble(outletCreditBalance) - Gratotal));
                    } else {
                        double truckFare = 0;
                        double Gratotal = Double.parseDouble(mBinding.textViewTotalTk.getText().toString()) + truckFare - Double.parseDouble(mBinding.editTextDiscount.getText().toString().isEmpty() ? "0" : mBinding.editTextDiscount.getText().toString());
                        mBinding.textViewGrand.setText(String.format(Locale.US, "%.2f", Gratotal));
                        mBinding.textViewBgTotal.setText(String.format(Locale.US, "%.2f", Double.parseDouble(outletCreditBalance) - Gratotal));
                    }
                }catch (Exception ex){
                    Log.d("tareq_test" , ""+ex.getMessage());
               //     Toast.makeText(CartActivity.this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.editTextDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {


                    if (!mBinding.editTextDiscount.getText().toString().isEmpty()) {
                        double discount = Double.parseDouble(mBinding.editTextDiscount.getText().toString());

                        double grandTotal = Double.parseDouble(mBinding.textViewTotalTk.getText().toString()) - discount + Double.parseDouble(mBinding.editTextTruck.getText().toString().isEmpty() ? "0" : mBinding.editTextTruck.getText().toString());
                        mBinding.textViewGrand.setText(String.format(Locale.US ,"%.2f", grandTotal));
                        mBinding.textViewBgTotal.setText(String.format(Locale.US,"%.2f", Double.parseDouble(outletCreditBalance) - grandTotal));
                    } else {
                        double discount = 0;

                        double grandTotal = Double.parseDouble(mBinding.textViewTotalTk.getText().toString()) - discount + Double.parseDouble(mBinding.editTextTruck.getText().toString().isEmpty() ? "0" : mBinding.editTextTruck.getText().toString());
                        mBinding.textViewGrand.setText(String.format(Locale.US ,"%.2f", grandTotal));
                        mBinding.textViewBgTotal.setText(String.format(Locale.US,"%.2f", Double.parseDouble(outletCreditBalance) - grandTotal));
                    }
                }catch (Exception ex){
                    Log.d("tareq_test" , "Error"+ ex.getMessage());
                  //  Toast.makeText(CartActivity.this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        PushDownAnim.setPushDownAnimTo(mBinding.Order).setOnSingleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderItems();
            }
        });

        PushDownAnim.setPushDownAnimTo(mBinding.cancel).setOnSingleClickListener(v -> {
            finish();
        });
    }

    private void updateTotal() {
        Double totalTaka = 0d;
        Double totalQty = 0d;

        for (CartObject cartObject : s_CartObjects) {
            totalTaka += cartObject.getTotal_price();
            totalQty += cartObject.getQty();
        }
        mBinding.textViewTotalTk.setText(String.format(Locale.US,"%.2f", totalTaka));
        mBinding.textViewQty.setText(String.format(Locale.US,"%.2f",totalQty));
        mBinding.textViewGrand.setText(String.format(Locale.US,"%.2f",totalTaka)); //its just the initialization
        mBinding.textViewBgTotal.setText(String.format(Locale.US,"%.2f",Double.parseDouble(outletCreditBalance) - totalTaka));
    }


    void orderItems() {
      pd.setMessage("Creating order....");
      pd.setCancelable(false);
      pd.show();
/*try{*/
            AddNewOrder addNewOrder = new AddNewOrder();
            AddNewOrder.NewOrder newOrder = new AddNewOrder.NewOrder();
            List<AddNewOrder.NewOrder.Product> products = new ArrayList<>();
            newOrder.outletId = Integer.parseInt(outletId.trim());

            for (CartObject cartObject : s_CartObjects) {

                AddNewOrder.NewOrder.Product product = new AddNewOrder.NewOrder.Product();

                product.id = 2;
                product.discount = 0.00;
                product.qty = cartObject.getQty();

                List<String> rowsDia = new ArrayList<>();
                rowsDia = databaseHandler.getVariantRowFromDetails(cartObject.getDia());
                List<String> rowsGrade = new ArrayList<>();
                rowsGrade = databaseHandler.getVariantRowFromDetails(cartObject.getGrade());
                rowsDia.retainAll(rowsGrade);
                Log.d("tareq_test" , "dias: "+ rowsDia.get(0));

                product.row = Integer.parseInt(rowsDia.get(0));
                product.price = cartObject.getRate().toString();
                products.add(product);

            }

            newOrder.additionalCharge = Double.parseDouble( mBinding.editTextTruck.getText().toString().isEmpty()?"0":mBinding.editTextTruck.getText().toString());
            ;
            if (mBinding.radioButtonBand.isChecked()) {
                newOrder.bend = 1;
            } else {
                newOrder.bend = 2;
            }
            if (mBinding.radioButtonCap.isChecked()) {
                newOrder.cap = 1;
            } else {
                newOrder.cap = 2;
            }
            newOrder.discount = Double.parseDouble(mBinding.editTextDiscount.getText().toString().isEmpty()?"0":mBinding.editTextDiscount.getText().toString());

            newOrder.remarks = mBinding.remarks.getEditText().getText().toString();
            newOrder.deliveryAddress = mBinding.deliveryAddress.getEditText().getText().toString();
            newOrder.products = products;
            addNewOrder.newOrder = newOrder;
          SessionManager sessionManager=  new SessionManager(this);
             SelliscopeApiEndpointInterface   apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                    sessionManager.getUserPassword(), false)
                    .create(SelliscopeApiEndpointInterface.class);

          Log.d("tareq_test" , ""+ "Order: " + new Gson().toJson(addNewOrder));

            if (NetworkUtility.isNetworkAvailable(this)) {
                Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
                call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                    @Override
                    public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                        pd.dismiss();
                        Log.d("tareq_test" , ""+new Gson().toJson(response.body()));
                        if (response.code() == 201) {
                       Log.d("tareq_test" , ""+new Gson().toJson(response.body()));
                            Toast.makeText(CartActivity.this, "Order created successfully", Toast.LENGTH_LONG).show();
                            s_CartObjects.clear();
                            startActivity(new Intent(CartActivity.this, PaymentActivity.class));
                            finish();
                        } else if (response.code() == 401) {
                            Log.d("tareq_test" , ""+new Gson().toJson(response.body()));
                            Toast.makeText(CartActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("tareq_test" , ""+new Gson().toJson(response.body()));
                            Toast.makeText(CartActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CartActivity.this, "Order created successfully", Toast.LENGTH_LONG).show();
                pd.dismiss();
                finish();
            }
        /*} catch (Exception e) {
            e.printStackTrace();
        }*/
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
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}

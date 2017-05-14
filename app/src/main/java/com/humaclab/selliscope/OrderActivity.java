package com.humaclab.selliscope;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.Utils.DatabaseHandler;
import com.humaclab.selliscope.Utils.NetworkUtility;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.adapters.OutletRecyclerViewAdapter;
import com.humaclab.selliscope.databinding.ActivityOrderBinding;
import com.humaclab.selliscope.databinding.NewOrderBinding;
import com.humaclab.selliscope.model.Order;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.ProductResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityOrderBinding binding;
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private List<String> productName = new ArrayList<>(), outletName = new ArrayList<>();
    private List<Integer> productID = new ArrayList<>(), productDiscount = new ArrayList<>(), outletID = new ArrayList<>();
    private List<Double> productPrice = new ArrayList<>();

    private int selectedPosition = 0, tableRowCount = 2;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(OrderActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.order));
        setSupportActionBar(toolbar);

        binding.btnIncrease.setOnClickListener(this);
        binding.btnDecrease.setOnClickListener(this);

        binding.spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                binding.tvPrice.setText(String.valueOf(productPrice.get(position)));
                binding.tvAmount.setText(String.valueOf(productPrice.get(position)));
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
                binding.tvAmount.setText(String.valueOf(Integer.parseInt(s.toString()) * productPrice.get(selectedPosition)));
                totalAmount = Integer.parseInt(s.toString()) * productPrice.get(selectedPosition);
                binding.tvTotalAmt.setText(String.valueOf(totalAmount));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (NetworkUtility.isNetworkAvailable(this)) {
            getOutlets();
            getProducts();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data",
                    Toast.LENGTH_SHORT).show();
        }

        binding.tvTotalAmt.setText(String.valueOf(totalAmount));
        binding.btnOrder.setOnClickListener(this);
    }

    private void getProducts() {
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
        });
    }

    private void getOutlets() {
        SessionManager sessionManager = new SessionManager(OrderActivity.this);
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
        });

    }

    @Override
    public void onClick(View v) {
        int qty = Integer.parseInt(binding.etQty.getText().toString());
        switch (v.getId()) {
            case R.id.btn_increase:
                binding.etQty.setText(String.valueOf(qty + 1));
                qty = qty + 1;
                binding.tvAmount.setText(String.valueOf(qty * productPrice.get(selectedPosition)));
                totalAmount = qty * productPrice.get(selectedPosition);
                binding.tvTotalAmt.setText(String.valueOf(totalAmount));
                break;
            case R.id.btn_decrease:
                if (qty > 1) {
                    binding.etQty.setText(String.valueOf(qty - 1));
                    qty = qty - 1;
                    binding.tvAmount.setText(String.valueOf(qty * productPrice.get(selectedPosition)));
                    totalAmount = qty * productPrice.get(selectedPosition);
                    binding.tvTotalAmt.setText(String.valueOf(totalAmount));
                } else {
                    binding.etQty.setText("1");
                    qty = 1;
                    binding.tvAmount.setText(String.valueOf(qty * productPrice.get(selectedPosition)));
                    binding.tvTotalAmt.setText(String.valueOf(productPrice.get(selectedPosition)));
                }
                break;
            case R.id.btn_order:
                Order order = new Order();
                Order.NewOrder newOrder = new Order.NewOrder();
                List<Order.NewOrder.Product> products = new ArrayList<>();
                newOrder.outletId = outletID.get(binding.spOutlet.getSelectedItemPosition());

                for (int i = 1; i < binding.tblOrders.getChildCount(); i++) {
                    View view = binding.tblOrders.getChildAt(i);
                    if (view instanceof TableRow) {
                        Order.NewOrder.Product product = new Order.NewOrder.Product();
                        TableRow row = (TableRow) view;
                        Spinner sp = (Spinner) row.findViewById(R.id.sp_product);
                        EditText et = (EditText) row.findViewById(R.id.et_qty);

                        product.id = productID.get(sp.getSelectedItemPosition());
                        product.discount = productDiscount.get(sp.getSelectedItemPosition());
                        product.qty = Integer.parseInt(et.getText().toString());
                        products.add(product);
                    }
                }

                newOrder.products = products;
                order.newOrder = newOrder;

                apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false)
                        .create(SelliscopeApiEndpointInterface.class);
                System.out.println(new Gson().toJson(order));

                Call<Order.OrderResponse> call = apiService.addOrder(order);
                call.enqueue(new Callback<Order.OrderResponse>() {
                    @Override
                    public void onResponse(Call<Order.OrderResponse> call, Response<Order.OrderResponse> response) {
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
                    public void onFailure(Call<Order.OrderResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_order) {
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
                    newOrder.etQty.setText("1");
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
                    newOrder.tvAmount.setText(String.valueOf(Integer.parseInt(s.toString()) * productPrice.get(selectedPosition[0])));
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

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void cancelOrder(View view) {
        finish();
    }
}

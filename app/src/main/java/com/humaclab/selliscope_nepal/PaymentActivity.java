package com.humaclab.selliscope_nepal;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_nepal.Utils.DatabaseHandler;
import com.humaclab.selliscope_nepal.Utils.NetworkUtility;
import com.humaclab.selliscope_nepal.Utils.SessionManager;
import com.humaclab.selliscope_nepal.adapters.PaymentRecyclerViewAdapter;
import com.humaclab.selliscope_nepal.model.Payment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private RecyclerView rv_payment;
    private SwipeRefreshLayout srl_payment;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Payment");
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);
        pd = new ProgressDialog(this);

        rv_payment = (RecyclerView) findViewById(R.id.rv_payment);
        rv_payment.setLayoutManager(new LinearLayoutManager(this));
        srl_payment = (SwipeRefreshLayout) findViewById(R.id.srl_payment);
        srl_payment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(PaymentActivity.this)) {
                    loadPayments();
                } else {
                    Toast.makeText(PaymentActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (NetworkUtility.isNetworkAvailable(this)) {
            loadPayments();
        } else {
            Toast.makeText(this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPayments() {
        pd.setMessage("Loading payment list.....");
        pd.show();

        SessionManager sessionManager = new SessionManager(PaymentActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        Call<Payment> call = apiService.getPayment();
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    try {
                        if (srl_payment.isRefreshing())
                            srl_payment.setRefreshing(false);

                        System.out.println("Response " + new Gson().toJson(response.body()));
                        List<Payment.OrderList> orders = response.body().result.orderList;
                        if (!orders.isEmpty()) {
                            rv_payment.setAdapter(new PaymentRecyclerViewAdapter(getApplication(), orders));
                        } else {
                            Toast.makeText(getApplicationContext(), "You don't have any due payments.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(PaymentActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                pd.dismiss();
                t.printStackTrace();
            }
        });
    }
}

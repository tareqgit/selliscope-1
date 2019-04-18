package com.humaclab.lalteer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.PaymentRecyclerViewAdapter;
import com.humaclab.lalteer.model.Payment;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;

    public void setOnImageResultAchiveListener(OnImageResultAchiveListener onImageResultAchiveListener) {
        mOnImageResultAchiveListener = onImageResultAchiveListener;
    }

    private OnImageResultAchiveListener mOnImageResultAchiveListener;
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private RecyclerView rv_payment;
    private SwipeRefreshLayout srl_payment;
    private ProgressDialog pd;
    private int outletId;
    PaymentRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.payment));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        outletId = getIntent().getIntExtra("outletID",0);
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

    public void loadPayments() {
        pd.setMessage("Loading payment list.....");
        pd.show();

        SessionManager sessionManager = new SessionManager(PaymentActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        Call<Payment> call = apiService.getPayment(outletId);
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
                             adapter = new PaymentRecyclerViewAdapter(PaymentActivity.this, orders, new PaymentRecyclerViewAdapter.OnPaymentListener() {
                                 @Override
                                 public void onPaymentComplete() {
                                     loadPayments();
                                 }
                             });
                            rv_payment.setAdapter(adapter);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                pd.dismiss();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
   // public static Bitmap sBitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            assert photo != null;
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            String promotionImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            // binding.ivTakeImage.setImageBitmap(photo);
            Log.d("tareq_test" , ""+promotionImage);
            Toast.makeText(this, ""+promotionImage, Toast.LENGTH_SHORT).show();
            mOnImageResultAchiveListener.onImageAchive(photo, promotionImage);

        }
    }



   /* @Override
    public void onImageClick() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }*/

   public interface OnImageResultAchiveListener{
       void onImageAchive(Bitmap image, String img);
   }
}

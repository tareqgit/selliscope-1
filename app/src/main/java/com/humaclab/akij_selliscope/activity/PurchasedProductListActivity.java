package com.humaclab.akij_selliscope.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.databinding.ActivityPurchasedProductListBinding;
import com.humaclab.akij_selliscope.model.Audit.Audit;
import com.humaclab.akij_selliscope.model.Order.Order;
import com.humaclab.akij_selliscope.model.Outlets;
import com.humaclab.akij_selliscope.model.PurchaseHistory.PurchaseHistoryItem;
import com.humaclab.akij_selliscope.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Purches History specific All Data
public class PurchasedProductListActivity extends AppCompatActivity {
    private ActivityPurchasedProductListBinding binding;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;
    private Outlets.Outlet outlet;
    private PurchaseHistoryItem purchaseHistoryItem;
    String q_one, q_two, q_three, q_four, q_five, q_six, q_seven;
    RadioButton rb_one, rb_two, rb_three_a, rb_three_b, rb_four, rb_five, rb_seven;
    private ProgressDialog pd;
    private String millisInString;
    private AlertDialog builder;
    private WebView web_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchased_product_list);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");
        purchaseHistoryItem = (PurchaseHistoryItem) getIntent().getSerializableExtra("product_list");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        millisInString = dateFormat.format(new Date());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Order No: " + purchaseHistoryItem.getOrderId() + " Audit");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);
        pd = new ProgressDialog(this);
        //binding.rlPurchasedProduct.setLayoutManager(new LinearLayoutManager(this));

//        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PurchasedProductListActivity.this, OrderActivity.class);
//                intent.putExtra("outletName", outlet.outletName);
//                intent.putExtra("outletID", outlet.outletId);
//                startActivity(intent);
//            }
//        });
        //getPurchasedProducts();

        //Glide.with(getApplicationContext()).load(purchaseHistoryItem.getMemo_img())
        //        .thumbnail(0.5f)
        //        .into(binding.imgMemo);

        //Glide.with(getApplicationContext()).load(purchaseHistoryItem.getOutlet_img())
        //        .thumbnail(0.5f)
        //        .into(binding.imgOutlet);


        Picasso.get().load(purchaseHistoryItem.getOutlet_img()).into(binding.imgOutlet);
        Picasso.get().load(purchaseHistoryItem.getMemo_img()).into(binding.imgMemo);

        binding.imgOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog(purchaseHistoryItem.getOutlet_img());
            }
        });

        binding.imgMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog(purchaseHistoryItem.getMemo_img());
            }
        });

        if(!(outlet.line ==null)){
            if (outlet.line.equals("A")) {

                binding.llQ3A.setVisibility(View.VISIBLE);
                binding.llQ3B.setVisibility(View.GONE);
                binding.rgQ1.check(R.id.rb_a);

            }
            if (outlet.line.equals(("B"))) {
                binding.llQ3B.setVisibility(View.VISIBLE);
                binding.llQ3A.setVisibility(View.GONE);
                binding.rgQ1.check(R.id.rb_b);
            }
        }
        else {
            Toast.makeText(this, "Line Not Found", Toast.LENGTH_SHORT).show();
        }



    }

    //private void getPurchasedProducts() {
    //    binding.rlPurchasedProduct.setAdapter(new PurchasedProductRecyclerAdapter(PurchasedProductListActivity.this, purchaseHistoryItem.getOrderDetails()));
    //}

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

    public void btn_submit(View view) {

        q_three = null;
        rb_one = binding.rgQ1.findViewById(binding.rgQ1.getCheckedRadioButtonId());
        q_one = (String) rb_one.getText();


        if (binding.rgQ2.getCheckedRadioButtonId() == -1) {
            binding.tvQ2.setError("");
            return;
        } else {
            binding.tvQ2.setError(null);
            rb_two = binding.rgQ2.findViewById(binding.rgQ2.getCheckedRadioButtonId());
            q_two = (String) rb_two.getText();
            // one of the radio buttons is checked
        }


        if (q_one.equals("A")) {
            if (binding.rgQ3A.getCheckedRadioButtonId() == -1) {
                binding.tvQ3A.setError("");
                return;

            } else {
                binding.tvQ3A.setError(null);
                rb_three_a = binding.rgQ3A.findViewById(binding.rgQ3A.getCheckedRadioButtonId());
                q_three = (String) rb_three_a.getText();
                // one of the radio buttons is checked
            }
        }

        if (q_one.equals("B")) {
            if (binding.rgQ3B.getCheckedRadioButtonId() == -1) {
                binding.tvQ3B.setError("");
                return;
            } else {
                binding.tvQ3B.setError(null);
                rb_three_b = binding.rgQ3B.findViewById(binding.rgQ3B.getCheckedRadioButtonId());
                q_three = (String) rb_three_b.getText();
                // one of the radio buttons is checked
            }
        }




        /*rb_three =binding.rgQ3.findViewById(binding.rgQ3.getCheckedRadioButtonId());
        q_three = (String) rb_three.getText();*/

        if (binding.rgQ4.getCheckedRadioButtonId() == -1) {
            binding.tvQ4.setError("");
            return;
        } else {
            binding.tvQ4.setError(null);
            rb_four = binding.rgQ4.findViewById(binding.rgQ4.getCheckedRadioButtonId());
            q_four = (String) rb_four.getText();
        }


        if (binding.rgQ5.getCheckedRadioButtonId() == -1) {
            //binding.tvQ2.setError("error");
            binding.tvQ5.setError("");
            return;
        } else {
            binding.tvQ5.setError(null);
            rb_five = binding.rgQ5.findViewById(binding.rgQ5.getCheckedRadioButtonId());
            q_five = (String) rb_five.getText();
        }



        if(binding.etQSix.getText().toString().isEmpty()){
            binding.tvQ6.setError("");
            return;
        }else {
            binding.tvQ6.setError(null);
            q_six = binding.etQSix.getText().toString();
        }


        if (binding.rgQ7.getCheckedRadioButtonId() == -1) {
            //binding.tvQ2.setError("error");
            binding.tvQ7.setError("");
            return;
        } else {
            binding.tvQ7.setError(null);
            rb_seven = binding.rgQ7.findViewById(binding.rgQ7.getCheckedRadioButtonId());
            q_seven = (String) rb_seven.getText();
        }


        //Toast.makeText(this, "" + q_one + q_two + q_three+q_four + q_five + q_six + q_seven, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "" + q_two, Toast.LENGTH_SHORT).show();

        Audit();
    }


    public void Audit(){
        pd.setMessage("Progressing Audit....");
        pd.setCancelable(false);
        pd.show();


        Audit audit = new Audit();
        audit.setOutletId(outlet.outletId);
        audit.setAuditDate(millisInString);
        audit.setQuestion1(q_one);
        audit.setQuestion2(q_two);
        audit.setQuestion3(q_three);
        audit.setQuestion4(q_four);
        audit.setQuestion5(q_five);
        audit.setQuestion6(q_six);
        audit.setQuestion7(q_seven);
        audit.setOrdertId(purchaseHistoryItem.getOrderId());

        Call<Audit.AuditResponse> auditResponseCall = apiService.AUDIT_RESPONSE_CALL(audit);
        auditResponseCall.enqueue(new Callback<Audit.AuditResponse>() {
            @Override
            public void onResponse(Call<Audit.AuditResponse> call, Response<Audit.AuditResponse> response) {
                pd.dismiss();
                if (response.isSuccessful()){
                    if(!response.body().getError()){
                        Toast.makeText(PurchasedProductListActivity.this,""+response.body().getResult(),Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        Toast.makeText(PurchasedProductListActivity.this, ""+response.code()+" Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Audit.AuditResponse> call, Throwable t) {
                pd.dismiss();
            }
        });



    }
    public void updateDialog( String url) {
        builder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.popup_image, null);
        builder.setView(dialogView);


        web_image = dialogView.findViewById(R.id.web_image);
        web_image.getSettings().setJavaScriptEnabled(true); // enable javascript

        //final Activity activity = this;

        web_image.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });

        web_image.loadUrl(url);



        builder.setCancelable(true);
        builder.show();
    }

}

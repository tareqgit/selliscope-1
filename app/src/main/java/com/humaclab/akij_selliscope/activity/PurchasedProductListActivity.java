package com.humaclab.akij_selliscope.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.adapters.PurchasedProductRecyclerAdapter;
import com.humaclab.akij_selliscope.databinding.ActivityPurchasedProductListBinding;
import com.humaclab.akij_selliscope.model.Outlets;
import com.humaclab.akij_selliscope.model.PurchaseHistory.PurchaseHistoryItem;
import com.humaclab.akij_selliscope.utils.SessionManager;
import com.squareup.picasso.Picasso;

// Purches History specific All Data
public class PurchasedProductListActivity extends AppCompatActivity {
    private ActivityPurchasedProductListBinding binding;
    private SessionManager sessionManager;
    private Outlets.Outlet outlet;
    private PurchaseHistoryItem purchaseHistoryItem;
    String q_one, q_two, q_three, q_four, q_five, q_six, q_seven;
    RadioButton rb_one, rb_two, rb_three_a, rb_three_b, rb_four, rb_five, rb_seven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchased_product_list);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");
        purchaseHistoryItem = (PurchaseHistoryItem) getIntent().getSerializableExtra("product_list");


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Order No: " + purchaseHistoryItem.getOrderId() + " Audit");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
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
            //binding.tvQ2.setError("error");
        } else {
            rb_two = binding.rgQ2.findViewById(binding.rgQ2.getCheckedRadioButtonId());
            q_two = (String) rb_two.getText();
            // one of the radio buttons is checked
        }


        if (q_one.equals("A")) {
            if (binding.rgQ3A.getCheckedRadioButtonId() == -1) {
                //binding.tvQ2.setError("error");
            } else {
                rb_three_a = binding.rgQ3A.findViewById(binding.rgQ3A.getCheckedRadioButtonId());
                q_three = (String) rb_three_a.getText();
                // one of the radio buttons is checked
            }
        }

        if (q_one.equals("B")) {
            if (binding.rgQ3B.getCheckedRadioButtonId() == -1) {
                //binding.tvQ2.setError("error");
            } else {
                rb_three_b = binding.rgQ3B.findViewById(binding.rgQ3B.getCheckedRadioButtonId());
                q_three = (String) rb_three_b.getText();
                // one of the radio buttons is checked
            }
        }




        /*rb_three =binding.rgQ3.findViewById(binding.rgQ3.getCheckedRadioButtonId());
        q_three = (String) rb_three.getText();*/

        if (binding.rgQ4.getCheckedRadioButtonId() == -1) {
            //binding.tvQ2.setError("error");
        } else {
            rb_four = binding.rgQ4.findViewById(binding.rgQ4.getCheckedRadioButtonId());
            q_four = (String) rb_four.getText();
        }


        if (binding.rgQ5.getCheckedRadioButtonId() == -1) {
            //binding.tvQ2.setError("error");
        } else {
            rb_five = binding.rgQ5.findViewById(binding.rgQ5.getCheckedRadioButtonId());
            q_five = (String) rb_five.getText();
        }


        q_six = binding.etQSix.getText().toString();


        if (binding.rgQ7.getCheckedRadioButtonId() == -1) {
            //binding.tvQ2.setError("error");
        } else {
            rb_seven = binding.rgQ7.findViewById(binding.rgQ7.getCheckedRadioButtonId());
            q_seven = (String) rb_seven.getText();
        }


        Toast.makeText(this, "" + q_one + q_two + q_three+q_four + q_five + q_six + q_seven, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "" + q_two, Toast.LENGTH_SHORT).show();

    }
}

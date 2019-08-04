/*
 * Created by Tareq Islam on 6/27/19 3:13 PM
 *
 *  Last modified 6/27/19 3:13 PM
 */

package com.humaclab.selliscope_mohammadi.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.cart.CartActivity;
import com.humaclab.selliscope_mohammadi.cart.model.CartObject;
import com.humaclab.selliscope_mohammadi.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class OrderNewActivity extends AppCompatActivity {

    List<String> diameters=new ArrayList<>();
    List<String> grades=new ArrayList<>();

    RecyclerView mRecyclerView;

    DiameterAdapter mDiameterAdapter;

    private String outletId, outletName, outletCreditBalance;

    public static List<CartObject> s_CartObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Order");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        outletId = String.valueOf(getIntent().getIntExtra("outletID",-1));
        Log.d("tareq_test" , "ID"+ outletId);
        outletName = getIntent().getStringExtra("outletName");
        outletCreditBalance =String.valueOf(getIntent().getIntExtra("outletCreditBalance",-1));

        mRecyclerView = findViewById(R.id.diameter_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        DatabaseHandler databaseHandler = new DatabaseHandler(this);


        diameters.addAll(databaseHandler.getVariants(1, databaseHandler.getProductIds().size()==0? 0 :databaseHandler.getProductIds().get(0)));
        grades.addAll(databaseHandler.getVariants(2,  databaseHandler.getProductIds().size()==0? 0 :databaseHandler.getProductIds().get(0)));

        Log.d("tareq_test" , "grades"+ new Gson().toJson(grades));

        mDiameterAdapter = new DiameterAdapter(this, diameters, null);
        mRecyclerView.setAdapter(mDiameterAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDiameterAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //clear selected Item list
        s_CartObjects.clear();
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_order:
              //  startActivity(new Intent(OrderNewActivity.this, CartActivity.class));
               if (!s_CartObjects.isEmpty()) {
                    Intent intent = new Intent(OrderNewActivity.this, CartActivity.class);
                    intent.putExtra("outletID", outletId);
                    intent.putExtra("outletName", outletName);
                    //intent.putExtra("products", (Serializable) selectedProductList);
                   intent.putExtra("outletCreditBalance", outletCreditBalance);

             //       recyclerViewState = binding.rvProduct.getLayoutManager().onSaveInstanceState(); //before moving to another activity store recycler state

                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No product selected yet.\nPlease select some product first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        s_CartObjects.clear();
        super.onDestroy();
    }
}

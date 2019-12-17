package com.easyopstech.easyops.sales_return;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.RootApplication;
import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivitySalesReturn2019Binding;
import com.easyopstech.easyops.model.variant_product.ProductsItem;
import com.easyopstech.easyops.sales_return.model.post.SalesReturn2019SelectedProduct;
import com.easyopstech.easyops.utils.DatabaseHandler;
import com.easyopstech.easyops.utils.SessionManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SalesReturn_2019_Activity extends AppCompatActivity implements SalesReturn_2019_Adapter.OnSelectProductListener {
    private Context mContext;

    private RootApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;
    private ProgressDialog pd;
   private ActivitySalesReturn2019Binding mBinding;
    private SalesReturn_2019_Adapter mSalesReturn_2019_adapter;
    private String outletName, outletID;

    public static List<SalesReturn2019SelectedProduct> sSalesReturn2019SelectedProducts= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sales_return_2019);

        mContext = this;
        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.sales_return);
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
         setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sessionManager = new SessionManager(this);
        pd = new ProgressDialog(this);
        databaseHandler = new DatabaseHandler(this);
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(RootApiEndpointInterface.class);



        mBinding.rvProduct.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
       // mBinding.rvProduct.setLayoutManager(new GridLayoutManager(mContext,2, RecyclerView.VERTICAL, false));


        mBinding.srlProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.srlProduct.setRefreshing(false);
                List<ProductsItem> productsItemList = databaseHandler.getProduct(0,0);
                mSalesReturn_2019_adapter.setProductsItemList(productsItemList);
            }
        });
        mBinding.srlProduct.setRefreshing(true);

        getProducts();

        mBinding.etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(0, 0, s.toString());
                    Log.d("tareq_test" , "size "+productsItemList.size());
                    mSalesReturn_2019_adapter.setProductsItemList(productsItemList);

                } else {
                    List<ProductsItem> productsItemList = databaseHandler.getProduct(0,0);
                    mSalesReturn_2019_adapter.setProductsItemList(productsItemList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();


        mSalesReturn_2019_adapter.notifyDataSetChanged(); //we are Notify to update recycler view cz if we delete any Item from cart should be live in this activity also
        // Restore state
      //  mBinding.rvProduct.getLayoutManager().onRestoreInstanceState(recyclerViewState); //we are restoring recycler position
        updateBadge();

    }

    public void getProducts() {
        mBinding.srlProduct.setRefreshing(false);
        List<ProductsItem> productsItemList = databaseHandler.getProduct(0,0);
       mSalesReturn_2019_adapter =  new SalesReturn_2019_Adapter(mContext,  productsItemList,this);
        mBinding.rvProduct.setAdapter(mSalesReturn_2019_adapter);

    }

    TextView textCartItemCount;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

       updateBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }
    public void updateBadge() {

        if (textCartItemCount != null) {
            if (sSalesReturn2019SelectedProducts.size() == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(sSalesReturn2019SelectedProducts.size(), 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        //clear selected Item list

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //clear selected Item lis
        finish();
       // startActivity(new Intent(SalesReturn_2019_Activity.this, ActivityCart.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
               /* Intent intent = new Intent(SalesReturn_2019_Activity.this, ActivityCart.class);
                intent.putExtra("outletID", outletID);
                intent.putExtra("outletName", outletName);

                //  recyclerViewState = mBinding.rvProduct.getLayoutManager().onSaveInstanceState(); //before moving to another activity store recycler state

                startActivity(intent);*/
                return true;
            case R.id.action_cart:

                if (!sSalesReturn2019SelectedProducts.isEmpty()) {
                    onBackPressed();
                    /*Intent intent_C = new Intent(SalesReturn_2019_Activity.this, ActivityCart.class);
                    intent_C.putExtra("outletID", outletID);
                    intent_C.putExtra("outletName", outletName);

                  //  recyclerViewState = mBinding.rvProduct.getLayoutManager().onSaveInstanceState(); //before moving to another activity store recycler state

                    startActivity(intent_C);*/
                } else {
                    Toast.makeText(mContext, "No product selected yet.\nPlease select some product first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSetSelectedProduct(SalesReturn2019SelectedProduct selectedProduct) {
        for (Iterator<SalesReturn2019SelectedProduct> iterator = sSalesReturn2019SelectedProducts.iterator(); iterator.hasNext(); ) {
            SalesReturn2019SelectedProduct selected = iterator.next();
            if (selected.getProductName().equalsIgnoreCase(selectedProduct.getProductName()) && selected.getProductId()==selectedProduct.getProductId()) {
                Log.d("tareq_test", "product matched" + selected.getProductName());
                iterator.remove();
            }
        }

        sSalesReturn2019SelectedProducts.add(selectedProduct);

        Log.d("tareq_test" , "Selected "+new Gson().toJson(sSalesReturn2019SelectedProducts));
        mSalesReturn_2019_adapter.notifyDataSetChanged();
        updateBadge();
    }

    @Override
    public synchronized void onRemoveSelectedProduct(SalesReturn2019SelectedProduct selectedProduct) {
            sSalesReturn2019SelectedProducts.remove(selectedProduct);
            updateBadge();

    }
}

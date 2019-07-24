package com.humaclab.selliscope.activity;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import static com.humaclab.selliscope.activity.OrderActivity.selectedProductList;
import static com.humaclab.selliscope.sales_return.SalesReturn_2019_Activity.sSalesReturn2019SelectedProducts;


import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.adapters.SelectedProductRecyclerAdapter;
import com.humaclab.selliscope.databinding.ActivityCartBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;
import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.pos_sdk.model.PosModel;
import com.humaclab.selliscope.sales_return.SalesReturn_2019_Activity;
import com.humaclab.selliscope.sales_return.db.ReturnProductDatabase;
import com.humaclab.selliscope.sales_return.db.ReturnProductEntity;
import com.humaclab.selliscope.sales_return.model.post.JsonMemberReturn;
import com.humaclab.selliscope.sales_return.model.post.SalesReturn2019Response;
import com.humaclab.selliscope.sales_return.model.post.SalesReturn2019SelectedProduct;
import com.humaclab.selliscope.sales_return.model.post.SalesReturnPostBody;
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass;
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
    Double salesReturnTotal = 0.0;
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

    ReturnProductDatabase returnProductDatabase ;

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



        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    binding.tvGrandTotal.setText(String.valueOf(
                            total -salesReturnTotal - Double.parseDouble(s.toString())
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

        binding.btnOrder.setOnClickListener(v -> orderNow());

        // for pos printer
       /* binding.printBtn.setOnClickListener(v->{
            print();
            startActivity(new Intent(ActivityCart.this, PosActivity.class));
        });*/


        returnProductDatabase = (ReturnProductDatabase) ReturnProductDatabase.getInstance(getApplicationContext());


    }

    private void updateTotal() {
        total=0d;
        salesReturnTotal=0d;
        //For showing total amount
        for (SelectedProductHelper selectedProduct : selectedProductList) {
          //this Segment Used for calculation of total price without promotion discount
            //  total += Double.valueOf(selectedProduct.getTotalPrice());
            //this Segment Used for calculation of total price with promotion discount
            Double t= Double.valueOf(selectedProduct.getTppromotionGrandPrice().isEmpty()?"0":selectedProduct.getTppromotionGrandPrice());
            total += t ;
        }

        for(SalesReturn2019SelectedProduct salesReturn2019SelectedProduct: sSalesReturn2019SelectedProducts){
            salesReturnTotal+=salesReturn2019SelectedProduct.getProductTotal();
        }

        binding.tvTotal.setText(String.format("%s - %s", String.valueOf(total), String.valueOf(salesReturnTotal)));

        if (!binding.etDiscount.getText().toString().equals("")){
            binding.tvGrandTotal.setText(String.valueOf(
                    total - salesReturnTotal- Double.parseDouble(binding.etDiscount.getText().toString())
            ));
        }else{
            binding.tvGrandTotal.setText(String.valueOf(
                    total - salesReturnTotal));
        }
    }

    public  static PosModel sPosModel;
    public void print(){
        PosModel.Builder posModelBuilder = new PosModel.Builder();
        posModelBuilder.withOutletName(outletName);
        posModelBuilder.withNumber("01234634763");
        posModelBuilder.withReceipt("D12455");
        posModelBuilder.withCashier("d23545");
        posModelBuilder.withCustomerName("D_Tareq");
        posModelBuilder.withCustomerAddr("D Uttara Dhaka 1230");

        List<PosModel.Product> productList = new ArrayList<>();

        for (SelectedProductHelper selectedProduct : selectedProductList) {

            PosModel.Product product = new PosModel.Product();

            product.p_Name = selectedProduct.getProductName();

            product.p_Quantity = Double.parseDouble(selectedProduct.getProductQuantity());

            product.p_Rate =Double.parseDouble(selectedProduct.getProductPrice());
            product.p_C_Amount = Double.parseDouble(selectedProduct.getTpDiscount());
            product.p_Net = Double.parseDouble(selectedProduct.getTotalPrice())- Double.parseDouble(selectedProduct.getTpDiscount());

            productList.add(product);
        }
        posModelBuilder.withMProducts(productList);

        //Total Quantity
        double totalQty=0;
        for (PosModel.Product product :productList ) {
            totalQty += product.p_Quantity;
        }

        posModelBuilder.withTotal_quantity(totalQty);

        //Total Invoice
        double invoiceTotal = 0;
        for (PosModel.Product product :productList ) {
            invoiceTotal += product.p_Rate;
        }
        posModelBuilder.withInvTotal(invoiceTotal);

        //Total Net Amount
        double netAmount = 0;
        for (PosModel.Product product :productList ) {
            netAmount += product.p_Net;
        }
        posModelBuilder.withNetAmount(netAmount);

        //Total Commission Amount
        double totalCAmount = 0;
        for(PosModel.Product product :productList){
            totalCAmount += product.p_C_Amount;
        }
        totalCAmount+=Double.parseDouble(binding.etDiscount.getText().toString()); //extra discount
        posModelBuilder.withTotal_C_Amount(totalCAmount);

        posModelBuilder.withPaytype(PosModel.PAYTYPE.Cash);
        posModelBuilder.withPayOrder("d24567");
        posModelBuilder.withAmount(netAmount);

        posModelBuilder.withTotalPaid(0);
        posModelBuilder.withDue(0);
        sPosModel= posModelBuilder.build();

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
            newOrder.orderTotal = Double.parseDouble(String.valueOf(total));

            if (binding.etDiscount.getText().toString().equals("")) {
                newOrder.discount = 0.0;
            } else {
                newOrder.discount =  Double.parseDouble(binding.etDiscount.getText().toString());
                newOrder.orderGrandTotal = total- Double.parseDouble(binding.etDiscount.getText().toString());
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

            Log.d("tareq_test" , "Order: " + new Gson().toJson(addNewOrder));

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

                            if(sSalesReturn2019SelectedProducts.size()!=0){
                                pd.show();
                                assert response.body() != null;
                                postSalesReturn(apiService,response.body().result.order.id,Integer.parseInt(outletID));
                            }else {

                                Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                finish();
                            }
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

                int order_return_id = (int) (Math.random() * 10000 + 1);
                databaseHandler.setOrder(addNewOrder, order_return_id); //for random number between 1 to 100

                if(sSalesReturn2019SelectedProducts.size()>0) {

                    new Thread(() -> {
                        for (SalesReturn2019SelectedProduct selectedProduct : sSalesReturn2019SelectedProducts) {
                            returnProductDatabase.returnProductDao().insertReturnProduct(new ReturnProductEntity.Builder()
                                    .withOrder_return_id(order_return_id)
                                    .withProduct_id(selectedProduct.getProductId())
                                    .withCause(selectedProduct.getReason())
                                    .withGodown_id(1)
                                    .withOutlet_id(Integer.parseInt(outletID))
                                    .withQuantity((int) Math.round(selectedProduct.getProductQty()))
                                    .withRate(selectedProduct.getProductRate())
                                    .withSku(selectedProduct.getProductSKU())
                                    .withVariant_row(selectedProduct.getProductVariantRow())
                                    .withReturn_date(CurrentTimeUtilityClass.getCurrentTimeStamp())
                                    .build());


                        }
                        Log.d("tareq_test" , "Sales return added to database");
                        pd.dismiss();
                        sSalesReturn2019SelectedProducts.clear();
                    }).start();
                }

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
            Log.d("tareq_test" , "exceptin in order "+ e.getMessage());
            e.printStackTrace();
        }
    }


    void postSalesReturn(SelliscopeApiEndpointInterface apiService, int orderId, int outletId){

        for (SalesReturn2019SelectedProduct salesreturnProduct:sSalesReturn2019SelectedProducts) {
            salesreturnProduct.setGodown_id(1); //fixed for sometime
            salesreturnProduct.setReturn_date( CurrentTimeUtilityClass.getCurrentTimeStamp());
        }

        JsonMemberReturn jsonMemberReturn=new JsonMemberReturn();
            jsonMemberReturn.setOrderId(orderId);
            jsonMemberReturn.setOutletId(outletId);
            jsonMemberReturn.setProducts(sSalesReturn2019SelectedProducts);

        SalesReturnPostBody salesReturnPostBody = new SalesReturnPostBody();
        salesReturnPostBody.setJsonMemberReturn(jsonMemberReturn);

        Log.d("tareq_test" , "Sales Return body: "+ new Gson().toJson(salesReturnPostBody));
            apiService.postSalesReturn(salesReturnPostBody).enqueue(new Callback<SalesReturn2019Response>() {
                @Override
                public void onResponse(Call<SalesReturn2019Response> call, Response<SalesReturn2019Response> response) {
                   pd.dismiss();
                    if(response.isSuccessful()){
                        Log.d("tareq_test" , "Salesreturn successfull");
                        Toast.makeText(ActivityCart.this, "Sales return successfully", Toast.LENGTH_LONG).show();
                        sSalesReturn2019SelectedProducts.clear();
                        Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        finish();
                    }else{
                        Log.d("tareq_test" , "Salesreturn failed"+ response.code());
                    }
                }

                @Override
                public void onFailure(Call<SalesReturn2019Response> call, Throwable t) {
                pd.dismiss();
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotal();
        selectedProductRecyclerAdapter.updateCart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
               // startActivity(new Intent(ActivityCart.this, OrderActivity.class));
                return true;

            case R.id.action_return:
                Intent intent = new Intent(ActivityCart.this, SalesReturn_2019_Activity.class);
                intent.putExtra("outletID", outletID);
                intent.putExtra("outletName", outletName);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRemoveSelectedProduct(Object selectedProduct) {
        Log.d("tareq_test" , "Sales Return Itme removed");
        try {

            total=0d;
            salesReturnTotal=0d;

            for (SelectedProductHelper selectedProductHelper_ : selectedProductList) {
                total += Double.valueOf(selectedProductHelper_.getTotalPrice());
            }

            for(SalesReturn2019SelectedProduct salesReturn2019SelectedProduct: sSalesReturn2019SelectedProducts){
                salesReturnTotal += salesReturn2019SelectedProduct.getProductTotal();
            }

            binding.tvTotal.setText(String.format("%s - %s", String.valueOf(total), String.valueOf(salesReturnTotal)));

            if (!binding.etDiscount.getText().toString().equals("")) {
                binding.tvGrandTotal.setText(String.valueOf(
                        total - salesReturnTotal- Double.parseDouble(binding.etDiscount.getText().toString())
                ));
            } else {
                binding.tvGrandTotal.setText(String.valueOf(total));
            }
            selectedProductRecyclerAdapter.notifyDataSetChanged();
        }catch (Exception ex){

        }
    }
}

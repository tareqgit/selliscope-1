package com.humaclab.selliscope.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.activity.ActivityCart;
import com.humaclab.selliscope.activity.OutletActivity;
import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.sales_return.db.ReturnProductDatabase;
import com.humaclab.selliscope.sales_return.db.ReturnProductEntity;
import com.humaclab.selliscope.sales_return.model.JsonMemberReturn;
import com.humaclab.selliscope.sales_return.model.SalesReturn2019Response;
import com.humaclab.selliscope.sales_return.model.SalesReturn2019SelectedProduct;
import com.humaclab.selliscope.sales_return.model.SalesReturnPostBody;
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.humaclab.selliscope.sales_return.SalesReturn_2019_Activity.sSalesReturn2019SelectedProducts;

/**
 * Created by leon on 20/12/17.
 */

public class InternetConnectivityChangeReceiver extends BroadcastReceiver {
    private Context context;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;
    ReturnProductDatabase returnProductDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
        this.sessionManager = new SessionManager(context);
        this.apiService = SelliscopeApplication.getRetrofitInstance(
                this.sessionManager.getUserEmail(),
                this.sessionManager.getUserPassword(),
                false)
                .create(SelliscopeApiEndpointInterface.class);

        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || mobile.isConnected()) {
            if (databaseHandler.getOrder().size() != 0) {
                Log.d("tareq_test", "Uploading order to server");
                returnProductDatabase = (ReturnProductDatabase) ReturnProductDatabase.getInstance(context);

                uploadOrderToServer();
            } else {
                Log.d("tareq_test", "No offline order is available.");
            }
            Log.d("tareq_test", "Network: Network available.");
//            Toast.makeText(context, "Network available", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("tareq_test", "Network: Network disabled.");
//            Toast.makeText(context, "Network disabled", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadOrderToServer() {
        List<AddNewOrder> addNewOrderList = databaseHandler.getOrder();

        for (final AddNewOrder addNewOrder : addNewOrderList) {
            Log.d("tareq_test", "offline" + new Gson().toJson(addNewOrder));

            Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
            call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                @Override
                public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                    Log.d("tareq_test", "Offline order: Order completed." + response.code());
                    if (response.code() == 201) {
                        databaseHandler.removeOrder(addNewOrder.newOrder.outletId);
                        Log.d("tareq_test", "Offline order: Order removed.");

                        new Thread(() -> {
                            if (returnProductDatabase.returnProductDao().getAllReturnProduct(addNewOrder.newOrder.products.get(0).order_return_id).size() != 0) {
                                Log.d("tareq_test", "Sales Return Item found with " + addNewOrder.newOrder.products.get(0).order_return_id + " is " + returnProductDatabase.returnProductDao().getAllReturnProduct(addNewOrder.newOrder.products.get(0).order_return_id).size());
                                assert response.body() != null;
                                postSalesReturn(apiService, response.body().result.order.id, response.body().result.order.outlet_id,addNewOrder.newOrder.products.get(0).order_return_id);
                            }
                        }).start();
                    }
                }

                @Override
                public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("tareq_test", "failed to send order update call");
                }
            });
        }
    }


    void postSalesReturn(SelliscopeApiEndpointInterface apiService, int orderId, int outletId, int order_return_id) {
        List<SalesReturn2019SelectedProduct> salesReturn2019SelectedProductList = new ArrayList<>();
        for (ReturnProductEntity salesreturnProduct : returnProductDatabase.returnProductDao().getAllReturnProduct(order_return_id)) {
            salesReturn2019SelectedProductList.add(new SalesReturn2019SelectedProduct.Builder()
                    .withProductId(salesreturnProduct.product_id)
                    .withProductQty((double) salesreturnProduct.quantity)
                    .withProductRate(salesreturnProduct.rate)
                    .withReturn_date(salesreturnProduct.return_date)
                    .withReason(salesreturnProduct.cause)
                    .withProductSKU(salesreturnProduct.sku)
                    .withProductVariantRow(salesreturnProduct.variant_row)
                    .withGodown_id(salesreturnProduct.godown_id)
                    .build()
            );
        }

        JsonMemberReturn jsonMemberReturn = new JsonMemberReturn();
        jsonMemberReturn.setOrderId(orderId);
        jsonMemberReturn.setOutletId(outletId);
        jsonMemberReturn.setProducts(salesReturn2019SelectedProductList);

        SalesReturnPostBody salesReturnPostBody = new SalesReturnPostBody();
        salesReturnPostBody.setJsonMemberReturn(jsonMemberReturn);

        Log.d("tareq_test", "Sales Return body: " + new Gson().toJson(salesReturnPostBody));
        apiService.postSalesReturn(salesReturnPostBody).enqueue(new Callback<SalesReturn2019Response>() {
            @Override
            public void onResponse(Call<SalesReturn2019Response> call, Response<SalesReturn2019Response> response) {

                if (response.isSuccessful()) {
                    Log.d("tareq_test", "Salesreturn successfull");
                    Toast.makeText(context, "Sales return successfully", Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            returnProductDatabase.returnProductDao().deleteAllReturnProduct(order_return_id);

                        }
                    }).start();


                } else {
                    Log.d("tareq_test", "Salesreturn failed" + response.code());
                }
            }

            @Override
            public void onFailure(Call<SalesReturn2019Response> call, Throwable t) {

            }
        });
    }

}

package com.humaclab.selliscope.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by leon on 20/12/17.
 */

public class InternetConnectivityChangeReceiver extends BroadcastReceiver {
    private Context context;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;

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
                    Log.d("tareq_test", "Offline order: Order completed."+response.code());
                    if (response.code() == 201) {
                        databaseHandler.removeOrder(addNewOrder.newOrder.outletId);
                        Log.d("tareq_test", "Offline order: Order removed.");
                    }
                }

                @Override
                public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("tareq_test" , "failed to send order update call");
                }
            });
        }
    }
}

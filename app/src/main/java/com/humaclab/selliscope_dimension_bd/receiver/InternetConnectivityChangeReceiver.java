package com.humaclab.selliscope_dimension_bd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.humaclab.selliscope_dimension_bd.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_dimension_bd.SelliscopeApplication;
import com.humaclab.selliscope_dimension_bd.model.AddNewOrder;
import com.humaclab.selliscope_dimension_bd.utils.DatabaseHandler;
import com.humaclab.selliscope_dimension_bd.utils.SessionManager;

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
            if (databaseHandler.getOrder().size() != 0)
                uploadOrderToServer();
            else
                Timber.e("No offline order is available.");
            Timber.e("Network: Network available.");
//            Toast.makeText(context, "Network available", Toast.LENGTH_SHORT).show();
        } else {
            Timber.e("Network: Network disabled.");
//            Toast.makeText(context, "Network disabled", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadOrderToServer() {
        List<AddNewOrder> addNewOrderList = databaseHandler.getOrder();
        for (final AddNewOrder addNewOrder : addNewOrderList) {
            Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
            call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                @Override
                public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                    Timber.e("Offline order: Order completed.");
                    if (response.code() == 201) {
                        databaseHandler.removeOrder(addNewOrder.newOrder.outletId);
                        Timber.e("Offline order: Order removed.");
                    }
                }

                @Override
                public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}

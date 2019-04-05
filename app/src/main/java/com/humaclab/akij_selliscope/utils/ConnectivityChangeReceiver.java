package com.humaclab.akij_selliscope.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.activity.ActivityLineB;
import com.humaclab.akij_selliscope.model.Order.NewOrder;
import com.humaclab.akij_selliscope.model.Order.Order;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    Realm realm = Realm.getDefaultInstance();

    private RealmHelper helper = new RealmHelper(realm);
    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                //dialog(true);
                SessionManager  sessionManager = new SessionManager(context);
                SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);

                if(!(helper.newOrderList().size() <= 0)){
                    Toast.makeText(context, helper.newOrderList().size()+"  Offline Order Available", Toast.LENGTH_SHORT).show();

                    List<NewOrder> newOrderList = helper.newOrderList();
                    Order order = new Order();
                    Order.NewOrder newOrder = new Order.NewOrder();

                    int end = newOrderList.size();
                    while(end > 0){
                        newOrder.setComment(newOrderList.get(end-1).getComment());
                        newOrder.setDiscount(newOrderList.get(end-1).getDiscount());
                        newOrder.setLatitude(newOrderList.get(end-1).getLatitude());
                        newOrder.setLongitude(newOrderList.get(end-1).getLongitude());
                        newOrder.setOutletId(newOrderList.get(end-1).getOutletId());
                        newOrder.setLine(newOrderList.get(end-1).getLine());
                        newOrder.setSlab(newOrderList.get(end-1).getSlab());
                        newOrder.setOutlet_img(newOrderList.get(end-1).getOutlet_img());
                        newOrder.setMemo_img(newOrderList.get(end-1).getMemo_img());
                        newOrder.setOrder_date(newOrderList.get(end-1).getOrder_date());
                        newOrder.setStock(newOrderList.get(end-1).getStock());
                        order.newOrder = newOrder;
                        Log.d("offlineTest", "" + new Gson().toJson(newOrder));

                        Call<Order.OrderResponse> orderResponseCall = apiService.order(order);
                        int dataSendIndex = end-1;
                        orderResponseCall.enqueue(new Callback<Order.OrderResponse>() {
                            @Override
                            public void onResponse(Call<Order.OrderResponse> call, Response<Order.OrderResponse> response) {
                                response.code();
                                if (response.code() == 201) {
                                    helper.deletRealm(dataSendIndex);
                                    //helper.uPdate(dataSendIndex);
                                    /*Toast.makeText(context, " Offline Order Success"+ dataSendIndex, Toast.LENGTH_SHORT).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            helper.deletRealm(dataSendIndex);
                                        }
                                    }, 10000);   //3 seconds*/



                                } else {
                                    Toast.makeText(context, "" + response.code() + "Offline Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Order.OrderResponse> call, Throwable t) {

                            }
                        });
                        end--;
                    }
                    //realm.deleteAll();
                }
                else {
                    Toast.makeText(context, "No Offline order Available ", Toast.LENGTH_SHORT).show();
                }

            } else {
                //dialog(false);

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}

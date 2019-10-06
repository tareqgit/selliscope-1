/*
 * Created by Tareq Islam on 9/3/19 10:17 AM
 *
 *  Last modified 9/3/19 10:17 AM
 */

package com.humaclab.lalteer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.model.AddNewOrder;
import com.humaclab.lalteer.performance.claim.model.Claim;
import com.humaclab.lalteer.performance.claim.model.ClaimPostResponse;
import com.humaclab.lalteer.room_lalteer.LalteerRoomDb;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/***
 * Created by mtita on 03,September,2019.
 */
public class UploadDataService {
    private Context context;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;
    private LalteerRoomDb lalteerRoomDb;
    private final Executor executor;

    public UploadDataService(Context context) {
        this.context = context;
        executor = Executors.newSingleThreadExecutor();
        this.databaseHandler = new DatabaseHandler(context);
        this.sessionManager = new SessionManager(context);
        this.apiService = SelliscopeApplication.getRetrofitInstance(
                this.sessionManager.getUserEmail(),
                this.sessionManager.getUserPassword(),
                false)
                .create(SelliscopeApiEndpointInterface.class);
        //For Room
        lalteerRoomDb = (LalteerRoomDb) LalteerRoomDb.getInstance(context);

    }


    public void uploadData(UploadCompleteListener uploadCompleteListener) {


        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || mobile.isConnected()) {
            if (databaseHandler.getOrder().size() != 0) {
                uploadOrderToServer(new UploadCompleteListener() {
                    @Override
                    public void uploadComplete() {
                        if (uploadCompleteListener != null) uploadCompleteListener.uploadComplete();
                    }

                    @Override
                    public void uploadFailed(String reason) {
                        if (uploadCompleteListener != null)
                            uploadCompleteListener.uploadFailed(reason);
                    }
                });
            } else {
                Log.d("tareq_test", "UploadDataService #61: uploadData:  No offline order is available.");
                if (uploadCompleteListener != null)
                    uploadCompleteListener.uploadFailed("No Offline Order Available");
            }



            executor.execute(()->{
                if(lalteerRoomDb.returnClaimDao().getClaimList().size()!=0){
                    uploadClaimsToServer(new UploadCompleteListener() {
                        @Override
                        public void uploadComplete() {
                            if (uploadCompleteListener != null) uploadCompleteListener.uploadComplete();
                        }

                        @Override
                        public void uploadFailed(String reason) {
                            if (uploadCompleteListener != null)
                                uploadCompleteListener.uploadFailed(reason);
                        }
                    });
                }else {

                    if (uploadCompleteListener != null)
                        uploadCompleteListener.uploadFailed("No Offline Claim Available");
                }
            });


            Log.d("tareq_test", "UploadDataService #64: uploadData:  Network: Network available.");
        } else {
            Log.d("tareq_test", "UploadDataService #67: uploadData: Network: Network disabled. ");

        }
    }


    private void uploadOrderToServer(UploadCompleteListener uploadCompleteListener) {
        List<AddNewOrder> addNewOrderList = databaseHandler.getOrder();
        for (AddNewOrder addNewOrder : addNewOrderList) {
            addNewOrder.newOrder.address = String.valueOf(GetAddressFromLatLang.getAddressFromLatLan(context, Double.valueOf(addNewOrder.newOrder.latitude), Double.valueOf(addNewOrder.newOrder.longitude)));
            Log.d("tareq_test", "" + new Gson().toJson(addNewOrder));
            Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
            call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                @Override
                public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                    //Timber.e("Offline order: Order completed.");
                    Log.d("tareq_test", "Offline order: Order completed");
                    if (response.code() == 200) {
                        databaseHandler.removeOrder(addNewOrder.newOrder.outletId);
                        //     Timber.e("Offline order: Order removed.");
                        Log.d("tareq_test", "Offline order: Order removed.");
                        if (uploadCompleteListener != null)
                            uploadCompleteListener.uploadComplete();
                    } else {
                        if (uploadCompleteListener != null)
                            uploadCompleteListener.uploadFailed("Order Failed Bad response:" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                    t.printStackTrace();
                    if (uploadCompleteListener != null)
                        uploadCompleteListener.uploadFailed("Order Failed Bad response:" + t.getMessage());
                }
            });
        }
    }


    private void uploadClaimsToServer(UploadCompleteListener uploadCompleteListener) {
        List<Claim> claimList = lalteerRoomDb.returnClaimDao().getClaimList();
        for (Claim claim : claimList) {
            apiService.sendClaim(claim).enqueue(new Callback<ClaimPostResponse>() {
                @Override
                public void onResponse(Call<ClaimPostResponse> call, Response<ClaimPostResponse> response) {
                    if(response.isSuccessful()){
                      executor.execute(()->  lalteerRoomDb.returnClaimDao().deleteClaim(claim));

                        if (uploadCompleteListener != null)
                            uploadCompleteListener.uploadComplete();
                    } else {
                        if (uploadCompleteListener != null)
                            uploadCompleteListener.uploadFailed("Claim send Failed Bad response:" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ClaimPostResponse> call, Throwable t) {
                    if (uploadCompleteListener != null)
                        uploadCompleteListener.uploadFailed("Claim send Failed Bad response:" + t.getMessage());
                }
            });
        }
    }


    public interface UploadCompleteListener {

        void uploadComplete();

        void uploadFailed(String reason);
    }
}

package com.humaclab.lalteer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.model.AddNewOrder;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.GetAddressFromLatLang;
import com.humaclab.lalteer.utils.SessionManager;
import com.humaclab.lalteer.utils.UploadDataService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by leon on 20/12/17.
 */

public class InternetConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            UploadDataService uploadDataService = new UploadDataService(context);
            uploadDataService.uploadData(new UploadDataService.UploadCompleteListener() {
                @Override
                public void uploadComplete() {
                    Log.d("tareq_test", "InternetConnectivityChangeReceiver #40: uploadComplete:  Upload Complete");
                }

                @Override
                public void uploadFailed(String reason) {
                    Log.d("tareq_test", "InternetConnectivityChangeReceiver #45: uploadFailed: "+ reason);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

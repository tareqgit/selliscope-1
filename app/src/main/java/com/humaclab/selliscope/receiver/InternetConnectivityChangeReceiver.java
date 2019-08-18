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
import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.sales_return.db.ReturnProductDatabase;
import com.humaclab.selliscope.sales_return.db.ReturnProductEntity;
import com.humaclab.selliscope.sales_return.model.post.JsonMemberReturn;
import com.humaclab.selliscope.sales_return.model.post.SalesReturn2019Response;
import com.humaclab.selliscope.sales_return.model.post.SalesReturn2019SelectedProduct;
import com.humaclab.selliscope.sales_return.model.post.SalesReturnPostBody;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.SessionManager;
import com.humaclab.selliscope.utils.UpLoadDataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leon on 20/12/17.
 */

public class InternetConnectivityChangeReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {

        UpLoadDataService upLoadDataService = new UpLoadDataService(context);
        upLoadDataService.uploadData(new UpLoadDataService.UploadCompleteListener() {
            @Override
            public void uploadComplete() {
                Log.d("tareq_test", "Upload complete");

            }

            @Override
            public void uploadFailed(String reason) {
                Log.d("tareq_test", "" + reason);
              }
        });
    }



}

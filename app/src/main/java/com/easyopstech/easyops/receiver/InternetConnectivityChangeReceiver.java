package com.easyopstech.easyops.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.easyopstech.easyops.service.InspectionWorker;
import com.easyopstech.easyops.service.LocationSyncerWorker;
import com.easyopstech.easyops.utils.UpLoadDataService;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 20/12/17.
 */

public class InternetConnectivityChangeReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {

        UpLoadDataService upLoadDataService = new UpLoadDataService(context);
        upLoadDataService.uploadOrder_and_ReturnData(new UpLoadDataService.UploadCompleteListener() {
            @Override
            public void uploadComplete() {
                Log.d("tareq_test", "Upload complete");
            }

            @Override
            public void uploadFailed(String reason) {
                Log.d("tareq_test", "" + reason);
              }
        });

        Log.d("tareq_test", "InternetConnectivityChangeReceiver #66: onReceive:  ");

        OneTimeWorkRequest oneTimeWorkRequestTrackong= new OneTimeWorkRequest.Builder(LocationSyncerWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .addTag("sync tracking data")
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(context)
                    .beginUniqueWork("sync tracking", ExistingWorkPolicy.KEEP, oneTimeWorkRequestTrackong )
                    .enqueue();


        OneTimeWorkRequest oneTimeWorkRequest= new OneTimeWorkRequest.Builder(InspectionWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .addTag("sync inspections")
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 60, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(context)
                .beginUniqueWork("sync inspection", ExistingWorkPolicy.KEEP, oneTimeWorkRequest)
                .enqueue();
    }



}

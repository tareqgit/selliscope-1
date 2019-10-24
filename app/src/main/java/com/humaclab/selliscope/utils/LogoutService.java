package com.humaclab.selliscope.utils;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.humaclab.selliscope.service.OrderWorker;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/***
 * Created by mtita on 24,October,2019.
 */
public class LogoutService {

    Context mContext;

    public LogoutService(Context context) {
        mContext = context;
    }

    public void logoutDataUpload(OnWorkManagerJobListener managerJobListener){
        OneTimeWorkRequest oneTimeWorkRequest= new OneTimeWorkRequest.Builder(OrderWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .addTag("sync order data")
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(mContext)
                .beginUniqueWork("sync order", ExistingWorkPolicy.KEEP, oneTimeWorkRequest )
                .enqueue();

        WorkManager.getInstance(mContext).getWorkInfoById(oneTimeWorkRequest.getId()).addListener(()-> {
            managerJobListener.onJobDone();
        } ,Executors.newSingleThreadExecutor());
    }

    public interface OnWorkManagerJobListener{
        void onJobDone();
    }
}

package com.easyopstech.easyops.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.easyopstech.easyops.utils.UpLoadDataService;

/***
 * Created by mtita on 24,October,2019.
 */
public class OrderWorker extends Worker {

    UpLoadDataService mUpLoadDataService;

    public OrderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mUpLoadDataService= new UpLoadDataService(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        final Result[] result = new Result[1];

        mUpLoadDataService.uploadInspectionData(new UpLoadDataService.UploadCompleteListener() {
            @Override
            public void uploadComplete() {
                result[0] = Result.success(); //this is what I want to do
            }

            @Override
            public void uploadFailed(String reason) {
                result[0] = Result.failure(); //this is what I want to do
            }
        });

        return result[0];
    }
}

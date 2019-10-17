package com.humaclab.selliscope.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.humaclab.selliscope.utils.UpLoadDataService;

/***
 * Created by mtita on 15,October,2019.
 */
public class InspectionWorker extends Worker {

    private UpLoadDataService mUpLoadDataService;
    public InspectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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

/*
 * Created by Tareq Islam on 10/16/19 3:34 PM
 *
 *  Last modified 10/16/19 3:34 PM
 */

package com.humaclab.lalteer.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;



/***
 * Created by mtita on 16,October,2019.
 */
public class OutstandingPaymentWorker extends Worker {

    UploadOutstandingPaymentService mUploadOutstandingPaymentService;


    public OutstandingPaymentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        mUploadOutstandingPaymentService = new UploadOutstandingPaymentService(context);


    }

    @NonNull
    @Override
    public Result doWork() {
        final Result[] result = new Result[1];
        mUploadOutstandingPaymentService.uploadOutstandingPayment(new UploadOutstandingPaymentService.UploadCompleteListener() {
            @Override
            public void onUploadComplete() {
                result[0]= Result.success();
            }

            @Override
            public void onUploadFailed() {
                result[0] = Result.failure(); //this is what I want to do
            }
        });

        return result[0];
    }



}

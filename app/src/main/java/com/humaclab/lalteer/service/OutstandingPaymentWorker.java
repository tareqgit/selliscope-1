/*
 * Created by Tareq Islam on 10/16/19 3:34 PM
 *
 *  Last modified 10/16/19 3:34 PM
 */

package com.humaclab.lalteer.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.activity.OutletDetailsActivity;
import com.humaclab.lalteer.model.outstanding_payment.OutstandingPaymentBody;
import com.humaclab.lalteer.model.outstanding_payment.OutstandingPostResponse;

import com.humaclab.lalteer.utils.SessionManager;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/***
 * Created by mtita on 16,October,2019.
 */
public class OutstandingPaymentWorker extends Worker {

    SelliscopeApiEndpointInterface apiService;
    int outletId;
    OutstandingPaymentBody outstandingPaymentBody;


    public OutstandingPaymentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        SessionManager sessionManager = new SessionManager(context);
         apiService=SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        outletId= getInputData().getInt("outletId",0);
        outstandingPaymentBody= new Gson().fromJson(getInputData().getString("paymentBody"),OutstandingPaymentBody.class);

    }

    @NonNull
    @Override
    public Result doWork() {

        UploadOutstandingPayment(outstandingPaymentBody, outletId, new ApiCallListener() {
            @Override
            public Result onSuccessFul() {
                return Result.success();
            }

            @Override
            public Result onFailed() {
                return Result.failure();
            }

            @Override
            public Result onRetry() {
                return Result.retry();
            }
        });

        return null;
    }



    private Result UploadOutstandingPayment(OutstandingPaymentBody outstandingPaymentBody, int outletId,  ApiCallListener apiCallListener) {
        CompositeDisposable compositeDisposable= new CompositeDisposable();
        apiService.postOutstandingPayments(outletId, outstandingPaymentBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<OutstandingPostResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<OutstandingPostResponse> response) {
                        if(response.isSuccessful()){
                            if(response.body()!=null){
                                if(!response.body().isError()){
                                    Log.d("tareq_test", "OutletDetailsActivity #167: onSuccess:  " );

                                    apiCallListener.onSuccessFul();
                                    compositeDisposable.dispose();

                                }else{
                                 apiCallListener.onFailed();
                                }
                            }else{
                                apiCallListener.onFailed();
                            }
                        }else{
                            apiCallListener.onFailed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("" +getClass().getName(), "Something Wrong! "+ e.getMessage());
                        apiCallListener.onRetry();
                    }
                });
        return null;
    }


    interface ApiCallListener{
        Result onSuccessFul();
        Result onFailed();
        Result onRetry();
    }
}

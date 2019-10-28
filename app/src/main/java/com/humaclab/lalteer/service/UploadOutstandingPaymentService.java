/*
 * Created by Tareq Islam on 10/28/19 10:18 AM
 *
 *  Last modified 10/28/19 10:18 AM
 */

package com.humaclab.lalteer.service;

import android.content.Context;
import android.util.Log;

import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.outstanding_payment.model.OutstandingPaymentBody;
import com.humaclab.lalteer.outstanding_payment.model.OutstandingPostResponse;
import com.humaclab.lalteer.room_lalteer.LalteerRoomDb;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/***
 * Created by mtita on 28,October,2019.
 */
public class UploadOutstandingPaymentService {

    private Context mContext;
    private SelliscopeApiEndpointInterface apiService;

    public UploadOutstandingPaymentService(Context context) {
        mContext = context;
        SessionManager sessionManager= new SessionManager(context);
        this.apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

    }

    public void uploadOutstandingPayment(UploadCompleteListener uploadCompleteListener ) {

        LalteerRoomDb lalteerRoomDb= (LalteerRoomDb) LalteerRoomDb.getInstance(mContext);

        List<OutstandingPaymentBody> outstandingPaymentBodyList= new ArrayList<>();
        outstandingPaymentBodyList=lalteerRoomDb.returnOutstandingPaymentDao().getAllOutstandingPaymentList();


        for ( OutstandingPaymentBody outstandingPaymentBody: outstandingPaymentBodyList    ) {
            apiService.postOutstandingPayments(outstandingPaymentBody.getOutlet_id(), outstandingPaymentBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Response<OutstandingPostResponse>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Response<OutstandingPostResponse> response) {
                            if(response.isSuccessful()){
                                if(response.body()!=null){
                                    if(!response.body().isError()){
                                        Log.d("tareq_test", "OutletDetailsActivity #167: onSuccess:  " );
                                        //   Toast.makeText(mContext, ""+ response.body().getResult(), Toast.LENGTH_SHORT).show();
                                        //   binding.etPayment.getEditText().setText("");
                                        //  loadOutstandingDue();

                                        Executors.newSingleThreadExecutor().execute(()-> {
                                            lalteerRoomDb.returnOutstandingPaymentDao().deleteOutstandingPayment(outstandingPaymentBody);
                                        });
                                    }
                                }else{
                                    Log.d("" +getClass().getName(), "Server Error");
                                    if(uploadCompleteListener!=null) uploadCompleteListener.onUploadFailed();
                                }
                            }else{
                                Log.d("" +getClass().getName(), "Server Error");
                                if(uploadCompleteListener!=null) uploadCompleteListener.onUploadFailed();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("" +getClass().getName(), "Something Wrong! "+ e.getMessage());
                            if(uploadCompleteListener!=null) uploadCompleteListener.onUploadFailed();
                        }
                    });
        }

        if(lalteerRoomDb.returnOutstandingPaymentDao().getAllOutstandingPaymentList().size()==0){
            if(uploadCompleteListener!=null) uploadCompleteListener.onUploadComplete();
        }


    }


    public interface UploadCompleteListener{
        void onUploadComplete();
        void onUploadFailed();
    }
}

/*
 * Created by Tareq Islam on 9/30/19 2:22 PM
 *
 *  Last modified 9/30/19 2:22 PM
 */

package com.humaclab.lalteer.performance.claim.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.activity.OutletActivity;
import com.humaclab.lalteer.performance.claim.ClaimActivity;
import com.humaclab.lalteer.performance.claim.model.Claim;
import com.humaclab.lalteer.performance.claim.model.ClaimPostResponse;
import com.humaclab.lalteer.performance.claim.model.ClaimResponse;
import com.humaclab.lalteer.performance.claim.model.ReasonItem;
import com.humaclab.lalteer.room_lalteer.LalteerRoomDb;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Created by mtita on 30,September,2019.
 */
public class ClaimRepository {

    private LiveData<List<ReasonItem>> mClaimResponseLiveData;
    private SelliscopeApiEndpointInterface apiService;
    private Context mContext;
    private LalteerRoomDb lalteerRoomDb;
    private final Executor executor;

    public ClaimRepository(Context context) {
        mContext = context;

        executor = Executors.newSingleThreadExecutor();
        //For Room
        lalteerRoomDb = (LalteerRoomDb) LalteerRoomDb.getInstance(context);

        //For Api
        SessionManager sessionManager = new SessionManager(context);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

    }


    public LiveData<List<ReasonItem>> getClaimResponseLiveData() {
        getReasonItems();
        return mClaimResponseLiveData = lalteerRoomDb.returnClaimReasonsDao().getAllReason();
    }

    private LiveData<List<ReasonItem>> getReasonItems() {
        final MutableLiveData<List<ReasonItem>> mutableLiveData = new MutableLiveData<>();

        apiService.getClaimReasons().enqueue(new Callback<ClaimResponse>() {
            @Override
            public void onResponse(Call<ClaimResponse> call, Response<ClaimResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        //    mutableLiveData.setValue(response.body().getResult());
                        executor.execute(() -> lalteerRoomDb.returnClaimReasonsDao().deleteAll());

                        for (ReasonItem reasonItem : response.body().getResult()) {
                            executor.execute(() -> {
                                lalteerRoomDb.returnClaimReasonsDao().insert(reasonItem);
                            });
                        }

                    }
                } else {
                    Log.d("tareq_test", "ClaimRepository #56: onResponse:  Server Error");
                }

            }

            @Override
            public void onFailure(Call<ClaimResponse> call, Throwable t) {
                Log.d("tareq_test", "ClaimRepository #64: onFailure:  Server Error");
                Toast.makeText(mContext, "Server Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return mutableLiveData;
    }


    public void postClaim(Claim claim, ClaimPostListener claimPostListener) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {


            apiService.sendClaim(claim).enqueue(new Callback<ClaimPostResponse>() {
                @Override
                public void onResponse(Call<ClaimPostResponse> call, Response<ClaimPostResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        Toast.makeText(mContext, "Claim Send Successful", Toast.LENGTH_SHORT).show();

                        Log.d("tareq_test", "ClaimRepository #114: onResponse:  success");

                        claimPostListener.onComplete();

                    }
                    if (response.code() >= 500 && response.code() <= 599) {
                        executor.execute(() -> lalteerRoomDb.returnClaimDao().insertClaim(claim));
                        Log.d("tareq_test", "ClaimRepository #118: onResponse:  stored in db");

                        Toast.makeText(mContext, "Claim queued Successful", Toast.LENGTH_SHORT).show();
                        claimPostListener.onComplete();

                    }
                }

                @Override
                public void onFailure(Call<ClaimPostResponse> call, Throwable t) {
                    executor.execute(() -> lalteerRoomDb.returnClaimDao().insertClaim(claim));
                    Log.d("tareq_test", "ClaimRepository #125: onResponse:  stored in db");
                    Toast.makeText(mContext, "Claim queued Successful", Toast.LENGTH_SHORT).show();
                    claimPostListener.onComplete();
                }
            });
        } else {
            executor.execute(() -> lalteerRoomDb.returnClaimDao().insertClaim(claim));
            Log.d("tareq_test", "ClaimRepository #130: onResponse:  stored in db");
            Toast.makeText(mContext, "Claim queued Successful", Toast.LENGTH_SHORT).show();
            claimPostListener.onComplete();
        }


    }

    public interface ClaimPostListener {
        void onComplete();


    }

}

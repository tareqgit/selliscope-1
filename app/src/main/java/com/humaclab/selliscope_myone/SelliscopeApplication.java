package com.humaclab.selliscope_myone;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;


import com.humaclab.selliscope_myone.outlet_paging.api.OutletItem;
import com.humaclab.selliscope_myone.outlet_paging.api.OutletSearchResponse;
import com.humaclab.selliscope_myone.utils.Constants;
import com.humaclab.selliscope_myone.utils.HttpAuthInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Leon on 3/6/2017.
 */

public class SelliscopeApplication extends Application {
    private static Retrofit retrofitInstance;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /**
     * @param email
     * @param password
     * @return retrofitInstance
     */

    public static Retrofit getRetrofitInstance(String email, String password, boolean isForLogin) {
        if (retrofitInstance == null || isForLogin) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpAuthInterceptor(email, password))
                    .connectTimeout(100000, TimeUnit.SECONDS)
                    .readTimeout(100000, TimeUnit.SECONDS)
//                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofitInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }



    public static void searchRepos(SelliscopeApiEndpointInterface service, String query, int page, int itemsPerPage, final ApiCallback apiCallback) {
        Log.d("tareq_test", String.format("searchRepos: query: %s, page: %s, itemsPerPage: %s", query, page, itemsPerPage));

        //Framing the query to be searched only in the Name and description fields of the
        //Github repositories
        String apiQuery = query ;

        //Executing the API asynchronously
        service.searchOutlets(apiQuery, page).enqueue(new Callback<OutletSearchResponse>() {
            //Called when the response is received
            @Override
            public void onResponse(@Nullable Call<OutletSearchResponse> call, @NonNull Response<OutletSearchResponse> response) {
                Log.d("tareq_test", "onResponse: Got response: " + response);
                if (response.isSuccessful()) {
                    //Retrieving the Repo Items when the response is successful
                    List<OutletItem> items;
                    if (response.body() != null) {
                        items = response.body().getResult().getOutlet();
                    } else {
                        items = new ArrayList<>();
                    }
                    //Pass the result to the callback
                    apiCallback.onSuccess(items);
                } else {
                    //When the response is unsuccessful
                    try {
                        //Pass the error to the callback
                        apiCallback.onError(response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        Log.e("tareq_test", "onResponse: Failed while reading errorBody: ", e);
                    }
                }
            }

            //Called on Failure of the request
            @Override
            public void onFailure(@Nullable Call<OutletSearchResponse> call, @NonNull Throwable t) {
                Log.d("tareq_test", "onFailure: Failed to get data");
                //Pass the error to the callback
                apiCallback.onError(t.getMessage() != null ?
                        t.getMessage() : "Unknown error");
            }
        });
    }



    public interface ApiCallback {
        /**
         * Callback invoked when the Search Repo API Call
         * completed successfully
         *
         * @param items The List of Repos retrieved for the Search done
         */
        void onSuccess(List<OutletItem> items);

        /**
         * Callback invoked when the Search Repo API Call failed
         *
         * @param errorMessage The Error message captured for the API Call failed
         */
        void onError(String errorMessage);
    }
}

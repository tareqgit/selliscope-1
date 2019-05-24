package com.humaclab.selliscope_myone;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;


import com.humaclab.selliscope_myone.utils.Constants;
import com.humaclab.selliscope_myone.utils.HttpAuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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



}

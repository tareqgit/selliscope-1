package com.humaclab.selliscope;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.humaclab.selliscope.Utils.ApiLinks;
import com.humaclab.selliscope.Utils.HttpAuthInterceptor;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by Nahid on 3/5/2017.
 */

public class SelliscopeApplication extends Application {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static Retrofit retrofitInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Stetho.initializeWithDefaults(this);
        Fabric.with(this, new Crashlytics());
    }


    /**
     * @param email
     * @param password
     * @return retofitInstance
     */

    public static Retrofit getRetrofitInstance(String email, String password, boolean
            isForLogin) {
        if (retrofitInstance == null || isForLogin) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpAuthInterceptor(email, password))
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(ApiLinks.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofitInstance;
    }
}

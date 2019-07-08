package com.humaclab.selliscope;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.humaclab.selliscope.receiver.GpsLocationBroadcastReceiver;
import com.humaclab.selliscope.utils.Constants;
import com.humaclab.selliscope.utils.HttpAuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by Leon on 3/6/2017.
 */

public class SelliscopeApplication extends Application {
    private static Retrofit retrofitInstance;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
    private static boolean activityVisible;


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
                    .addInterceptor(new HttpAuthInterceptor(email.toLowerCase(), password))
                    .connectTimeout(100000, TimeUnit.SECONDS)
                    .readTimeout(100000, TimeUnit.SECONDS)
                    .addNetworkInterceptor(new StethoInterceptor())
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
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        createNotificationChannel();
        Stetho.initializeWithDefaults(this);
      /*  Fabric.with(this, new Crashlytics());*/

        //receiver for having gps
        registerReceiver(GpsLocationBroadcastReceiver.getInstance(), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }


    public static final String CHANNEL_ID="testServiceChannel";

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Test Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.humaclab.selliscope.receiver.GpsLocationBroadcastReceiver;
import com.humaclab.selliscope.utility_db.db.UtilityDatabase;
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
    public static final String CHANNEL_ID = "testServiceChannel";

    public static boolean developer=false;

    private static Retrofit retrofitInstance;
    private FirebaseAnalytics mFirebaseAnalytics;


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
                    .connectTimeout(70, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)

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
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        createNotificationChannel();
        Stetho.initializeWithDefaults(this);

        //receiver for having gps
        registerReceiver(GpsLocationBroadcastReceiver.getInstance(), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        UtilityDatabase utilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(getApplicationContext());

    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Test Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

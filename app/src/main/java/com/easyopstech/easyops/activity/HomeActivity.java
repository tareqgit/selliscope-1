package com.easyopstech.easyops.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.RootApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.easyopstech.easyops.BuildConfig;
import com.easyopstech.easyops.LocationMonitoringService;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.fragment.DashboardFragment;
import com.easyopstech.easyops.fragment.PerformanceFragment;
import com.easyopstech.easyops.fragment.TargetFragment;
import com.easyopstech.easyops.model.app_version.AppVersion;
import com.easyopstech.easyops.model.diameter.DiameterResponse;
import com.easyopstech.easyops.receiver.InternetConnectivityChangeReceiver;

import com.easyopstech.easyops.service.NotificationHandler;
import com.easyopstech.easyops.utility_db.db.UtilityDatabase;
import com.easyopstech.easyops.utils.Constants;
import com.easyopstech.easyops.utils.DatabaseHandler;
import com.easyopstech.easyops.utils.LoadLocalIntoBackground;
import com.easyopstech.easyops.utils.LogoutService;
import com.easyopstech.easyops.utils.ReloadDataService;
import com.easyopstech.easyops.utils.SessionManager;
import com.easyopstech.easyops.utils.UpLoadDataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
import static com.easyopstech.easyops.R.id.content_fragment;
import static io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public enum FRAGMENT_TAGS {
        Target_Fragment, Dashboard_Fragment, Performance_Fragment
    }

    public static ScheduledExecutorService schedulerForMinute, schedulerForHour, schedulerForDataUpdate;
    public static BroadcastReceiver internetBroadcastReciever = new InternetConnectivityChangeReceiver();
    private FragmentManager fragmentManager;
    private SessionManager sessionManager;
    private RootApiEndpointInterface apiService;
    private ProgressDialog pd;
    private LoadLocalIntoBackground loadLocalIntoBackground;
    private Context context;

    private BroadcastReceiver broadcastReceiver;

    private static final String TAG = HomeActivity.class.getSimpleName();
    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedService = false;

    private static final int JOB_ID = 101;
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    private String manufacturer;
    private String modelHandset;

    //This receiver working as a medium for two , First from LocationMonitoringService and Second from GpsLocationBroadcastReceiver
    private BroadcastReceiver gpsBroadcastStateListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //this accuracy is from LocationMonitoringService
            float accuracy = intent.getFloatExtra("accuracy", 0);
            if (accuracy <= 17) {
                ((ImageView) findViewById(R.id.gps_signal_image)).setImageResource(R.drawable.ic_high_signal);
            } else if (accuracy <= 60) {
                ((ImageView) findViewById(R.id.gps_signal_image)).setImageResource(R.drawable.ic_mid_signal);
            } else if (accuracy <= 200) {
                ((ImageView) findViewById(R.id.gps_signal_image)).setImageResource(R.drawable.ic_low_signal);
            } else {
                ((ImageView) findViewById(R.id.gps_signal_image)).setImageResource(R.drawable.ic_no_signal);
            }

            //this state from GpsLocationBroadcastReceiver
            boolean state = intent.getBooleanExtra("state", true);
            if (state) {
                ((ImageView) findViewById(R.id.gps_signal_image)).setImageResource(R.drawable.ic_high_signal);
            } else {
                ((ImageView) findViewById(R.id.gps_signal_image)).setImageResource(R.drawable.ic_low_signal);
            }
        }
    };

    private LocalBroadcastManager mLocalBroadcastManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For Bangla
        LoadLocale();
        setContentView(R.layout.activity_home);
        context = this;

        //region Set Alarm for dataUpload
        setAlarmForDataUpload();
        //endregion


        get_FCM_Token(); ///Should be called  Once app open for the first time as this token naturally static if it changes new token can be found from FCMNotificationService-> newToken().
        // [END handle_data_extras]


        //region Battery Percentage
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        Log.d("tareq_test", "HomeActivity #189: onCreate:  "+ batLevel);
        //endregion


        //
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        Constants.BASE_URL = sharedPreferences.getString("BASE_URL", Constants.BASE_URL);


        displayGpsSignalRequest(); //for showing gps request

        //Location Receiver from LocationMonitoringService
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        // From  LocationMonitoringService to this activity
        mLocalBroadcastManager.registerReceiver(
                gpsBroadcastStateListener, new IntentFilter("GPS")
        );
        // From GpsLocationBroadcaster to this activity
        mLocalBroadcastManager.registerReceiver(gpsBroadcastStateListener, new IntentFilter("GpsState"));


        manufacturer = android.os.Build.MANUFACTURER;
        modelHandset = Build.MODEL;

        //registerReceiver();
        ////I think this is redundent now ///    sendLocationDataService = new SendLocationDataService();

        sessionManager = new SessionManager(this);


        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        loadLocalIntoBackground = new LoadLocalIntoBackground(this);

  /*      AsyncTask.execute(() -> loadLocalIntoBackground.loadAll(new LoadLocalIntoBackground.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                Log.d("tareq_test", "Data load all complete");
            }

            @Override
            public void onLoadFailed(String msg) {
                Log.d("tareq_test", "Data load all failed: " + msg);
            }
        }));
*/

        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(RootApiEndpointInterface.class);
        pd = new ProgressDialog(this);
//        CheckAppUpdated.checkAppUpdate(this);
        /*
        this segment helps you to check gps setting
       gpsControl();
       */
        Toolbar toolbar = findViewById(R.id.toolbar);
        //    toolbar.setTitle("Target Plan");
        //  TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        //   toolbarTitle.setVisibility(View.VISIBLE);


        setSupportActionBar(toolbar);
        // Obtain the FirebaseAnalytics instance.
        //region For Testing Utility Database
        UtilityDatabase utilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(getApplicationContext());


        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = formatDate.format(d);
        SimpleDateFormat formathour = new SimpleDateFormat("HH", Locale.ENGLISH);
        String hour = formathour.format(d);


        fragmentManager = getSupportFragmentManager();
        getFragment(TargetFragment.class, FRAGMENT_TAGS.Target_Fragment);
      /*  TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: {
                        toolbar.setTitle("Target Plan");
                        getFragment(TargetFragment.class);
                        break;
                    }
                    case 1: {
                        toolbar.setTitle("Dashboard");
                        getFragment(DashboardFragment.class);
                        break;
                    }
                    case 2: {
                        toolbar.setTitle("Performance");
                        getFragment(PerformanceFragment.class);
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    */

        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_target:
                    toolbar.setTitle(getString(R.string.target_plan));
                    getFragment(TargetFragment.class, FRAGMENT_TAGS.Target_Fragment);

                    return true;
                case R.id.navigation_dashboard:
                    toolbar.setTitle(getString(R.string.dashboard));
                    getFragment(DashboardFragment.class, FRAGMENT_TAGS.Dashboard_Fragment);
                    return true;
                case R.id.navigation_performance:
                    toolbar.setTitle(getString(R.string.performance));
                    getFragment(PerformanceFragment.class, FRAGMENT_TAGS.Performance_Fragment);
                    return true;
            }
            return false;
        });


        //For log and track user
    /*    Answers.getInstance().logCustom(new CustomEvent("User Login")
                .putCustomAttribute("User Name", sessionManager.getUserDetails().get("userName"))
                .putCustomAttribute("User Email", sessionManager.getUserEmail())
                .putCustomAttribute("Used version", "Version - " + BuildConfig.VERSION_NAME)
        );*/
        //For log and track user

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView userName = navigationView.getHeaderView(0)
                .findViewById(R.id.tv_user_name);


        ImageView profilePicture = navigationView.getHeaderView(0)
                .findViewById(R.id.iv_profile_pic);

        userName.setText(sessionManager.getUserDetails().get("userName"));
        Glide.with(this)
                .load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .thumbnail(0.1f)

                .placeholder(R.drawable.ic_employee)
                .centerCrop()
                .transform(new CircleCrop())
                .into(profilePicture);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tv_selliscope_version = navigationView.getHeaderView(0).findViewById(R.id.tv_selliscope_version);
        tv_selliscope_version.setText(String.format("Version - %s", BuildConfig.VERSION_NAME));

        //For getting diameter
        setDiameter();

        //loading Data into background
       /* schedulerForMinute = Executors.newSingleThreadScheduledExecutor();
        schedulerForMinute.scheduleAtFixedRate(() -> {

            Log.v("Running threads", "Thread running in background for updating products and outlets");
            loadLocalIntoBackground.loadOutlet();


        }, 0, 1, TimeUnit.MINUTES);*/



      /*  schedulerForHour = Executors.newSingleThreadScheduledExecutor();
        schedulerForHour.scheduleAtFixedRate(() -> {
           // runOnUiThread(new Runnable() {
          //      @Override
           //     public void run() {
            Log.v("Running threads", "Thread running in background for updating products and outlets after 30 Minutes interval");
            loadLocalIntoBackground.loadProduct(null);
   //             }
    //        });
        }, 30, 30, TimeUnit.MINUTES);*/
        //loading Data into background

        try {

            //Register internetBroadcastReciever for Internet Connectivity change
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

            registerReceiver(internetBroadcastReciever, filter);
        } catch (Exception e) {
            Log.d("tareq_test", "" + e.getMessage());
        }


        LoadappsVertion();


       /* //startService(new Intent(this, SendLocationDataService.class));


        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            Toast.makeText(context, ""+"\n Latitude : " + latitude + "\n Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );
*/

      /*  ComponentName componentName = new ComponentName(this, MyJobScheduler.class);

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);

        //builder.setMinimumLatency(1);
        //builder.setOverrideDeadline(1);
        builder.setPeriodic(15 * 60 * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);


        jobInfo = builder.build();
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);*/


        uploadDataFromLocalStorage(this);
        //initialize
/*
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            int downSpeed = nc.getLinkDownstreamBandwidthKbps();
            int upSpeed = nc.getLinkUpstreamBandwidthKbps();
            Log.d("tareq_test", "HomeActivity #416: onCreate:  "+ downSpeed +" , "+ upSpeed );
        }*/

    }

    private void setAlarmForDataUpload() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date time_at = sdf.parse("18:00:00"); //at 6pm

            Date now = Calendar.getInstance().getTime();
            String mNow = sdf.format(now);
            Date now_at = sdf.parse(mNow);


            NotificationHandler.cancelReminder(this, "notification");


            long duration = time_at.getTime() - now_at.getTime();
            if (duration < 0) {
                duration += (24 * 60 * 60 * 1000);
            }
            NotificationHandler.scheduleReminder(this, duration, createWorkInputData("Please", "Update data", 1), "notification");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //Work Manager data builder
    private Data createWorkInputData(String title, String text, int id) {
        return new Data.Builder()
                .putString(Constants.EXTRA_TITLE, title)
                .putString(Constants.EXTRA_TEXT, text)
                .putInt(Constants.EXTRA_ID, id)
                .build();
    }


    private void uploadDataFromLocalStorage(Context context) {

        if (schedulerForDataUpdate == null || schedulerForDataUpdate.isShutdown() || schedulerForDataUpdate.isTerminated()) {
            schedulerForDataUpdate = Executors.newSingleThreadScheduledExecutor();
            schedulerForDataUpdate.scheduleAtFixedRate(() -> {

                Log.d("Running threads", "Thread running in background for updating products and outlets");

                UpLoadDataService upLoadDataService = new UpLoadDataService(context);

                upLoadDataService.uploadOrder_and_ReturnData(new UpLoadDataService.UploadCompleteListener() {
                    @Override
                    public void uploadComplete() {
                        Log.d("tareq_test", "Upload complete");


                    }

                    @Override
                    public void uploadFailed(String reason) {
                        Log.e("tareq_test", "" + reason);

                    }
                });

            }, 0, 3, TimeUnit.MINUTES);
        }

    }

    private void displayGpsSignalRequest() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            Log.d("tareq_test", "on Success");
        });

        task.addOnFailureListener(this, e -> {
            Log.d("tareq_test", "OnFailed");
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS); //976 is just the reference number
                } catch (IntentSender.SendIntentException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    private void get_FCM_Token() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();


                        Log.d("FCM", "InstanceID Token: " + token);

                        //     Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //   final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        //         Toast.makeText(context, "Must turn on GPs", Toast.LENGTH_SHORT).show();
                        try {
                            int locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);

                            if (locationMode == LOCATION_MODE_HIGH_ACCURACY) {
                                //request location updates
                                Log.d("tareq_test", "High Accuracy found");
                            } else { //redirect user to settings page
                                //need high accuracy
                                Log.d("tareq_test", "need high accuracy");
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        displayGpsSignalRequest();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void welcome() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 12) {
            welcome(getString(R.string.good_morning));

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            welcome(getString(R.string.good_afternoon));

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            welcome(getString(R.string.good_evening));

        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            welcome(getString(R.string.good_night));

        } else {
            welcome(getString(R.string.good_night));
        }
    }

    private void setDiameter() {
        Call<DiameterResponse> call = apiService.getDiameter();
        call.enqueue(new Callback<DiameterResponse>() {
            @Override
            public void onResponse(Call<DiameterResponse> call, Response<DiameterResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        sessionManager.setDiameter(response.body().getDiameter().getDiameter());
                    }
                }
            }

            @Override
            public void onFailure(Call<DiameterResponse> call, Throwable t) {
                Log.e("tareq_test", "Getting diameter error");
            }
        });
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {

                    return fragment;
                }
            }
        }
        return null;
    }


    private void getFragment(Class createFragment, FRAGMENT_TAGS fragTag) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) createFragment.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragTag) {

            case Performance_Fragment:
                if (getVisibleFragment() != null && !getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Performance_Fragment.toString()))

                    fragmentTransaction.setCustomAnimations(R.anim.right_to_left, R.anim.right_out_left);
                break;
            case Target_Fragment:
                if (getVisibleFragment() != null && !getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Target_Fragment.toString()))

                    fragmentTransaction.setCustomAnimations(R.anim.left_to_right, R.anim.left_out_right);
                break;
            case Dashboard_Fragment:
                if (getVisibleFragment() != null) {
                    if (getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Target_Fragment.toString()))
                        fragmentTransaction.setCustomAnimations(R.anim.right_to_left, R.anim.right_out_left);
                    else if (getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Performance_Fragment.toString()))
                        fragmentTransaction.setCustomAnimations(R.anim.left_to_right, R.anim.left_out_right);
                }

                break;
        }

        fragmentTransaction.replace(content_fragment, fragment, fragTag.toString())
                .commitAllowingStateLoss();

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tap BACK again to Exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_logout:
                pd.setMessage("Login out...");
                pd.show();

                new LogoutService(context).logoutDataUpload(()->{
                   if(new DatabaseHandler(context).getOrder().isEmpty())
                    HomeActivity.this.deleteDatabase(Constants.databaseName);
                });

                //      jobScheduler.cancel(JOB_ID);
                //Stop Android Service for sending location to service
                stopService(new Intent(HomeActivity.this, LocationMonitoringService.class));
                mAlreadyStartedService = false;

//                if (databaseHandler.removeAll()) {
                sessionManager.logoutUser(false);

                //       schedulerForMinute.shutdownNow();
                //    schedulerForHour.shutdownNow();
                schedulerForDataUpdate.shutdown();



                /*stopService(new Intent(HomeActivity.this, SendLocationDataService.class));*/

                pd.dismiss();

                try {
                    if (internetBroadcastReciever != null)
                        unregisterReceiver(internetBroadcastReciever);
                } catch (Exception e) {
                    Log.d("tareq_test", "HomeActivity #704: onNavigationItemSelected:  " + e.getMessage());
                }

                finish();
//                }
                break;
            case R.id.nav_privacy_policy:
                startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));
                break;
            case R.id.nav_about_us:
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("About Us");
                alertDialog.setMessage("ICT Incubator,\nSoftware Technology Park (4th Floor), Janata Tower,\nKawranbazar, Dhaka 1215, Bangladesh\ninfo@humaclab.com\nMobile: +8801711505322");
                alertDialog.setMessage("Humac Lab ,\nHouse - 11, Road - 21\nSector - 04, Uttara ,\n Dhaka 1230, Bangladesh\ninfo@humaclab.com\nMobile: +8801711505322");

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            case R.id.nav_update_data:
                pd.setMessage("Offline data is Uploading to Server.\nPlease be patient....");
                pd.setCancelable(false);

                pd.show();
                UpLoadDataService upLoadDataService = new UpLoadDataService(HomeActivity.this);

                //region Upload Order Data
                pd.show();
                upLoadDataService.uploadOrder_and_ReturnData(new UpLoadDataService.UploadCompleteListener() {
                    @Override
                    public void uploadComplete() {
                        pd.dismiss();
                        Log.d("tareq_test", "Upload order data complete");
                    }

                    @Override
                    public void uploadFailed(String reason) {
                        pd.dismiss();
                        Log.e("tareq_test", "" + reason);
                        Toast.makeText(HomeActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
                    }
                });
                //endregion

                //region for uploading inspection we need to show pd again
                pd.show();
                upLoadDataService.uploadInspectionData(new UpLoadDataService.UploadCompleteListener() {
                    @Override
                    public void uploadComplete() {
                        pd.dismiss();
                        Log.d("tareq_test", "Upload inspection data complete");
                    }

                    @Override
                    public void uploadFailed(String reason) {

                        pd.dismiss();
                        runOnUiThread(()-> Toast.makeText(HomeActivity.this, "" + reason, Toast.LENGTH_SHORT).show());

                    }
                });
                //endregion



              /*  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                }, 60000);*/
                break;


            case R.id.nav_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_contact:
                final AlertDialog alertDialogContact = new AlertDialog.Builder(this).create();
                alertDialogContact.setTitle("Contact Us");
                alertDialogContact.setMessage("Email: support@humaclab.com \nMobile: 01707073175 \nSunday - Thursday \n9.00am - 6.00pm");
                alertDialogContact.setButton(DialogInterface.BUTTON_POSITIVE, "CAll", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //                       Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", "+8801707073175" ));
//                        if (ActivityCompat.checkSelfPermission((Activity)context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            Toast.makeText(context, "Permission DisApproved", Toast.LENGTH_SHORT).show();
//                            alertDialogContact.dismiss();
//                        }
//                        else {
//                            startActivity(intent);
//                            alertDialogContact.dismiss();
//                        }


                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:8801707073175"));

                        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(callIntent);


                    }
                });
                alertDialogContact.show();
                break;
            case R.id.nav_refresh_data:

                final AlertDialog alertDialogRefresh = new AlertDialog.Builder(this).create();
                alertDialogRefresh.setTitle("Confirm");
                alertDialogRefresh.setMessage("Are you sure? \n\nYou want to clear all data. ");
                alertDialogRefresh.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", (dialog, which) -> {

                    pd.setMessage("Local data is updating.\nPlease be patient....");
                    pd.setCancelable(false);
                    pd.show();

                    new ReloadDataService(context).reloadData(new ReloadDataService.ReloadDataListener() {
                        @Override
                        public void onComplete() {
                            Log.d("tareq_test", "Data refresh complete");
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Data Load Complete", Toast.LENGTH_SHORT).show());

                            pd.dismiss();
                        }

                        @Override
                        public void onFailed(String reason) {
                            pd.dismiss();
                            Log.d("tareq_test", "Data refresh failed" + reason);
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "failed: " + reason + "\n Please Try Again", Toast.LENGTH_SHORT).show());

                        }
                    });


                });


                if (!alertDialogRefresh.isShowing()) alertDialogRefresh.show();
                break;

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        try {
            mLocalBroadcastManager.unregisterReceiver(gpsBroadcastStateListener);
            unregisterReceiver(internetBroadcastReciever); //for internet
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            stopService(locationServiceIntent); /*The latter may seem rather peculiar:
             why do we want to stop exactly the service that we want to keep alive?
              Because if we do not stop it, the service will die with our app.
              Instead, by stopping the service, we will force the service to call its own onDestroy which will force it to recreate itself after the app is dead.*/
        } catch (Exception e) {
            Log.e("tareq_test", "Stop location Service intent: " + e.getMessage());
        }
        super.onDestroy();
        if (sessionManager.isLoggedIn()) {

            //Stop For On Destroy to send server address
        /*    stopService(new Intent(this, LocationMonitoringService.class));
            mAlreadyStartedService = false;
*/
            //     stopService(new Intent(HomeActivity.this, SendLocationDataService.class));
            //unregisterReceiver(broadcastReceiver);


            //as xiomi kills foreground service
            if (manufacturer.equals("Xiaomi") || modelHandset.equals("Primo GH7i"))
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    //For Bangla
    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        //Locale.setDefault(locale);
        //Phone Default Language is English
        Locale localeEN = new Locale("en");
        Locale.setDefault(localeEN);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences sharedPreferencesLanguage = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesLanguage.edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }


    //Load
    public void LoadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }


    private void LoadappsVertion() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(RootApiEndpointInterface.class);
            Call<AppVersion> call = apiService.getAppsversion();
            call.enqueue(new Callback<AppVersion>() {
                @Override
                public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                    if (response.code() == 200) {

                        System.out.println("APPS VERTION " + new Gson().toJson(response.body()));

                        int serverVersion = Integer.parseInt(response.body().getResult().getVersionCode());
                        int appVersion = BuildConfig.VERSION_CODE;
                        if (serverVersion > appVersion) {
                            updateDialog(response.body().getResult().getVersionName(), response.body().getResult().getUrl());
                        }


                    }

                }

                @Override
                public void onFailure(Call<AppVersion> call, Throwable t) {
                    Log.d("tareq_test", "Loading App version error");
                }

            });
        }
    }

    private void updateDialog(String version, final String link) {


        final AlertDialog builder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.information, null);
        builder.setView(dialogView);

        TextView tv_details_information = dialogView.findViewById(R.id.tv_details_information);
        TextView tv_title = dialogView.findViewById(R.id.tv_title_name);


        tv_details_information.setText("A new version is available on PlayStore .Please Update it");
        tv_title.setText("Update Available " + version);


        Button iv_info_cancel = dialogView.findViewById(R.id.btn_update);
        iv_info_cancel.setOnClickListener(v -> {
            builder.dismiss();


            stopService(new Intent(HomeActivity.this, LocationMonitoringService.class));
            mAlreadyStartedService = false;


            sessionManager.logoutUser(false);
            if (schedulerForMinute != null && schedulerForHour != null) {
                schedulerForMinute.shutdownNow();
                schedulerForHour.shutdownNow();

            }
            schedulerForDataUpdate.shutdown();
            //stopService(new Intent(HomeActivity.this, SendLocationDataService.class));
            HomeActivity.this.deleteDatabase(Constants.databaseName);
            try {
                mLocalBroadcastManager.unregisterReceiver(gpsBroadcastStateListener);
                unregisterReceiver(internetBroadcastReciever);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();

            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();


        welcome();
/*        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                LoadappsVertion();
            }
        }, 2000);*/

        //Start Android Service for sending location to service
        startStep1();
    }

    private void welcome(String message) {
        TextView welcome_text = findViewById(R.id.textview_greeting);
        welcome_text.setVisibility(View.VISIBLE); //as this is disabled for other uses
        welcome_text.setText(String.format("%s, %s", message, sessionManager.getUserDetails().get("userName")));

    }

    /**
     * This method is responsible to register receiver with NETWORK_CHANGE_ACTION.
     * */
    /*private void registerReceiver()
    {


        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    Bundle extras = intent.getExtras();

                    NetworkInfo info = (NetworkInfo) extras
                            .getParcelable("networkInfo");

                    NetworkInfo.State state = info.getState();
                    Log.d("InternalBroadcast", info.toString() + " "
                            + state.toString());

                    if (state == NetworkInfo.State.CONNECTED) {

                        Toast.makeText(context, "CONNECTED", Toast.LENGTH_SHORT).show();
                       // onNetworkUp();

                    } else if (state == NetworkInfo.State.CONNECTED) {
                        Toast.makeText(context, "not CONNECTED", Toast.LENGTH_SHORT).show();
                       // onNetworkDown();

                    }

                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }

    }*/
    /*public  void gpsControl(){

        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //GPS enabled

        }

        else{
            //GPS disabled
            ImportentFunction.displayPromptForEnablingGPS(this);
        }
    }*/

    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getApplicationContext(), "NO GOOGLE PLAYSTORE AVAILABLE", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        alartDialogBuilder(); //create Internet Connection Error Alert Dialog

        //region StartZoombie LocationService
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        //endregion


        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.


        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {

        if (!dialogInternetConnectionError.isShowing())
            dialogInternetConnectionError.show();
    }

    AlertDialog dialogInternetConnectionError;

    private void alartDialogBuilder() {
        AlertDialog.Builder internetConnectionErrorDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        internetConnectionErrorDialogBuilder.setTitle("Error");
        internetConnectionErrorDialogBuilder.setMessage("No Internet ");
        String positiveText = "Refresh";
        internetConnectionErrorDialogBuilder.setPositiveButton(positiveText,
                (dialog, which) -> {


                    //Block the Application Execution until user grants the permissions
                    if (startStep2(dialog)) {

                        //Now make sure about location permission.
                        if (checkPermissions()) {

                            //Step 2: Start the Location Monitor Service
                            //Everything is there to start the service.
                            startStep3();
                        } else if (!checkPermissions()) {
                            requestPermissions();
                        }

                    }
                });
        dialogInternetConnectionError = internetConnectionErrorDialogBuilder.create();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */

    Intent locationServiceIntent;

    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.
        LocationMonitoringService locationMonitoringService = new LocationMonitoringService();
        locationServiceIntent = new Intent(this, locationMonitoringService.getClass());
        if (!mAlreadyStartedService) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                startForegroundService(locationServiceIntent);
            }

            //mMsgView.setText("msg_location_service_started");

            //Start location sharing service to app server.........
            startService(locationServiceIntent);
            mAlreadyStartedService = true;
            //Ends................................................

        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    /*    @Override
        public void onStop() {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (pm.isScreenOn()) {
                Log.e("ok","isScreenOn");
            }
            else {
                Log.e("ok","isScreenoff");
            }
            super.onStop();
        }*/


}

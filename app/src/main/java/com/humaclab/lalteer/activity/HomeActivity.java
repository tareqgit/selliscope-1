package com.humaclab.lalteer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.humaclab.lalteer.BuildConfig;
import com.humaclab.lalteer.LocationMonitoringService;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.fragment.DashboardFragment;
import com.humaclab.lalteer.fragment.PerformanceFragment;
import com.humaclab.lalteer.fragment.TargetFragment;
import com.humaclab.lalteer.model.app_version.AppVersion;
import com.humaclab.lalteer.model.diameter.DiameterResponse;
import com.humaclab.lalteer.receiver.InternetConnectivityChangeReceiver;
import com.humaclab.lalteer.utils.Constants;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.LoadLocalIntoBackground;
import com.humaclab.lalteer.utils.SessionManager;
import com.humaclab.lalteer.utils.UploadDataService;


import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
import static com.humaclab.lalteer.R.id.content_fragment;
import static io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static ScheduledExecutorService schedulerForMinute, schedulerForHour, schedulerForDataUpdate;
    public static BroadcastReceiver receiver = new InternetConnectivityChangeReceiver();
    private FragmentManager fragmentManager;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private ProgressDialog pd;
    private LoadLocalIntoBackground loadLocalIntoBackground;
    private static final String TAG = HomeActivity.class.getSimpleName();
    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedService = false;

    /*for disposing all Observer at a single time*/
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadLocale();
        setContentView(R.layout.activity_home);
        manufacturer = android.os.Build.MANUFACTURER;
        //
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        Constants.BASE_URL = sharedPreferences.getString("BASE_URL", Constants.BASE_URL);

        // Toast.makeText(this, ""+ Constants.BASE_URL, Toast.LENGTH_SHORT).show();

        displayGpsSignalRequest(); //for showing gps request


        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        databaseHandler = new DatabaseHandler(this);
        loadLocalIntoBackground = new LoadLocalIntoBackground(this, mCompositeDisposable);

      /*  loadLocalIntoBackground.loadAll(new LoadLocalIntoBackground.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {

            }

            @Override
            public void onLoadFailed(String reason) {

            }
        });*/

         pd = new ProgressDialog(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getResources().getString(R.string.home));
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        getFragment(TargetFragment.class);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: {
                        getFragment(TargetFragment.class);
                        break;
                    }
                    case 1: {
                        getFragment(DashboardFragment.class);
                        break;
                    }
                    case 2: {
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

        /*//For log and track user
        Answers.getInstance().logCustom(new CustomEvent("User Login")
                .putCustomAttribute("User Name", sessionManager.getUserDetails().get("userName"))
                .putCustomAttribute("User Email", sessionManager.getUserEmail())
                .putCustomAttribute("Used version", "Version - " + BuildConfig.VERSION_NAME)
        );
        //For log and track user*/

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
                .placeholder(R.drawable.default_profile_pic)
                .into(profilePicture);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tv_selliscope_version = navigationView.getHeaderView(0).findViewById(R.id.tv_selliscope_version);
        tv_selliscope_version.setText("Version - " + BuildConfig.VERSION_NAME);

        //For getting diameter
        setDiameter();

       /* //loading Data into background
        schedulerForMinute = Executors.newSingleThreadScheduledExecutor();
        schedulerForMinute.scheduleAtFixedRate(new Runnable() {
            public void run() {
               *//* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                 *//*
                Log.v("Running threads", "Thread running in background for updating products and outlets");
                loadLocalIntoBackground.loadOutlet(null);
            }
            //});
            // }
        }, 0, 1, TimeUnit.MINUTES);*/

      /*  schedulerForHour = Executors.newSingleThreadScheduledExecutor();
        schedulerForHour.scheduleAtFixedRate(new Runnable() {
            public void run() {
             *//*   runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
              *//*
                Log.v("Running threads", "Thread running in background for updating products and outlets after 30 Minutes interval");
                loadLocalIntoBackground.loadProduct(null);
                loadLocalIntoBackground.loadCategory(null);
                loadLocalIntoBackground.loadBrand(null);
                *//*    }
                });
 *//*
            }
        }, 30, 30, TimeUnit.MINUTES);*/
        //loading Data into background

        try {
            //Register receiver for Internet Connectivity change
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            Log.d("tareq_test", "" + e.getMessage());
        }

        // For Shared Preferrence to Language
        /*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = settings.getString("LANG", "");
        if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }*/
        LoadappsVertion();
        uploadDataFromLocalStorage(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //    final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
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

    private void setDiameter() {
       apiService.getDiameter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
               .subscribe(new SingleObserver<Response<DiameterResponse>>() {
                   @Override
                   public void onSubscribe(Disposable d) {
                       if(mCompositeDisposable!=null) mCompositeDisposable.add(d);
                   }

                   @Override
                   public void onSuccess(Response<DiameterResponse> response) {
                       if (response.code() == 200) {
                           sessionManager.setDiameter(response.body().getDiameter().getDiameter());
                       }
                   }

                   @Override
                   public void onError(Throwable e) {

                   }
               });

    }

    private void getFragment(Class createFragment) {
        try {
            Fragment fragment = (Fragment) createFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(content_fragment, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

                stopService(new Intent(HomeActivity.this, LocationMonitoringService.class));
                mAlreadyStartedService = false;

//                if (databaseHandler.removeAll()) {
                sessionManager.logoutUser(false);
              if(schedulerForMinute!=null)  schedulerForMinute.shutdownNow();
              if(schedulerForHour!=null)  schedulerForHour.shutdownNow();



                /*stopService(new Intent(HomeActivity.this, SendLocationDataService.class));*/
                HomeActivity.this.deleteDatabase(Constants.databaseName);
                pd.dismiss();
                unregisterReceiver(receiver);
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
                pd.setMessage("Offline data is Uploading to Server. \nPlease be patient....");
                pd.setCancelable(false);
                pd.show();
                UploadDataService upLoadDataService = new UploadDataService(HomeActivity.this);
                upLoadDataService.uploadData(new UploadDataService.UploadCompleteListener() {
                    @Override
                    public void uploadComplete() {
                        pd.dismiss();
                        Log.d("tareq_test", "Upload data complete");
                    }

                    @Override
                    public void uploadFailed(String reason) {
                        pd.dismiss();
                        Log.e("tareq_test", "" + reason);
                        runOnUiThread(()->{
                            Toast.makeText(HomeActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
                        });

                    }
                });
                break;

            case R.id.nav_refresh_data:
                final AlertDialog alertDialogRefresh = new AlertDialog.Builder(this).create();
                alertDialogRefresh.setTitle("Confirm");
                alertDialogRefresh.setMessage("Are you sure? \n\nYou want to clear all data. ");
                alertDialogRefresh.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", (dialog, which) -> {

                    pd.setMessage("Local data is updating.\nPlease be patient....");
                    pd.setCancelable(false);
                    pd.show();

                 /*   loadLocalIntoBackground.updateAllData();
                    new Handler().postDelayed(() -> pd.dismiss(), 60000);*/
                    AsyncTask.execute(() -> loadLocalIntoBackground.loadAll(new LoadLocalIntoBackground.LoadCompleteListener() {
                        @Override
                        public void onLoadComplete() {

                            Log.d("tareq_test", "Data load all complete");
                            Toast.makeText(HomeActivity.this, "Loading Completed: " ,Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        }

                        @Override
                        public void onLoadFailed(String msg) {
                            Log.d("tareq_test", "Data load all failed: " + msg);
                            Toast.makeText(HomeActivity.this, "Loading Failed: " + msg, Toast.LENGTH_SHORT).show();

                            pd.dismiss();

                        }
                    }));

                });


                if (!alertDialogRefresh.isShowing()) alertDialogRefresh.show();
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
                alertDialogContact.setMessage("Email: support@humaclab.com \nMobile: 01707073175");
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
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String manufacturer;
    Intent locationServiceIntent;

    @Override
    protected void onDestroy() {
        try {
            stopService(locationServiceIntent); /*The latter may seem rather peculiar:
             why do we want to stop exactly the service that we want to keep alive?
              Because if we do not stop it, the service will die with our app.
              Instead, by stopping the service, we will force the service to call its own onDestroy which will force it to recreate itself after the app is dead.*/
        } catch (Exception e) {
            Log.e("tareq_test", "Stop location Service intent: " + e.getMessage());
        }
        super.onDestroy();

        unregisterReceiver(receiver); //extra

        if (sessionManager.isLoggedIn()) {

            if (manufacturer.equals("Xiaomi"))
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();

        welcome();
        startStep1();
    }

    private void uploadDataFromLocalStorage(Context context) {

        if (schedulerForDataUpdate == null || schedulerForDataUpdate.isShutdown() || schedulerForDataUpdate.isTerminated()) {
            schedulerForDataUpdate = Executors.newSingleThreadScheduledExecutor();
            schedulerForDataUpdate.scheduleAtFixedRate(() -> {

                Log.d("Running threads", "Thread running in background for updating products and outlets");

                UploadDataService upLoadDataService = new UploadDataService(context);

                upLoadDataService.uploadData(new UploadDataService.UploadCompleteListener() {
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

    private void LoadappsVertion() {
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        apiService.getAppsversion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<AppVersion>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<AppVersion> response) {
                        if (response.code() == 200) {

                            Log.d("tareq_test", "APPS VERTION " + new Gson().toJson(response.body()));

                            int serverVersion = Integer.parseInt(response.body().getResult().getVersionCode());
                            int appVersion = BuildConfig.VERSION_CODE;
                            if (serverVersion > appVersion) {
                                updateDialog(response.body().getResult().getVersionName(), response.body().getResult().getUrl());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(HomeActivity.this, "Loading Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    /*    Call<AppVersion> call = apiService.getAppsversion();
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                if (response.code() == 200) {

                    Log.d("tareq_test", "APPS VERTION " + new Gson().toJson(response.body()));

                    int serverVersion = Integer.parseInt(response.body().getResult().getVersionCode());
                    int appVersion = BuildConfig.VERSION_CODE;
                    if (serverVersion > appVersion) {
                        updateDialog(response.body().getResult().getVersionName(), response.body().getResult().getUrl());
                    }
                }

            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Loading Error", Toast.LENGTH_SHORT).show();
            }

        });*/

    }


    private void updateDialog(String version, final String link) {


        final AlertDialog builder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.information, null);
        builder.setView(dialogView);

        TextView tv_details_information = (TextView) dialogView.findViewById(R.id.tv_details_information);
        TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title_name);


        tv_details_information.setText("A new version is available on PlayStore. Please Update it");
        tv_title.setText("Update Available " + version);


        Button iv_info_cancel = dialogView.findViewById(R.id.btn_update);
        iv_info_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();


                stopService(new Intent(HomeActivity.this, LocationMonitoringService.class));
                mAlreadyStartedService = false;


                sessionManager.logoutUser(false);
                schedulerForMinute.shutdownNow();
                schedulerForHour.shutdownNow();
                //stopService(new Intent(HomeActivity.this, SendLocationDataService.class));
                HomeActivity.this.deleteDatabase(Constants.databaseName);
                unregisterReceiver(receiver);
                finish();

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    //Load
    public void LoadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

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

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("ERror");
        builder.setMessage("No Internet ");

        String positiveText = "Refresh";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


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
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        LocationMonitoringService locationMonitoringService = new LocationMonitoringService();
        locationServiceIntent = new Intent(this, locationMonitoringService.getClass());
        if (!mAlreadyStartedService) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //    Intent intent2 = new Intent(this, LocationMonitoringService.class);
                startForegroundService(locationServiceIntent);
            }

            //mMsgView.setText("msg_location_service_started");

            //Start location sharing service to app server.........
            //   Intent intent = new Intent(this, LocationMonitoringService.class);
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

    private void welcome() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            welcome(getString(R.string.GoodMorning));

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            welcome(getString(R.string.GoodAfternoon));

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            welcome(getString(R.string.GoodEvening));

        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            welcome(getString(R.string.GoodNight));

        }
    }

    private void welcome(String message) {

        TextView welcome_text = findViewById(R.id.welcome_text);
        welcome_text.setVisibility(View.VISIBLE);
        welcome_text.setText(message + ", " + sessionManager.getUserDetails().get("userName"));

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

}

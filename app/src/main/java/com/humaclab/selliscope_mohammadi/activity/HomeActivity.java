package com.humaclab.selliscope_mohammadi.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope_mohammadi.BuildConfig;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_mohammadi.SelliscopeApplication;
import com.humaclab.selliscope_mohammadi.fragment.DashboardFragment;
import com.humaclab.selliscope_mohammadi.fragment.TargetFragment;
import com.humaclab.selliscope_mohammadi.model.Diameter.DiameterResponse;
import com.humaclab.selliscope_mohammadi.receiver.InternetConnectivityChangeReceiver;
import com.humaclab.selliscope_mohammadi.service.SendLocationDataService;
import com.humaclab.selliscope_mohammadi.utils.CheckAppUpdated;
import com.humaclab.selliscope_mohammadi.utils.Constants;
import com.humaclab.selliscope_mohammadi.utils.DatabaseHandler;
import com.humaclab.selliscope_mohammadi.utils.LoadLocalIntoBackground;
import com.humaclab.selliscope_mohammadi.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.humaclab.selliscope_mohammadi.R.id.content_fragment;

@RuntimePermissions
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fragmentManager;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private ProgressDialog pd;
    private LoadLocalIntoBackground loadLocalIntoBackground;
    private ScheduledExecutorService schedulerForMinute, schedulerForHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sessionManager = new SessionManager(this);
        databaseHandler = new DatabaseHandler(this);
        loadLocalIntoBackground = new LoadLocalIntoBackground(this);
        loadLocalIntoBackground.loadAll();
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        pd = new ProgressDialog(this);
        CheckAppUpdated.checkAppUpdate(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.home));
        setSupportActionBar(toolbar);
        HomeActivityPermissionsDispatcher.startUserTrackingServiceWithCheck(this);
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
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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
        Picasso.get()
                .load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .into(profilePicture);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tv_selliscope_version = navigationView.getHeaderView(0).findViewById(R.id.tv_selliscope_version);
        tv_selliscope_version.setText("Version - " + BuildConfig.VERSION_NAME);

        //For getting diameter
        setDiameter();

        //loading Data into background
        schedulerForMinute = Executors.newSingleThreadScheduledExecutor();
        schedulerForMinute.scheduleAtFixedRate(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Running threads", "Thread running in background for updating products and outlets");
                        loadLocalIntoBackground.loadOutlet(false);
                    }
                });
            }
        }, 0, 1, TimeUnit.MINUTES);
        schedulerForHour = Executors.newSingleThreadScheduledExecutor();
        schedulerForHour.scheduleAtFixedRate(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Running threads", "Thread running in background for updating products and outlets after 30 Minutes interval");
                        loadLocalIntoBackground.loadProduct();
                    }
                });
            }
        }, 30, 30, TimeUnit.MINUTES);
        //loading Data into background

        //Register receiver for Internet Connectivity change
        registerReceiver(new InternetConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void setDiameter() {
        Call<DiameterResponse> call = apiService.getDiameter();
        call.enqueue(new Callback<DiameterResponse>() {
            @Override
            public void onResponse(Call<DiameterResponse> call, Response<DiameterResponse> response) {
                if (response.code() == 200) {
                    sessionManager.setDiameter(response.body().getDiameter().getDiameter());
                }
            }

            @Override
            public void onFailure(Call<DiameterResponse> call, Throwable t) {

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


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void startUserTrackingService() {
        startService(new Intent(
                HomeActivity.this, SendLocationDataService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        // NOTE: delegate the permission handling to generated method
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation(final PermissionRequest request) {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, permissions, 404);
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForLocation() {
        Toast.makeText(this, "Go Away", Toast.LENGTH_SHORT).show();
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
            case R.id.nav_logout: {
                pd.setMessage("Login out...");
                pd.show();
//                if (databaseHandler.removeAll()) {
                sessionManager.logoutUser();
                schedulerForMinute.shutdownNow();
                schedulerForHour.shutdownNow();
                stopService(new Intent(getApplicationContext(), SendLocationDataService.class));
                getApplicationContext().deleteDatabase(Constants.databaseName);
                pd.dismiss();
                finish();
//                }
                break;
            }
            case R.id.nav_privacy_policy: {
                startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));
                break;
            }
            case R.id.nav_about_us: {
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("About Us");
                alertDialog.setMessage("ICT Incubator,\nSoftware Technology Park (4th Floor), Janata Tower,\nKawranbazar, Dhaka 1215, Bangladesh\ninfo@humaclab.com\nMobile: +8801711505322");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            }
            case R.id.nav_check_update: {
                CheckAppUpdated.checkAppUpdate(this);
                break;
            }
            case R.id.nav_update_data: {
                pd.setMessage("Local data is updating.\nPlease be patient....");
                pd.setCancelable(false);
                pd.show();
                loadLocalIntoBackground.updateAllData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                }, 60000);
                break;
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessionManager.isLoggedIn()) {
            Timber.d("Home Activity stopped.");
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }
}

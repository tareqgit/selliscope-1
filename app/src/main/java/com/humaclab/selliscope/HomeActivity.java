package com.humaclab.selliscope;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.humaclab.selliscope.Utils.SessionManager;
import com.squareup.picasso.Picasso;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.humaclab.selliscope.R.id.content_fragment;

@RuntimePermissions
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager;
    TextView toolbarTitle;
    TextView userName;
    ImageView profilePicture;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sessionManager = new SessionManager(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.home));
        setSupportActionBar(toolbar);
        HomeActivityPermissionsDispatcher.startUserTrackingServiceWithCheck(this);
        fragmentManager = getSupportFragmentManager();
        getFragment(TargetFragment.class);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        userName = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.tv_user_name);
        profilePicture = (ImageView) navigationView.getHeaderView(0)
                .findViewById(R.id.iv_profile_pic);
        userName.setText(sessionManager.getUserDetails().get("userName"));
        Picasso.with(this)
                .load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .into(profilePicture);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getFragment(Class createFragment) {
        try {
            Fragment fragment = (Fragment) createFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(content_fragment, fragment)
                    .commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
        new AlertDialog.Builder(this)
                .setMessage("GPS permission is required")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                sessionManager.logoutUser();
                finish();
                break;
            }
            case R.id.nav_privacy_policy: {
                startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));
                break;
            }
            case R.id.nav_about_us: {
                showAboutUs();
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAboutUs() {
        new MaterialDialog.Builder(this)
                .title(R.string.title)
                .content(R.string.content)
                .positiveText(R.string.agree)
                .show();
    }

    public boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}

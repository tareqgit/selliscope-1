package com.humaclab.selliscope_nepal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
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
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_nepal.Service.SendLocationDataService;
import com.humaclab.selliscope_nepal.Utils.CheckAppUpdated;
import com.humaclab.selliscope_nepal.Utils.Constants;
import com.humaclab.selliscope_nepal.Utils.DatabaseHandler;
import com.humaclab.selliscope_nepal.Utils.SessionManager;
import com.humaclab.selliscope_nepal.model.BrandResponse;
import com.humaclab.selliscope_nepal.model.CategoryResponse;
import com.humaclab.selliscope_nepal.model.Outlets;
import com.humaclab.selliscope_nepal.model.VariantProduct.GodownItem;
import com.humaclab.selliscope_nepal.model.VariantProduct.ProductsItem;
import com.humaclab.selliscope_nepal.model.VariantProduct.VariantProductResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
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

import static com.humaclab.selliscope_nepal.R.id.content_fragment;

@RuntimePermissions
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fragmentManager;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler databaseHandler;
    private ProgressDialog pd;
    private ScheduledExecutorService schedulerForMinute, schedulerForHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sessionManager = new SessionManager(this);
        databaseHandler = new DatabaseHandler(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        pd = new ProgressDialog(this);

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
        Picasso.with(this)
                .load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .into(profilePicture);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tv_selliscope_version = navigationView.getHeaderView(0).findViewById(R.id.tv_selliscope_version);
        tv_selliscope_version.setText("Version - " + BuildConfig.VERSION_NAME);

        //loading Data into background
        schedulerForMinute = Executors.newSingleThreadScheduledExecutor();
        schedulerForMinute.scheduleAtFixedRate(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Running threads", "Thread running in background for updating products and outlets");
                        getProducts(false);
                        getOutlets();
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
                        getProducts(true);
                    }
                });
            }
        }, 30, 30, TimeUnit.MINUTES);
        //loading Data into background
    }

    //loading Data into background
    public void getProducts(final boolean fullUpdate) {
        if (sessionManager.isLoggedIn()) {
            Call<VariantProductResponse> call = apiService.getProducts();
            call.enqueue(new Callback<VariantProductResponse>() {
                @Override
                public void onResponse(Call<VariantProductResponse> call, Response<VariantProductResponse> response) {
                    if (response.code() == 200) {
                        try {
                            List<ProductsItem> products = response.body().getResult().getProducts();
                            if (products.size() != databaseHandler.getSizeOfProduct() || fullUpdate) {
                                //for removing previous data
                                databaseHandler.removeProductCategoryBrand();
                                for (ProductsItem result : products) {
                                    if (result.getGodown() == null) {
                                        databaseHandler.addProduct(
                                                result.getId(),
                                                result.getName(),
                                                result.getPrice(),
                                                result.getImg(),
                                                Integer.parseInt(result.getBrand().getId()),
                                                result.getBrand().getName(),
                                                Integer.parseInt(result.getCategory().getId()),
                                                result.getCategory().getName(),
                                                "",
                                                !result.getVariants().isEmpty()
                                        );
                                    } else {
                                        int stock = 0;
                                        for (GodownItem godownItem : result.getGodown()) {
                                            stock += Integer.parseInt(godownItem.getStock());
                                        }
                                        databaseHandler.addProduct(
                                                result.getId(),
                                                result.getName(),
                                                result.getPrice(),
                                                result.getImg(),
                                                Integer.parseInt(result.getBrand().getId()),
                                                result.getBrand().getName(),
                                                Integer.parseInt(result.getCategory().getId()),
                                                result.getCategory().getName(),
                                                String.valueOf(stock),
                                                !result.getVariants().isEmpty()
                                        );
                                    }
                                }
                                databaseHandler.removeVariantCategories();
                                databaseHandler.setVariantCategories(
                                        response.body().getResult().getVariant(),
                                        response.body().getResult().getProducts()
                                );
                                getCategory();
                                getBrand();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == 401) {
                        Toast.makeText(getApplicationContext(), "Invalid VariantProductResponse from server on Application.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Server Error! Try Again Later on Application!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<VariantProductResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void getCategory() {
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.code() == 200) {
                    try {
                        List<CategoryResponse.CategoryResult> categories = response.body().result.categoryResults;
                        for (CategoryResponse.CategoryResult result : categories) {
                            databaseHandler.addCategory(result.id, result.name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid Response from server on Application.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error! Try Again Later on Application!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getBrand() {
        Call<BrandResponse> call = apiService.getBrands();
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.code() == 200) {
                    try {
                        List<BrandResponse.BrandResult> brands = response.body().result.brandResults;
                        for (BrandResponse.BrandResult result : brands) {
                            databaseHandler.addBrand(result.id, result.name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid Response from server on Application.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error! Try Again Later on Application!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getOutlets() {
        Call<ResponseBody> call = apiService.getOutlets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        Outlets.Successful getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.Successful.class);
                        if (getOutletListSuccessful.outletsResult.outlets.size() != databaseHandler.getSizeOfOutlet()) {
                            databaseHandler.removeOutlet();
                            databaseHandler.addOutlet(getOutletListSuccessful.outletsResult.outlets);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid Response from server on Application.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error! Try Again Later on Application!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    //loading Data into background

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
                if (databaseHandler.removeAll()) {
                    sessionManager.logoutUser();
                    schedulerForMinute.shutdownNow();
                    schedulerForHour.shutdownNow();
                    stopService(new Intent(getApplicationContext(), SendLocationDataService.class));
                    getApplicationContext().deleteDatabase(Constants.databaseName);
                    pd.dismiss();
                    finish();
                }
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


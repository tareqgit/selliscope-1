package com.humaclab.selliscope.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationSource.OnLocationChangedListener {

    private GoogleMap mMap;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.route));
        setSupportActionBar(toolbar);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermission(RouteActivity.this))

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        if (isGPSEnabled()) {
            getLocation();


        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Enable GPS");
            alertDialog.setMessage("Enable GPS to get current location.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                alertDialog.dismiss();
            });
            alertDialog.show();
        }
    }

    public void getLocation() {
        if (checkPermission(RouteActivity.this)) {
          /*  Awareness.SnapshotApi.getLocation(googleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                if (mMap != null) {
                                    Log.d("tareq_test" , ""+location.getLatitude());
                                            Double lat=Double.parseDouble(String.format(Locale.US,"%.05f",  location.getLatitude()));
                                            Double lon= Double.parseDouble(String.format(Locale.US,"%.05f", location.getLongitude()));
                               Log.d("tareq_test" , ""+lat+" - "+lon);
                                    LatLng currentLocation = new LatLng(lat,
                                           lon);
                                    mMap.addMarker(new MarkerOptions().position(currentLocation)
                                            .title("You are here!")
                                            .icon(BitmapDescriptorFactory.fromResource(
                                                    R.drawable.ic_user_current_location)));
                                    CameraPosition cameraPosition = new CameraPosition(
                                            currentLocation, 15, 70, 0);
                                    CameraUpdate yourLocation = CameraUpdateFactory
                                            .newCameraPosition(cameraPosition);
                                    mMap.animateCamera(yourLocation);
                                    getVisits();
                                }
                            } else {
                                Timber.d("Didn't get Location Data");
                            }
                        }
                    });*/

            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    LatLng currentLocation;
                    if (mLastKnownLocation != null) {
                        currentLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        mMap.clear();

                        mMap.addMarker(new MarkerOptions().position(currentLocation)
                                .title("You are here!")
                                .icon(BitmapDescriptorFactory.fromResource(
                                        R.drawable.ic_user_current_location)));


                  /*  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));*/
                        CameraPosition cameraPosition = new CameraPosition(
                                currentLocation, 15, 70, 0);

                        CameraUpdate yourLocation = CameraUpdateFactory
                                .newCameraPosition(cameraPosition);
                        mMap.animateCamera(yourLocation);
                        getVisits();
                    }
                } else {
                    Log.d("tareq_test", "Current location is null. Using defaults.");
                    Log.e("tareq_test", "Exception: %s", task.getException());
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } else {
            Timber.d("Location Permission is not enabled.");
        }
    }

    void getVisits() {
        SessionManager sessionManager = new SessionManager(RouteActivity.this);
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<Outlets> call = apiService.getOutlets();
        call.enqueue(new Callback<Outlets>() {
            @Override
            public void onResponse(@NonNull Call<Outlets> call, @NonNull Response<Outlets> response) {
             //   Gson gson = new Gson();
                if (response.code() == 200) {
                    //       Outlets getOutletListSuccessful ;
                    if (response.body() != null) {
               //         getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.class);

                        List<Outlets.Outlet> outlets = response.body().outletsResult.outlets;
                        for (int i = 0; i < outlets.size(); i++) {
                            mMap.addMarker(new MarkerOptions().position(
                                    new LatLng(outlets.get(i).outletLatitude,
                                            outlets.get(i).outletLongitude))
                                    .icon(vectorToBitmap(
                                            R.drawable.ic_dokan, 0))
                                    .title(outlets.get(i).outletName));
                        }
                    }

                } else if (response.code() == 401) {
                    Toast.makeText(RouteActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RouteActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Outlets> call, @NonNull Throwable t) {
                Log.d("Response", t.toString());
            }
        });

    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(Objects.requireNonNull(vectorDrawable).getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        getLocation();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_gps) {
            getLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

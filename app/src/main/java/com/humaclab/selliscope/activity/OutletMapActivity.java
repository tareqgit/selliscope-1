/*
 * Created by Tareq Islam on 4/17/19 4:23 PM
 *
 *  Last modified 4/11/19 12:44 PM
 */

/*
 * Created by Tareq Islam on 4/11/19 12:41 PM
 *
 *  Last modified 4/10/19 3:14 PM
 */

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
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class OutletMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationSource.OnLocationChangedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

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
        toolbarTitle.setText(getResources().getString(R.string.dashBoard_map));
        setSupportActionBar(toolbar);
        googleApiClient = new GoogleApiClient.Builder(OutletMapActivity.this)
                .addApi(Awareness.API)
                .build();
        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                getLocation();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d("tareq_test" , ""+getIntent().getDoubleExtra("outletLat",0));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermission(OutletMapActivity.this))
            mMap.getUiSettings().setMapToolbarEnabled(true);
        if (isGPSEnabled()) {
            getLocation();
        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Enable GPS");
            alertDialog.setMessage("Enable GPS to get current location.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    public void getLocation() {
        if (checkPermission(OutletMapActivity.this)) {
            Awareness.SnapshotApi.getLocation(googleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                if (mMap != null) {
                                    LatLng currentLocation = new LatLng(Double.parseDouble(String.format(Locale.US,"%.05f", location.getLatitude())),
                                            Double.parseDouble(String.format(Locale.US,"%.05f", location.getLongitude())));


                                    LatLng outletLocation = new LatLng(getIntent().getDoubleExtra("outletLat",0),
                                            getIntent().getDoubleExtra("outletLong",0));


                                 //   Location outletLocation = new Location("outlet_location");
                                //    outletLocation.setLatitude(Double.parseDouble(getIntent().getStringExtra("outletLat")));
                                  //  outletLocation.setLongitude(Double.parseDouble(getIntent().getStringExtra("outletLong")));


                                    mMap.addMarker(new MarkerOptions().position(currentLocation)
                                            .title("You are here!")
                                            .icon(BitmapDescriptorFactory.fromResource(
                                                    R.drawable.ic_user_current_location)));
                                    CameraPosition cameraPosition = new CameraPosition(
                                            outletLocation, 15, 70, 0);
                                    CameraUpdate yourLocation = CameraUpdateFactory
                                                            .newLatLngZoom(outletLocation,21f);


                                    mMap.animateCamera(yourLocation);

                                    getVisits();
                                }
                            } else {
                                Timber.d("Didn't get Location Data");
                            }
                        }
                    });
        } else {
            Timber.d("Location Permission is not enabled.");
        }
    }

    void getVisits() {
        SessionManager sessionManager = new SessionManager(OutletMapActivity.this);
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getOutlets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        Outlets getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.class);
                        List<Outlets.Outlet> outlets = getOutletListSuccessful
                                .outletsResult.outlets;
                        for (int i = 0; i < outlets.size(); i++) {
                            mMap.addMarker(new MarkerOptions().position(
                                    new LatLng(outlets.get(i).outletLatitude,
                                            outlets.get(i).outletLongitude))
                                    .icon(vectorToBitmap(
                                            R.drawable.store_location_marker, 0))
                                    .title(outlets.get(i).outletName));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(OutletMapActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OutletMapActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());
            }
        });

    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
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
        googleApiClient.disconnect();
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

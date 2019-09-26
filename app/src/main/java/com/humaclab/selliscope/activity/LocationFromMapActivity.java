package com.humaclab.selliscope.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.humaclab.selliscope.R;

import java.io.IOException;
import java.util.List;

public class LocationFromMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;


    private SupportMapFragment mapFragment;

    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private TextView resutText;
    private Button selectLocation;
    private Double mLatitude, mLongitude;
    private int MAP_LOCATION = 512;
    private String address;


    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < 22)
            setStatusBarTranslucent(false);
        else
            setStatusBarTranslucent(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_from_map);


        resutText = findViewById(R.id.dragg_result);
        selectLocation = findViewById(R.id.btn_select_location);
        selectLocation.setOnClickListener(v -> {
            try {
                Intent intent = new Intent();
                intent.putExtra("latitude", mLatitude);
                intent.putExtra("longitude", mLongitude);
                intent.putExtra("address", address);
                setResult(MAP_LOCATION, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        configureCameraIdle();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraIdleListener(onCameraIdleListener);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getLocation();
                return false;
            }
        });
        getLocation();

    }

    private void getLocation() {
        //        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Set the map's camera position to the current location of the device.
                Location location = task.getResult();
                if (location != null) {
                    Log.d("tareq_test", "LocationFromMapActivity #133: getLocation:  " + location.getLatitude() + " " + location.getLongitude());
                    LatLngBounds hisLocation = new LatLngBounds(new LatLng(location.getLatitude() - .0005f, location.getLongitude() - .0005f), new LatLng(location.getLatitude() + .0005f, location.getLongitude() + .0005f));

                    //   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(20)                   // Sets the zoom

                            .bearing(0)                // Sets the orientation of the camera to North
                            .tilt(0)                   // Sets the tilt of the camera to 0 degrees

                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.setLatLngBoundsForCameraTarget(hisLocation);
                }
            }
        });


    }

    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = mMap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(LocationFromMapActivity.this);
                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if (!locality.isEmpty() && !country.isEmpty()) {
                            resutText.setText(locality + "  " + country);
                            address = locality;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}

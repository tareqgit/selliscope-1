package com.humaclab.selliscope;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.gson.Gson;
import com.humaclab.selliscope.Utils.NetworkUtility;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.adapters.DistrictAdapter;
import com.humaclab.selliscope.adapters.OutletTypeAdapter;
import com.humaclab.selliscope.adapters.ThanaAdapter;
import com.humaclab.selliscope.model.CreateOutlet;
import com.humaclab.selliscope.model.Districts;
import com.humaclab.selliscope.model.Login;
import com.humaclab.selliscope.model.OutletTypes;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.Thanas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class EditOutletActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    private EditText outletName, outletAddress, outletOwner, outletContactNumber;
    private Spinner outletType, district, thana;
    private ImageView iv_outlet;
    private Button submit, cancel;
    private String email, password;
    private OutletTypeAdapter outletTypeAdapter;
    private ThanaAdapter thanaAdapter;
    private DistrictAdapter districtAdapter;
    SelliscopeApiEndpointInterface apiService;
    SessionManager sessionManager;
    private GoogleApiClient googleApiClient;
    boolean isValidOutletName = true;
    boolean isValidOwnerName = true;
    boolean isValidAddress = true;
    boolean isValidPhone = true;
    double latitude, longitude = 0.0;
    int outletTypeId, thanaId = -1;
    private String outletImage;

    private Outlets.Successful.Outlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_outlet);

        googleApiClient = new GoogleApiClient.Builder(EditOutletActivity.this)
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

        outlet = (Outlets.Successful.Outlet) getIntent().getSerializableExtra("outletDetails");

        sessionManager = new SessionManager(this);
        email = sessionManager.getUserEmail();
        password = sessionManager.getUserPassword();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Outlet");
        setSupportActionBar(toolbar);
        outletName = (EditText) findViewById(R.id.et_outlet_name);
        outletName.setText(outlet.outletName);

        outletAddress = (EditText) findViewById(R.id.et_outlet_address);
        outletAddress.setText(outlet.outletAddress);

        outletOwner = (EditText) findViewById(R.id.et_outlet_owner_name);
        outletOwner.setText(outlet.ownerName);

        outletContactNumber = (EditText) findViewById(R.id.et_outlet_contact_number);
        outletContactNumber.setText(outlet.phone);

        district = (Spinner) findViewById(R.id.sp_district);
        thana = (Spinner) findViewById(R.id.sp_thana);
        outletType = (Spinner) findViewById(R.id.sp_outlet_type);
        submit = (Button) findViewById(R.id.btn_add_outlet);
        cancel = (Button) findViewById(R.id.btn_cancel);
        getDistricts(email, password);
        getOutletTypes(email, password);

        iv_outlet = (ImageView) findViewById(R.id.iv_outlet);
        Glide.with(getApplicationContext()).load(outlet.outletImgUrl)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.ic_outlet_bnw)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_outlet);

        iv_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Districts.Successful.District district =
                        (Districts.Successful.District) parent.getItemAtPosition(position);
                getThanas(email, password, district.districtId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        thana.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Thanas.Successful.Thana thana =
                        (Thanas.Successful.Thana) parent.getItemAtPosition(position);
                thanaId = thana.thanaId;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        outletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OutletTypes.Successful.OutletType outletType =
                        (OutletTypes.Successful.OutletType) parent.getItemAtPosition(position);
                outletTypeId = outletType.outletTypeId;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outletName.getText().toString().trim().isEmpty()) {
                    outletName.setError("Required!");
                    isValidOutletName = false;
                } else {
                    isValidOutletName = true;
                }
                if (outletOwner.getText().toString().trim().isEmpty()) {
                    outletName.setError("Required!");
                    isValidOwnerName = false;
                } else {
                    isValidOwnerName = true;
                }
                if (outletContactNumber.getText().toString().trim().isEmpty()) {
                    outletContactNumber.setError("Required!");
                    isValidPhone = false;
                } else {
                    isValidPhone = true;
                }
                if (outletAddress.getText().toString().trim().isEmpty()) {
                    outletAddress.setError("Required!");
                    isValidAddress = false;
                } else {
                    isValidAddress = true;
                }
                Timber.d("Valid Address" + isValidAddress);
                Timber.d("Valid Outle Name" + isValidOutletName);
                Timber.d("valid owner naem" + isValidOwnerName);
                Timber.d("Valid phone" + isValidPhone);
                Timber.d("Latitude" + latitude);
                Timber.d("Long" + longitude);
                Timber.d("Type id" + outletTypeId);
                Timber.d("Thana id" + thanaId);

                if (isValidAddress && isValidOutletName && isValidOwnerName
                        && isValidPhone && latitude != 0.0 && longitude != 0.0
                        && outletTypeId != -1 && thanaId != -1) {
                    Timber.d("addOutletRun");
                    if (NetworkUtility.isNetworkAvailable(EditOutletActivity.this)) {
                        /*addOutlet(email, password, outletTypeId, outletName.getText().toString().trim(),
                                outletOwner.getText().toString().trim(),
                                outletAddress.getText().toString().trim(), thanaId,
                                outletContactNumber.getText().toString().trim(), latitude, longitude);*/
                    } else
                        Toast.makeText(EditOutletActivity.this, "Connect to Wifi or Mobile Data",
                                Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void editOutlet(String email, String password, int outletTypeId, String outletName,
                            String ownerName, String address, int thanaId, String phone,
                            double latitude, double longitude) {
        apiService = SelliscopeApplication.getRetrofitInstance(email, password, false)
                .create(SelliscopeApiEndpointInterface.class);
       /* List<CreateOutlet.Outlet> outlet = new ArrayList<>();
        outlet.add(new CreateOutlet.Outlet(outletTypeId, outletName,
                ownerName, address, thanaId, phone, latitude, longitude, outletImage));*/

        Call<ResponseBody> call = apiService.updateOutlet(outlet.outletId, new CreateOutlet(outletTypeId, outletName,
                ownerName, address, thanaId, phone, latitude, longitude, outletImage));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                System.out.println("Response code: " + response.code());
                System.out.println("Response: " + new Gson().toJson(response.body()));
                if (response.code() == 201) {
                    try {
                        CreateOutlet createOutletResult =
                                gson.fromJson(response.body().string()
                                        , CreateOutlet.class);
                        Toast.makeText(EditOutletActivity.this, createOutletResult.result,
                                Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(EditOutletActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditOutletActivity.this,
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

    void getDistricts(String email, String password) {
        apiService = SelliscopeApplication.getRetrofitInstance(email, password, false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getDistricts();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                Timber.d("Response " + response.code() + " " + response.body().toString());
                if (response.code() == 200) {
                    try {
                        Districts.Successful districtListSuccessful
                                = gson.fromJson(response.body().string()
                                , Districts.Successful.class);
                        districtAdapter = new DistrictAdapter(EditOutletActivity.this,
                                districtListSuccessful.districtResult.districts);
                        district.setAdapter(districtAdapter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    try {
                        Login.Unsuccessful loginUnsuccessful = gson
                                .fromJson(response.errorBody().string()
                                        , Login.Unsuccessful.class);
                        Toast.makeText(EditOutletActivity.this,
                                loginUnsuccessful.result, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditOutletActivity.this,
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

    void getOutletTypes(String email, String password) {
        apiService = SelliscopeApplication.getRetrofitInstance(email, password, false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getOutletTypes();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                Timber.d("Response " + response.code() + " " + response.body().toString());
                if (response.code() == 200) {
                    try {
                        OutletTypes.Successful outletTypeListSuccessful
                                = gson.fromJson(response.body().string()
                                , OutletTypes.Successful.class);
                        outletTypeAdapter = new OutletTypeAdapter(EditOutletActivity.this,
                                outletTypeListSuccessful.outletTypeResult.outletTypes);
                        outletType.setAdapter(outletTypeAdapter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    try {
                        Login.Unsuccessful loginUnsuccessful = gson.fromJson(response.errorBody().string()
                                , Login.Unsuccessful.class);
                        Toast.makeText(EditOutletActivity.this,
                                loginUnsuccessful.result, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditOutletActivity.this,
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

    void getThanas(String email, String password, int districtId) {
        apiService = SelliscopeApplication.getRetrofitInstance(email, password, false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getThanas(districtId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                Timber.d("Response " + response.code() + " " + response.body().toString());
                if (response.code() == 200) {
                    try {
                        Thanas.Successful thanaListSuccessful
                                = gson.fromJson(response.body().string()
                                , Thanas.Successful.class);
                        thanaAdapter = new ThanaAdapter(EditOutletActivity.this,
                                thanaListSuccessful.thanaResult.thanas);
                        thana.setAdapter(thanaAdapter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    try {
                        Login.Unsuccessful loginUnsuccessful = gson
                                .fromJson(response.errorBody().string()
                                        , Login.Unsuccessful.class);
                        Toast.makeText(EditOutletActivity.this,
                                loginUnsuccessful.result, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditOutletActivity.this,
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

    public void getLocation() {
        if (checkPermission(EditOutletActivity.this)) {
            Awareness.SnapshotApi.getLocation(googleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            } else {
                                Timber.d("Didn't get Location Data");
                            }
                        }
                    });
        } else {
            Timber.d("Location Permission is not enabled.");
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            assert photo != null;
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outletImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            iv_outlet.setImageBitmap(photo);
        }
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }
}

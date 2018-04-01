package com.humaclab.selliscope_rangs.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.selliscope_rangs.R;
import com.humaclab.selliscope_rangs.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_rangs.SelliscopeApplication;
import com.humaclab.selliscope_rangs.adapters.DistrictAdapter;
import com.humaclab.selliscope_rangs.adapters.OutletTypeAdapter;
import com.humaclab.selliscope_rangs.adapters.ThanaAdapter;
import com.humaclab.selliscope_rangs.model.CreateOutlet;
import com.humaclab.selliscope_rangs.model.District.District;
import com.humaclab.selliscope_rangs.model.OutletType.OutletType;
import com.humaclab.selliscope_rangs.model.Thana.Thana;
import com.humaclab.selliscope_rangs.utils.DatabaseHandler;
import com.humaclab.selliscope_rangs.utils.NetworkUtility;
import com.humaclab.selliscope_rangs.utils.SendUserLocationData;
import com.humaclab.selliscope_rangs.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by leon on 11/19/17.
 */

public class AddOutletActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    boolean isValidOutletName = true;
    boolean isValidOwnerName = true;
    boolean isValidAddress = true;
    boolean isValidPhone = true;
    String mLatitude = null, mLongitude = null;
    private int outletTypeId, thanaId = -1;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private EditText outletName, outletAddress, outletOwner, outletContactNumber;
    private Spinner outletType, district, thana;
    private ImageView iv_outlet;
    private Button submit, cancel;
    private OutletTypeAdapter outletTypeAdapter;
    private ThanaAdapter thanaAdapter;
    private DistrictAdapter districtAdapter;
    private GoogleApiClient googleApiClient;
    private String outletImage;
    private DatabaseHandler databaseHandler;

    private ProgressDialog pd;

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outlet);
        googleApiClient = new GoogleApiClient.Builder(AddOutletActivity.this)
                .addApi(Awareness.API)
                .addApi(LocationServices.API)
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
        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        pd = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Outlet");
        setSupportActionBar(toolbar);
        outletName = findViewById(R.id.et_outlet_name);
        outletAddress = findViewById(R.id.et_outlet_address);
        outletOwner = findViewById(R.id.et_outlet_owner_name);
        outletContactNumber = findViewById(R.id.et_outlet_contact_number);
        district = findViewById(R.id.sp_district);
        thana = findViewById(R.id.sp_thana);
        outletType = findViewById(R.id.sp_outlet_type);
        submit = findViewById(R.id.btn_add_outlet);
        cancel = findViewById(R.id.btn_cancel);
        getDistricts();
        getOutletTypes();

        iv_outlet = findViewById(R.id.iv_outlet);
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
                District district = (District) parent.getItemAtPosition(position);
                getThanas(district.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        thana.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Thana thana = (Thana) parent.getItemAtPosition(position);
                thanaId = thana.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        outletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OutletType outletType = (OutletType) parent.getItemAtPosition(position);
                outletTypeId = outletType.getId();
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
                Timber.d("Latitude" + mLatitude);
                Timber.d("Long" + mLongitude);
                Timber.d("Type id" + outletTypeId);
                Timber.d("Thana id" + thanaId);

                if (isValidAddress && isValidOutletName && isValidOwnerName
                        && isValidPhone && mLatitude != null && mLongitude != null
                        && outletTypeId != -1 && thanaId != -1) {
                    Timber.d("addOutletRun");
                    if (NetworkUtility.isNetworkAvailable(AddOutletActivity.this)) {
                        addOutlet(
                                outletTypeId, outletName.getText().toString().trim(),
                                outletOwner.getText().toString().trim(),
                                outletAddress.getText().toString().trim(),
                                thanaId,
                                outletContactNumber.getText().toString().trim(),
                                mLatitude,
                                mLongitude
                        );
                    } else
                        Toast.makeText(AddOutletActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddOutletActivity.this, "Could not found any location yet.\nPlease try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addOutlet(int outletTypeId, String outletName,
                           String ownerName, String address, int thanaId, String phone,
                           String latitude, String longitude) {
        pd.setMessage("Creating outlet......");
        pd.setCancelable(false);
        pd.show();

        Call<ResponseBody> call = apiService.createOutlet(new CreateOutlet(outletTypeId, outletName, ownerName, address, thanaId, phone, latitude, longitude, outletImage));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                System.out.println("Response code: " + response.code());
                pd.dismiss();
                if (response.code() == 201) {
                    Toast.makeText(AddOutletActivity.this, "Outlet created successfully", Toast.LENGTH_SHORT).show();
                    try {
                        CreateOutlet createOutletResult = gson.fromJson(response.body().string(), CreateOutlet.class);
                        Toast.makeText(AddOutletActivity.this, createOutletResult.result, Toast.LENGTH_SHORT).show();
                        submit.setEnabled(false);
                        finish();
                        startActivity(new Intent(AddOutletActivity.this, OutletActivity.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(AddOutletActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddOutletActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());

            }
        });
    }

    void getDistricts() {
        districtAdapter = new DistrictAdapter(AddOutletActivity.this, databaseHandler.getDistrict());
        district.setAdapter(districtAdapter);
    }

    void getThanas(int districtId) {
        thanaAdapter = new ThanaAdapter(AddOutletActivity.this, databaseHandler.getThana(districtId));
        thana.setAdapter(thanaAdapter);
    }

    void getOutletTypes() {
        outletTypeAdapter = new OutletTypeAdapter(AddOutletActivity.this, databaseHandler.getOutletType());
        outletType.setAdapter(outletTypeAdapter);
    }

    public void getLocation() {
        SendUserLocationData sendUserLocationData = new SendUserLocationData(AddOutletActivity.this);
        sendUserLocationData.getInstantLocation(this, new SendUserLocationData.OnGetLocation() {
            @Override
            public void getLocation(Double latitude, Double longitude) {
                mLatitude = String.valueOf(latitude);
                mLongitude = String.valueOf(longitude);
            }
        });
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

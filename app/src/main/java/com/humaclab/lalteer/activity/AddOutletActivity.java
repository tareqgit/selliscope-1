package com.humaclab.lalteer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
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
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.DistrictAdapter;
import com.humaclab.lalteer.adapters.OutletTypeAdapter;
import com.humaclab.lalteer.adapters.ThanaAdapter;
import com.humaclab.lalteer.model.CreateOutlet;
import com.humaclab.lalteer.model.District.District;
import com.humaclab.lalteer.model.OutletType.OutletType;
import com.humaclab.lalteer.model.Thana.Thana;
import com.humaclab.lalteer.utils.AccessPermission;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.LoadLocalIntoBackground;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SendUserLocationData;
import com.humaclab.lalteer.utils.SessionManager;

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
    double mLatitude = 0.0, mLongitude = 0.0, mCurrentLongitude = 0.0, mCurrentLatitude = 0.0;
    private int outletTypeId, thanaId = -1;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private EditText outletName, outletAddress, outletOwner, outletContactNumber, creditLimit;
    private Spinner outletType, district, thana;
    private ImageView iv_outlet;
    private Button submit, cancel, getLocation;
    private OutletTypeAdapter outletTypeAdapter;
    private ThanaAdapter thanaAdapter;
    private DistrictAdapter districtAdapter;
    private GoogleApiClient googleApiClient;
    private String outletImage;
    private DatabaseHandler databaseHandler;

    private ProgressDialog pd;
    private int MAP_LOCATION = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outlet);
        checkPermission();

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
        toolbar.setTitle(R.string.add_dealer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outletName = findViewById(R.id.et_outlet_name);
        outletAddress = findViewById(R.id.et_outlet_address);
        outletOwner = findViewById(R.id.et_outlet_owner_name);
        creditLimit = findViewById(R.id.et_credit);
        outletContactNumber = findViewById(R.id.et_outlet_contact_number);
        district = findViewById(R.id.sp_district);
        thana = findViewById(R.id.sp_thana);
        outletType = findViewById(R.id.sp_outlet_type);
        submit = findViewById(R.id.btn_add_outlet);
        getLocation = findViewById(R.id.btn_get_location);
        cancel = findViewById(R.id.btn_cancel);
        getDistricts();
        getOutletTypes();

        iv_outlet = findViewById(R.id.iv_outlet);
        iv_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
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
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddOutletActivity.this, LocationFromMapActivity.class);
                intent.putExtra("latitude", mCurrentLatitude);
                intent.putExtra("longitude", mCurrentLongitude);
                startActivityForResult(intent, MAP_LOCATION);
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
                Timber.d("Valid Address" + isValidAddress);
                Timber.d("Valid Outle Name" + isValidOutletName);
                Timber.d("valid owner naem" + isValidOwnerName);
                Timber.d("Valid phone" + isValidPhone);
                Timber.d("Latitude" + mLatitude);
                Timber.d("Long" + mLongitude);
                Timber.d("Type id" + outletTypeId);
                Timber.d("Thana id" + thanaId);

                if (!isEmpty()) {
                    if (mLatitude != 0.0 && mLongitude != 0.0) {
                        Timber.d("addOutletRun");
                        if (NetworkUtility.isNetworkAvailable(AddOutletActivity.this)) {
                            Location currentLocation = new Location("");
                            currentLocation.setLatitude(mCurrentLatitude);
                            currentLocation.setLongitude(mCurrentLongitude);

                            Location mapLocation = new Location("");
                            mapLocation.setLatitude(mLatitude);
                            mapLocation.setLongitude(mLongitude);

                            if (mapLocation.distanceTo(currentLocation) <= 100) {
                                if (thanaId != 0) {
                                    if(outletContactNumber.getText().toString().length()<11) {
                                        outletContactNumber.setError("Number should be valid mobile number");
                                    }else {
                                        int credit_limit=0;
                                        try {
                                            credit_limit=Integer.parseInt(creditLimit.getText().toString().trim());
                                        } catch (NumberFormatException e) {
                                            credit_limit=0;
                                        }
                                        addOutlet(
                                                outletTypeId, outletName.getText().toString().trim(),
                                                outletOwner.getText().toString().trim(),
                                                outletAddress.getText().toString().trim(),
                                                thanaId,
                                                outletContactNumber.getText().toString().trim(),
                                                mLatitude,
                                                mLongitude,
                                                credit_limit

                                        );
                                    }
                                } else {
                                    Toast.makeText(AddOutletActivity.this, getString(R.string.Please_select_a_thana), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AddOutletActivity.this, getString(R.string.Dealer_location_is), Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(AddOutletActivity.this, getString(R.string.connect), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddOutletActivity.this, getString(R.string.Could_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isEmpty() {
        int count = 0;
        View view;
        if (outletName.getText().toString().trim().isEmpty()) {
            view = outletName;
            outletName.setError(getString(R.string.error_field_required));
            view.requestFocus();
            count++;
        }
        if (outletOwner.getText().toString().trim().isEmpty()) {
            view = outletOwner;
            outletOwner.setError(getString(R.string.error_field_required));
            view.requestFocus();
            count++;
        }
        if (outletContactNumber.getText().toString().trim().isEmpty()) {
            view = outletContactNumber;
            outletContactNumber.setError(getString(R.string.error_field_required));
            view.requestFocus();
            count++;
        }
        if (outletAddress.getText().toString().trim().isEmpty()) {
            view = outletAddress;
            outletAddress.setError(getString(R.string.error_field_required));
            view.requestFocus();
            count++;
        }
        return count != 0;
    }

    private void addOutlet(int outletTypeId, String outletName,
                           String ownerName, String address, int thanaId, String phone,
                           double latitude, double longitude, int creditLimit) {
        pd.setMessage(getString(R.string.creating_Dealer));
        pd.setCancelable(false);
        pd.show();

        CreateOutlet createOutlet = new CreateOutlet(outletTypeId, outletName, ownerName, address, thanaId, phone, latitude, longitude, outletImage,creditLimit);
        Log.d("response", new Gson().toJson(createOutlet));

        Call<ResponseBody> call = apiService.createOutlet(new CreateOutlet(outletTypeId, outletName, ownerName, address, thanaId, phone, latitude, longitude, outletImage,creditLimit));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                System.out.println("Response code: " + response.code());
                pd.dismiss();
                if (response.code() == 200) {
                    Toast.makeText(AddOutletActivity.this, getString(R.string.dealer_created_msg), Toast.LENGTH_SHORT).show();
                    LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(AddOutletActivity.this);
                    loadLocalIntoBackground.loadOutlet(true);
                    try {
                        CreateOutlet createOutletResult = gson.fromJson(response.body().string(), CreateOutlet.class);
                        Toast.makeText(AddOutletActivity.this, createOutletResult.result, Toast.LENGTH_SHORT).show();
                        submit.setEnabled(false);
                        Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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
                mCurrentLatitude = latitude;
                mCurrentLongitude = longitude;
            }
        });
    }

    private void checkPermission() {
        AccessPermission.accessPermission(AddOutletActivity.this);
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
        if (requestCode == MAP_LOCATION) {
            try {
                mLatitude = data.getDoubleExtra("latitude", 0.0);
                mLongitude = data.getDoubleExtra("longitude", 0.0);
                outletAddress.setText(data.getStringExtra("address"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

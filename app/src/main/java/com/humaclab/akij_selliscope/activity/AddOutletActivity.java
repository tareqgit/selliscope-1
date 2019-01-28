package com.humaclab.akij_selliscope.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.adapters.DistrictAdapter;
import com.humaclab.akij_selliscope.adapters.OutletTypeAdapter;
import com.humaclab.akij_selliscope.adapters.ThanaAdapter;
import com.humaclab.akij_selliscope.model.CreateOutlet;
import com.humaclab.akij_selliscope.model.District.District;
import com.humaclab.akij_selliscope.model.Thana.Thana;
import com.humaclab.akij_selliscope.utils.AccessPermission;
import com.humaclab.akij_selliscope.utils.DatabaseHandler;
import com.humaclab.akij_selliscope.utils.LoadLocalIntoBackground;
import com.humaclab.akij_selliscope.utils.NetworkUtility;
import com.humaclab.akij_selliscope.utils.SendUserLocationData;
import com.humaclab.akij_selliscope.utils.SessionManager;

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
    private EditText outletName, outletAddress, outletOwner, outletContactNumber, commnet;
    private Spinner outletType, district, thana, sp_otherbevarage, sp_coolerStatus, line;
    private ImageView iv_outlet;
    private Button submit, cancel, getLocation;
    private OutletTypeAdapter outletTypeAdapter;
    private ThanaAdapter thanaAdapter;
    private DistrictAdapter districtAdapter;
    private GoogleApiClient googleApiClient;
    private String outletImage;
    private DatabaseHandler databaseHandler;
    private String str_otherbevarage, str_coolerStatus;
    private CheckBox cb_1,cb_2,cb_3,cb_4,cb_5,cb_6, cb_11,cb_12,cb_13,cb_14,cb_15;

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
        toolbar.setTitle("Add Outlet");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outletName = findViewById(R.id.et_outlet_name);
        outletAddress = findViewById(R.id.et_outlet_address);
        outletOwner = findViewById(R.id.et_outlet_owner_name);
        outletContactNumber = findViewById(R.id.et_outlet_contact_number);
        commnet = findViewById(R.id.commnet);
        district = findViewById(R.id.sp_district);
        line = findViewById(R.id.line);
        cb_1 = findViewById(R.id.cb_1);
        cb_2 = findViewById(R.id.cb_2);
        cb_3 = findViewById(R.id.cb_3);
        cb_4 = findViewById(R.id.cb_4);
        cb_5 = findViewById(R.id.cb_5);
        cb_6 = findViewById(R.id.cb_6);
        cb_11 = findViewById(R.id.cb_11);
        cb_12 = findViewById(R.id.cb_12);
        cb_13 = findViewById(R.id.cb_13);
        cb_14 = findViewById(R.id.cb_14);
        cb_15 = findViewById(R.id.cb_15);

        //sp_coolerStatus = findViewById(R.id.sp_coolerStatus);
        /*sp_coolerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    setSpinnerError(sp_coolerStatus,"field can't be empty");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        //sp_otherbevarage = findViewById(R.id.sp_otherbevarage);

       /* int selectedItemOfMySpinner = sp_coolerStatus.getSelectedItemPosition();
        String actualPositionOfMySpinner = (String) sp_coolerStatus.getItemAtPosition(selectedItemOfMySpinner);

        if (actualPositionOfMySpinner.isEmpty()) {
            setSpinnerError(sp_coolerStatus,"field can't be empty");
        }*/


        thana = findViewById(R.id.sp_thana);
        outletType = findViewById(R.id.sp_outlet_type);
        submit = findViewById(R.id.btn_add_outlet);
        getLocation = findViewById(R.id.btn_get_location);
        cancel = findViewById(R.id.btn_cancel);
        getDistricts();
        //getOutletTypes();

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
        /*outletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OutletType outletType = (OutletType) parent.getItemAtPosition(position);
                outletTypeId = outletType.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
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

                if (!(outletImage == null)) {
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
                                       /* if (!(sp_coolerStatus.getSelectedItemPosition() == 0)) {
                                            if (!(sp_otherbevarage.getSelectedItemPosition() == 0)) {*/
                                                addOutlet(
                                                        outletTypeId, outletName.getText().toString().trim(),
                                                        outletOwner.getText().toString().trim(),
                                                        outletAddress.getText().toString().trim(),
                                                        thanaId,
                                                        outletContactNumber.getText().toString().trim(),
                                                        mLatitude,
                                                        mLongitude,
                                                        commnet.getText().toString().trim()
                                                );
                                            /*} else {
                                                setSpinnerError(sp_otherbevarage, "field can't be empty");
                                            }
                                        } else {
                                            setSpinnerError(sp_coolerStatus, "field can't be empty");
                                        }*/

                                    } else {
                                        Toast.makeText(AddOutletActivity.this, "Please select a thana first", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(AddOutletActivity.this, "Outlet location is not within your 100 meter radius.", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                Toast.makeText(AddOutletActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddOutletActivity.this, "Could not found any location yet.\nPlease try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddOutletActivity.this, "Image Not Capture", Toast.LENGTH_SHORT).show();
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
                           double latitude, double longitude, String commnet) {
        pd.setMessage("Creating outlet......");
        pd.setCancelable(false);
        pd.show();


        StringBuffer coolerStatus = new StringBuffer();

        if (cb_1.isChecked()) {
            coolerStatus.append("AFBL, ");
        }

        if (cb_2.isChecked()) {
            coolerStatus.append("PEPSI/7up, ");
        }

        if (cb_4.isChecked()) {
            coolerStatus.append("COKE/Sprite, ");
        }

        if (cb_3.isChecked()) {
            coolerStatus.append("Fresh, ");
        }

        if (cb_5.isChecked()) {
            coolerStatus.append("PRAN, ");
        }

        if (cb_6.isChecked()) {
            coolerStatus.append("None, ");}

        str_coolerStatus = coolerStatus.toString();


        StringBuffer otherbevarage = new StringBuffer();
        if (cb_11.isChecked()){
            otherbevarage.append("Pranup, " );
        }
        if (cb_12.isChecked()){
            otherbevarage.append("Fresh, ");
        }
        if (cb_13.isChecked()){
            otherbevarage.append("None, ");
        }
        if (cb_14.isChecked()){
            otherbevarage.append("COKE/Sprite, ");
        }
        if (cb_15.isChecked()){
            otherbevarage.append("7up/Pepsi, ");
        }

        str_otherbevarage = otherbevarage.toString();

        Log.d("check",str_coolerStatus+" "+ str_otherbevarage);
        Call<ResponseBody> call = apiService.createOutlet(new CreateOutlet(outletName, ownerName, address, thanaId, line.getSelectedItem().toString(), phone
                , commnet, latitude, longitude, outletImage, str_coolerStatus, str_otherbevarage));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                System.out.println("Response code: " + response.code());
                pd.dismiss();
                if (response.code() == 201) {
                    Toast.makeText(AddOutletActivity.this, "Outlet created successfully", Toast.LENGTH_SHORT).show();
                    LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(AddOutletActivity.this);
                    loadLocalIntoBackground.loadOutlet(true);
                    try {
                        CreateOutlet createOutletResult = gson.fromJson(response.body().string(), CreateOutlet.class);
                        Toast.makeText(AddOutletActivity.this, createOutletResult.result, Toast.LENGTH_SHORT).show();
                        submit.setEnabled(false);
                        Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //finish();
                        //startActivity(new Intent(AddOutletActivity.this, OutletActivity.class));
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

    private void setSpinnerError(Spinner spinner, String error) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.

        }
    }
}

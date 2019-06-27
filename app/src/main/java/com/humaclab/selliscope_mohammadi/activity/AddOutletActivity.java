package com.humaclab.selliscope_mohammadi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_mohammadi.SelliscopeApplication;
import com.humaclab.selliscope_mohammadi.adapters.DistrictAdapter;
import com.humaclab.selliscope_mohammadi.adapters.OutletTypeAdapter;
import com.humaclab.selliscope_mohammadi.adapters.ThanaAdapter;
import com.humaclab.selliscope_mohammadi.model.CreateOutlet;
import com.humaclab.selliscope_mohammadi.model.District.District;
import com.humaclab.selliscope_mohammadi.model.OutletPrefixResponse.PrefixItem;
import com.humaclab.selliscope_mohammadi.model.OutletPrefixResponse.PrefixResponse;
import com.humaclab.selliscope_mohammadi.model.OutletType.OutletType;
import com.humaclab.selliscope_mohammadi.model.Thana.Thana;
import com.humaclab.selliscope_mohammadi.utils.DatabaseHandler;
import com.humaclab.selliscope_mohammadi.utils.LoadLocalIntoBackground;
import com.humaclab.selliscope_mohammadi.utils.NetworkUtility;
import com.humaclab.selliscope_mohammadi.utils.SendUserLocationData;
import com.humaclab.selliscope_mohammadi.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private EditText outletName, outletAddress, outletOwner, outletContactNumber;
    private EditText etCreditLimit, etBankName, etBGAmount, etExpDate, etOwnerEmail;
    private Spinner spOutletPrefix, spPaymentType, outletType, district, thana;
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

    private List<Integer> prefixID = new ArrayList<>();
    private List<String> prefixName = new ArrayList<>();
    private int paymentType;

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

        etCreditLimit = findViewById(R.id.et_credit_limit);
        etBankName = findViewById(R.id.et_bank_name);
        etBGAmount = findViewById(R.id.et_bg_amount);
        etExpDate = findViewById(R.id.et_exp_date);
        etExpDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar myCalendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            etExpDate.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
                        }
                    }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }
            }
        });
        etExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etExpDate.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        etOwnerEmail = findViewById(R.id.et_outlet_owner_email);

        district = findViewById(R.id.sp_district);
        thana = findViewById(R.id.sp_thana);
        outletType = findViewById(R.id.sp_outlet_type);
        spOutletPrefix = findViewById(R.id.sp_outlet_prefix);
        spPaymentType = findViewById(R.id.sp_payment_type);
        submit = findViewById(R.id.btn_add_outlet);
        getLocation = findViewById(R.id.btn_get_location);
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

                if (!isEmpty(spPaymentType.getSelectedItemPosition())) {
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
                                if (spPaymentType.getSelectedItemPosition() > 0) {
                                    addOutlet(
                                            outletTypeId, outletName.getText().toString().trim(),
                                            outletOwner.getText().toString().trim(),
                                            outletAddress.getText().toString().trim(),
                                            thanaId,
                                            outletContactNumber.getText().toString().trim(),
                                            mLatitude,
                                            mLongitude
                                    );
                                } else {
                                    Toast.makeText(AddOutletActivity.this, "Please select a payment type.", Toast.LENGTH_SHORT).show();
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
            }
        });

        spPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        paymentType = 1;
                        etBankName.setVisibility(View.VISIBLE);
                        etBGAmount.setVisibility(View.VISIBLE);
                        etExpDate.setVisibility(View.VISIBLE);

                        etCreditLimit.setText("");
                        etCreditLimit.setVisibility(View.GONE);
                        break;
                    case 2:
                        paymentType = 2;
                        etCreditLimit.setVisibility(View.VISIBLE);

                        etBankName.setText("");
                        etBankName.setVisibility(View.GONE);
                        etBGAmount.setText("");
                        etBGAmount.setVisibility(View.GONE);
                        etExpDate.setText("");
                        etExpDate.setVisibility(View.GONE);
                        break;
                    /*case 3:
                        etBankName.setVisibility(View.GONE);
                        etBGAmount.setVisibility(View.GONE);
                        etExpDate.setVisibility(View.GONE);
                        etCreditLimit.setVisibility(View.GONE);
                        break;*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getPrefix();
    }

    private boolean isEmpty(int selectedItemPosition) {
        int count = 0;
        View view = null;
        if (selectedItemPosition == 1) {
            if (etBankName.getText().toString().isEmpty()) {
                etBankName.setError(getString(R.string.error_field_required));
                view = etBankName;
                view.requestFocus();
                count++;
            }
            if (etBGAmount.getText().toString().isEmpty()) {
                etBGAmount.setError(getString(R.string.error_field_required));
                view = etBGAmount;
                view.requestFocus();
                count++;
            }
            if (etExpDate.getText().toString().isEmpty()) {
                etExpDate.setError(getString(R.string.error_field_required));
                view = etExpDate;
                view.requestFocus();
                count++;
            }
        }
        if (selectedItemPosition == 2) {
            if (etCreditLimit.getText().toString().isEmpty()) {
                etCreditLimit.setError(getString(R.string.error_field_required));
                view = etCreditLimit;
                view.requestFocus();
                count++;
            }
        }
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

    private void getPrefix() {
        pd.setMessage("Loading outlet prefix....");
        pd.show();
        Call<PrefixResponse> call = apiService.getOutletPrefix();
        call.enqueue(new Callback<PrefixResponse>() {
            @Override
            public void onResponse(Call<PrefixResponse> call, Response<PrefixResponse> response) {
                System.out.println("Prefix response:" + new Gson().toJson(response.body()));
                PrefixResponse prefixResponse = response.body();
                List<PrefixItem> prefixItemList = prefixResponse.getPrefixResult().getPrefix();

                for (PrefixItem prefixItem : prefixItemList) {
                    prefixID.add(prefixItem.getId());
                    prefixName.add(prefixItem.getPrefix());
                }
                spOutletPrefix.setAdapter(new ArrayAdapter<>(AddOutletActivity.this, android.R.layout.simple_list_item_1, prefixName));
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<PrefixResponse> call, Throwable t) {

            }
        });
    }

    private void addOutlet(int outletTypeId, String outletName,
                           String ownerName, String address, int thanaId, String phone,
                           double latitude, double longitude) {
        pd.setMessage("Creating outlet......");
        pd.setCancelable(false);
        pd.show();

        System.out.println("Outlet create: " + new Gson().toJson(new CreateOutlet(
                        outletTypeId,
                        outletName,
                        ownerName,
                        address,
                        thanaId,
                        phone,
                        latitude,
                        longitude,
                        outletImage,
                        prefixID.get(spOutletPrefix.getSelectedItemPosition()),
                        paymentType,
                        etBankName.getText().toString(),
                        etBGAmount.getText().toString(),
                        etExpDate.getText().toString(),
                        etCreditLimit.getText().toString(),
                        etOwnerEmail.getText().toString()
                )
        ));
        Call<ResponseBody> call = apiService.createOutlet(new CreateOutlet(
                        outletTypeId,
                        outletName,
                        ownerName,
                        address,
                        thanaId,
                        phone,
                        latitude,
                        longitude,
                        outletImage,
                        prefixID.get(spOutletPrefix.getSelectedItemPosition()),
                        paymentType,
                        etBankName.getText().toString(),
                        etBGAmount.getText().toString(),
                        etExpDate.getText().toString(),
                        etCreditLimit.getText().toString(),
                        etOwnerEmail.getText().toString()
                )
        );
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
                mCurrentLatitude = latitude;
                mCurrentLongitude = longitude;
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
}

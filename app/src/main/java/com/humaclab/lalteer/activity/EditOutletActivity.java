package com.humaclab.lalteer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.bumptech.glide.Glide;
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
import com.humaclab.lalteer.model.Outlets;
import com.humaclab.lalteer.model.Thana.Thana;
import com.humaclab.lalteer.utils.AccessPermission;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class EditOutletActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    boolean isValidOutletName = true;
    boolean isValidOwnerName = true;
    boolean isValidAddress = true;
    boolean isValidPhone = true;
    //    double latitude, longitude = 0.0;
    int outletTypeId, thanaId = -1;
    private EditText outletName, outletAddress, outletOwner, outletContactNumber, creditLimit;
    private Spinner outletType, district, thana;
    private ImageView iv_outlet;
    private Button submit, cancel;
    private String email, password;
    private OutletTypeAdapter outletTypeAdapter;
    private ThanaAdapter thanaAdapter;
    private DistrictAdapter districtAdapter;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private String outletImage;
    private ProgressDialog pd;
    private DatabaseHandler databaseHandler;

    private Outlets.Outlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_outlet);
        checkPermission();

        databaseHandler = new DatabaseHandler(this);
        pd = new ProgressDialog(this);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");

        sessionManager = new SessionManager(this);
        email = sessionManager.getUserEmail();
        password = sessionManager.getUserPassword();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Outlet");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outletName = findViewById(R.id.et_outlet_name);
        outletName.setText(outlet.outletName);

        outletAddress = findViewById(R.id.et_outlet_address);
        outletAddress.setText(outlet.outletAddress);

        outletOwner = findViewById(R.id.et_outlet_owner_name);
        outletOwner.setText(outlet.ownerName);

        outletContactNumber = findViewById(R.id.et_outlet_contact_number);
        outletContactNumber.setText(outlet.phone);

        creditLimit = findViewById(R.id.et_creditLimit);
        creditLimit.setText(String.valueOf(outlet.credit_limit));

        district = findViewById(R.id.sp_district);
        thana = findViewById(R.id.sp_thana);
        outletType = findViewById(R.id.sp_outlet_type);
        submit = findViewById(R.id.btn_update_outlet);
        cancel = findViewById(R.id.btn_cancel);
        getDistricts();
        getOutletTypes();

        iv_outlet = findViewById(R.id.iv_outlet);
        Glide.with(getApplicationContext()).load(outlet.outletImgUrl)
                .thumbnail(0.5f)
                .into(iv_outlet);

        iv_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
                Timber.d("Latitude" + outlet.outletLatitude);
                Timber.d("Long" + outlet.outletLongitude);
                Timber.d("Type id" + outletTypeId);
                Timber.d("Thana id" + thanaId);

                if (isValidAddress && isValidOutletName && isValidOwnerName
                        && isValidPhone && outlet.outletLatitude != 0.0 && outlet.outletLongitude != 0.0
                        && outletTypeId != -1 && thanaId != -1) {
                    Timber.d("addOutletRun");
                    if (NetworkUtility.isNetworkAvailable(EditOutletActivity.this)) {
                        updatedOutlet(email, password, outletTypeId, outletName.getText().toString().trim(),
                                outletOwner.getText().toString().trim(),
                                outletAddress.getText().toString().trim(), thanaId,
                                outletContactNumber.getText().toString().trim(), outlet.outletLatitude, outlet.outletLongitude,Integer.parseInt(creditLimit.getText().toString()));
                    } else
                        Toast.makeText(EditOutletActivity.this, "Connect to Wifi or Mobile Data",
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatedOutlet(String email, String password, int outletTypeId, String outletName,
                               String ownerName, String address, int thanaId, String phone,
                               double latitude, double longitude,int creditLimit) {
        pd.setMessage("Outlet updating.....");
        pd.setCancelable(false);
        pd.show();

        apiService = SelliscopeApplication.getRetrofitInstance(email, password, false).create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.updateOutlet(outlet.outletId,
                new CreateOutlet(outletTypeId, outletName, ownerName, address, thanaId, phone, latitude, longitude, outletImage, creditLimit));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                Gson gson = new Gson();
                System.out.println("Response code: " + response.code());
                if (response.code() == 200) {
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

                finish();
                startActivity(new Intent(EditOutletActivity.this, OutletActivity.class));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Log.d("Response", t.toString());
            }
        });
    }

    void getDistricts() {
        List<District> districtList = databaseHandler.getDistrict();
        districtAdapter = new DistrictAdapter(EditOutletActivity.this, districtList);
        district.setAdapter(districtAdapter);

        //For selecting previous district from server
        int position = 0;
        for (int i = 0; i < districtList.size(); i++) {
            if (districtList.get(i).getName().equals(outlet.district))
                position = i;
        }
        district.setSelection(position);
        //For selecting previous district
    }

    void getThanas(int districtId) {
        List<Thana> thanaList = databaseHandler.getThana(districtId);
        thanaAdapter = new ThanaAdapter(EditOutletActivity.this, thanaList);
        thana.setAdapter(thanaAdapter);

        //For selecting previous thana
        int position = 0;
        for (int i = 0; i < thanaList.size(); i++) {
            if (thanaList.get(i).getName().equals(outlet.thana))
                position = i;
        }
        thana.setSelection(position);
        //For selecting previous thana
    }

    void getOutletTypes() {
        List<OutletType> outletTypeList = databaseHandler.getOutletType();
        outletTypeAdapter = new OutletTypeAdapter(EditOutletActivity.this, outletTypeList);
        outletType.setAdapter(outletTypeAdapter);
        //For selecting previous outlet type
        int position = 0;
        for (int i = 0; i < outletTypeList.size(); i++) {
            if (outletTypeList.get(i).getName().equals(outlet.outletType))
                position = i;
        }
        thana.setSelection(position);
        //For selecting previous outlet type
    }

    private void checkPermission() {
        AccessPermission.accessPermission(EditOutletActivity.this);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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

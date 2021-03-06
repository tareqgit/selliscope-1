package com.easyopstech.easyops.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.easyopstech.easyops.RootApplication;
import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.adapters.DistrictAdapter;
import com.easyopstech.easyops.adapters.OutletTypeAdapter;
import com.easyopstech.easyops.adapters.ThanaAdapter;
import com.easyopstech.easyops.model.CreateOutlet;
import com.easyopstech.easyops.model.district.District;
import com.easyopstech.easyops.model.outlet_type.OutletType;
import com.easyopstech.easyops.model.thana.Thana;
import com.easyopstech.easyops.utils.AccessPermission;
import com.easyopstech.easyops.utils.DatabaseHandler;
import com.easyopstech.easyops.utils.LoadLocalIntoBackground;
import com.easyopstech.easyops.utils.NetworkUtility;
import com.easyopstech.easyops.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leon on 11/19/17.
 */

public class AddOutletActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    boolean isValidOutletName = true;
    boolean isValidOwnerName = true;
    boolean isValidAddress = true;
    boolean isValidPhone = true;
    double mLatitude = 0.0, mLongitude = 0.0;
  //  double  mCurrentLongitude = 0.0, mCurrentLatitude = 0.0;
    private int outletTypeId, thanaId = -1;
    private RootApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private EditText outletName, outletAddress, outletOwner, outletContactNumber, outletrefnumber;
    private Spinner outletType, district, thana;
    private ImageView iv_outlet;
    private Button submit, cancel, getLocation;
    private OutletTypeAdapter outletTypeAdapter;
    private ThanaAdapter thanaAdapter;
    private DistrictAdapter districtAdapter;
  //  private GoogleApiClient googleApiClient;
    private String outletImage;
    private DatabaseHandler databaseHandler;

    private ProgressDialog pd;
    private int MAP_LOCATION = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outlet);
        checkPermission();

     /*   googleApiClient = new GoogleApiClient.Builder(AddOutletActivity.this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

              //  getLocation();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });*/

        databaseHandler = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(RootApiEndpointInterface.class);
        pd = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Outlet");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outletName = findViewById(R.id.et_outlet_name);
        outletAddress = findViewById(R.id.et_outlet_address);
        outletOwner = findViewById(R.id.et_outlet_owner_name);
        outletContactNumber = findViewById(R.id.et_outlet_contact_number);
        outletrefnumber = findViewById(R.id.et_outlet_ref_number);
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
           //     intent.putExtra("latitude", mCurrentLatitude);
             //   intent.putExtra("longitude", mCurrentLongitude);
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

                if (!isEmpty()) {
               //     if (mLatitude != 0.0 && mLongitude != 0.0) {

                        if (NetworkUtility.isNetworkAvailable(AddOutletActivity.this)) {
                         /*   Location currentLocation = new Location("");
                            currentLocation.setLatitude(mCurrentLatitude);
                            currentLocation.setLongitude(mCurrentLongitude);
*/
                            Location mapLocation = new Location("");
                            mapLocation.setLatitude(mLatitude);
                            mapLocation.setLongitude(mLongitude);

                          //  if (mapLocation.distanceTo(currentLocation) <= 100) {
                                if (thanaId != 0) {
                                    addOutlet(
                                            outletTypeId, outletName.getText().toString().trim(),
                                            outletOwner.getText().toString().trim(),
                                            outletAddress.getText().toString().trim(),
                                            thanaId,
                                            outletContactNumber.getText().toString().trim(),
                                            mLatitude,
                                            mLongitude,
                                            outletrefnumber.getText().toString().trim()
                                    );
                                } else {
                                    Toast.makeText(AddOutletActivity.this, "Please select a thana first ", Toast.LENGTH_SHORT).show();
                                }
                      //      } else {
                         //       Toast.makeText(AddOutletActivity.this, "Outlet location is not within your 100 meter radius . "+mapLocation.distanceTo(currentLocation), Toast.LENGTH_SHORT).show();
                       //     }
                        } else
                            Toast.makeText(AddOutletActivity.this, "Connect to Wifi or Mobile Data", Toast.LENGTH_SHORT).show();
                 //   } else {
                //        Toast.makeText(AddOutletActivity.this, "Could not found any location yet.\nPlease try again.", Toast.LENGTH_SHORT).show();
              //      }
                }
            }
        });

        try {

            ((SwipeRefreshLayout) findViewById(R.id.swipeRefresher)).setColorSchemeColors(Color.parseColor("#EA5455"), Color.parseColor("#FCCF31"), Color.parseColor("#F55555"));

            //Do something here


            getDistricts();
            getOutletTypes();


        } catch (Exception e) {
            e.printStackTrace();
        }

        LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(AddOutletActivity.this);


        ((SwipeRefreshLayout) findViewById(R.id.swipeRefresher)).setOnRefreshListener(() -> {
            //if network is Available then update the data again
            if (NetworkUtility.isNetworkAvailable(AddOutletActivity.this)) {
                ((SwipeRefreshLayout) findViewById(R.id.swipeRefresher)).setRefreshing(true);

                loadLocalIntoBackground.loadOutletType(new LoadLocalIntoBackground.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        loadLocalIntoBackground.loadDistrict(new LoadLocalIntoBackground.LoadCompleteListener() {
                            @Override
                            public void onLoadComplete() {
                                loadLocalIntoBackground.loadThana(new LoadLocalIntoBackground.LoadCompleteListener() {
                                    @Override
                                    public void onLoadComplete() {
                                        ((SwipeRefreshLayout) findViewById(R.id.swipeRefresher)).setRefreshing(false);
                                        getDistricts();
                                        getOutletTypes();

                                    }

                                    @Override
                                    public void onLoadFailed(String reason) {
                                        Log.d("tareq_test", "Thana error: " + reason);
                                    }
                                });
                            }

                            @Override
                            public void onLoadFailed(String reason) {
                                Log.d("tareq_test", "District error: " + reason);
                            }
                        });
                    }

                    @Override
                    public void onLoadFailed(String reason) {
                        Log.d("tareq_test", "Outlet Type error: " + reason);
                    }
                });


            }
        });


        //if network is Available then update the data again
        if (NetworkUtility.isNetworkAvailable(AddOutletActivity.this)) {
            if (NetworkUtility.isNetworkAvailable(AddOutletActivity.this)) {
                ((SwipeRefreshLayout) findViewById(R.id.swipeRefresher)).setRefreshing(true);

                loadLocalIntoBackground.loadOutletType(new LoadLocalIntoBackground.LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        loadLocalIntoBackground.loadDistrict(new LoadLocalIntoBackground.LoadCompleteListener() {
                            @Override
                            public void onLoadComplete() {
                                loadLocalIntoBackground.loadThana(new LoadLocalIntoBackground.LoadCompleteListener() {
                                    @Override
                                    public void onLoadComplete() {
                                        ((SwipeRefreshLayout) findViewById(R.id.swipeRefresher)).setRefreshing(false);
                                        getDistricts();
                                        getOutletTypes();

                                    }

                                    @Override
                                    public void onLoadFailed(String reason) {
                                        Log.d("tareq_test", "Thana error: " + reason);
                                    }
                                });
                            }

                            @Override
                            public void onLoadFailed(String reason) {
                                Log.d("tareq_test", "District error: " + reason);
                            }
                        });
                    }

                    @Override
                    public void onLoadFailed(String reason) {
                        Log.d("tareq_test", "Outlet Type error: " + reason);
                    }
                });


            }
        }
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
                           double latitude, double longitude, String outletrefnumber) {
        pd.setMessage("Creating outlet......");
        pd.setCancelable(false);
        pd.show();
        Log.d("tareq_test", "" + outletImage);
        Call<ResponseBody> call = apiService.createOutlet(new CreateOutlet(outletTypeId, outletName, ownerName, address, thanaId, phone, latitude, longitude, outletImage, outletrefnumber));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();

                pd.dismiss();
                if (response.code() == 201) {
                    Toast.makeText(AddOutletActivity.this, "Outlet created successfully", Toast.LENGTH_SHORT).show();
                    LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(AddOutletActivity.this);
                    loadLocalIntoBackground.loadOutlet(null);
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
        District district1 = new District();
        district1.setId(0);
        district1.setName("Select district");
        ArrayList<District> arrayListDistrict = new ArrayList<>();
        arrayListDistrict.add(district1);
        arrayListDistrict.addAll(databaseHandler.getDistrict());
        districtAdapter = new DistrictAdapter(AddOutletActivity.this, arrayListDistrict);
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

  /*  public void getLocation() {
        if (LocationMonitoringService.sLocation != null) {
            mCurrentLatitude = LocationMonitoringService.sLocation.getLatitude();
            mCurrentLongitude = LocationMonitoringService.sLocation.getLongitude();
        }else{
            Toast.makeText(this, "Can't get Location", Toast.LENGTH_SHORT).show();
        }

    }*/

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
            Glide.with(iv_outlet)
                    .load(photo)
                    .transform(new CircleCrop())
                    .into(iv_outlet);
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
      //  googleApiClient.disconnect();
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

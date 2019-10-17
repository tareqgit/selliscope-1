package com.humaclab.lalteer.activity;

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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.DistrictAdapter;
import com.humaclab.lalteer.adapters.OutletTypeAdapter;
import com.humaclab.lalteer.adapters.ThanaAdapter;
import com.humaclab.lalteer.model.CreateOutlet;
import com.humaclab.lalteer.model.district.District;
import com.humaclab.lalteer.model.outlet_type.OutletType;
import com.humaclab.lalteer.model.thana.Thana;
import com.humaclab.lalteer.utils.AccessPermission;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.LoadLocalIntoBackground;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
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

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    
    private OnOutletCreateListener mOnOutletCreateListener;

    public void setOnOutletCreateListener(AddOutletActivity.OnOutletCreateListener onOutletCreateListener) {
        mOnOutletCreateListener = onOutletCreateListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outlet);
        checkPermission();

        /*googleApiClient = new GoogleApiClient.Builder(AddOutletActivity.this)
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
        });*/
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
        getLocation.setOnClickListener(v -> {
            Intent intent = new Intent(AddOutletActivity.this, LocationFromMapActivity.class);
          //  intent.putExtra("latitude", mCurrentLatitude);
           // intent.putExtra("longitude", mCurrentLongitude);
            startActivityForResult(intent, MAP_LOCATION);
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
                 //   if (mLatitude != 0.0 && mLongitude != 0.0) {
                   //     Timber.d("addOutletRun");
                        if (NetworkUtility.isNetworkAvailable(AddOutletActivity.this)) {
                         /*   Location currentLocation = new Location("");
                            currentLocation.setLatitude(mCurrentLatitude);
                            currentLocation.setLongitude(mCurrentLongitude);
*/
                            Location mapLocation = new Location("");
                            mapLocation.setLatitude(mLatitude);
                            mapLocation.setLongitude(mLongitude);

                         //   if (mapLocation.distanceTo(currentLocation) <= 100) {
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
                           //} else {
                           //     Toast.makeText(AddOutletActivity.this, getString(R.string.Dealer_location_is), Toast.LENGTH_SHORT).show();
                           // }
                        } else
                            Toast.makeText(AddOutletActivity.this, getString(R.string.connect), Toast.LENGTH_SHORT).show();
                  //  } else {
                 //       Toast.makeText(AddOutletActivity.this, getString(R.string.Could_not_found), Toast.LENGTH_SHORT).show();
                //    }
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
        LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(AddOutletActivity.this, mCompositeDisposable);


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
                           double latitude, double longitude, int creditLimit
                          ) {
        pd.setMessage(getString(R.string.creating_Dealer));
        pd.setCancelable(false);
        pd.show();

        CreateOutlet createOutlet = new CreateOutlet(outletTypeId, outletName, ownerName, address, thanaId, phone, latitude, longitude, outletImage,creditLimit);
        Log.d("response", new Gson().toJson(createOutlet));

        apiService.createOutlet(createOutlet).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if(mCompositeDisposable!=null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> response) {
                        Gson gson = new Gson();
                        System.out.println("Response code: " + response.code());
                        pd.dismiss();
                        if (response.code() == 200) {
                            Toast.makeText(AddOutletActivity.this, getString(R.string.dealer_created_msg), Toast.LENGTH_SHORT).show();
                            LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(AddOutletActivity.this, mCompositeDisposable);
                            loadLocalIntoBackground.loadOutlet(null);
                            try {
                                CreateOutlet createOutletResult = null;
                                if (response.body() != null) {
                                    createOutletResult = gson.fromJson(response.body().string(), CreateOutlet.class);
                                }
                                if (createOutletResult != null) {
                                    Toast.makeText(AddOutletActivity.this, createOutletResult.result, Toast.LENGTH_SHORT).show();
                                }

                                if(mOnOutletCreateListener!=null) mOnOutletCreateListener.onSuccess();
                                submit.setEnabled(false);
                                Intent intent = new Intent(getApplicationContext(), OutletActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (response.code() == 401) {
                            Toast.makeText(AddOutletActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                            if(mOnOutletCreateListener!=null) mOnOutletCreateListener.onFailure("Invalid Response from server");
                        } else {
                            Toast.makeText(AddOutletActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                            if(mOnOutletCreateListener!=null) mOnOutletCreateListener.onFailure("Server Error! Try Again Later!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Response", e.toString());
                        if(mOnOutletCreateListener!=null) mOnOutletCreateListener.onFailure("Res! "+ e.toString());
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

   /* public void getLocation() {
        SendUserLocationData sendUserLocationData = new SendUserLocationData(AddOutletActivity.this);
        sendUserLocationData.getInstantLocation(this, new SendUserLocationData.OnGetLocation() {
            @Override
            public void getLocation(Double latitude, Double longitude) {
                mCurrentLatitude = latitude;
                mCurrentLongitude = longitude;
            }
        });
    }*/

    private void checkPermission() {
        AccessPermission.accessPermission(AddOutletActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
   //     googleApiClient.disconnect();
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();
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


    public interface OnOutletCreateListener{
        void onSuccess();
        void onFailure(String reason);
    }
}

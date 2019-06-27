package com.humaclab.selliscope_mohammadi.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.humaclab.selliscope_mohammadi.model.Outlets;
import com.humaclab.selliscope_mohammadi.model.Thana.Thana;
import com.humaclab.selliscope_mohammadi.utils.DatabaseHandler;
import com.humaclab.selliscope_mohammadi.utils.NetworkUtility;
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

public class EditOutletActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    boolean isValidOutletName = true;
    boolean isValidOwnerName = true;
    boolean isValidAddress = true;
    boolean isValidPhone = true;
    //    double latitude, longitude = 0.0;
    int outletTypeId, thanaId = -1;
    private EditText outletName, outletAddress, outletOwner, outletContactNumber;
    private EditText etCreditLimit, etBankName, etBGAmount, etExpDate, etOwnerEmail;
    private Spinner spOutletPrefix, spPaymentType, outletType, district, thana;
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

    private Outlets.Successful.Outlet outlet;

    private List<Integer> prefixID = new ArrayList<>();
    private List<String> prefixName = new ArrayList<>();
    private int paymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_outlet);

        databaseHandler = new DatabaseHandler(this);
        pd = new ProgressDialog(this);
        outlet = (Outlets.Successful.Outlet) getIntent().getSerializableExtra("outletDetails");

        sessionManager = new SessionManager(this);
        email = sessionManager.getUserEmail();
        password = sessionManager.getUserPassword();
        apiService = SelliscopeApplication.getRetrofitInstance(email, password, false).create(SelliscopeApiEndpointInterface.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Outlet");
        setSupportActionBar(toolbar);
        outletName = findViewById(R.id.et_outlet_name);
        outletName.setText(outlet.outletName);

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

        spOutletPrefix = findViewById(R.id.sp_outlet_prefix);
        spPaymentType = findViewById(R.id.sp_payment_type);

        outletAddress = findViewById(R.id.et_outlet_address);
        outletAddress.setText(outlet.outletAddress);

        outletOwner = findViewById(R.id.et_outlet_owner_name);
        outletOwner.setText(outlet.ownerName);

        outletContactNumber = findViewById(R.id.et_outlet_contact_number);
        outletContactNumber.setText(outlet.phone);

        etOwnerEmail = findViewById(R.id.et_outlet_owner_email);
        etOwnerEmail.setText(outlet.outletEmail);

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
                Timber.d("Latitude" + outlet.outletLatitude);
                Timber.d("Long" + outlet.outletLongitude);
                Timber.d("Type id" + outletTypeId);
                Timber.d("Thana id" + thanaId);

                if (isValidAddress && isValidOutletName && isValidOwnerName
                        && isValidPhone && outlet.outletLatitude != 0.0 && outlet.outletLongitude != 0.0
                        && outletTypeId != -1 && thanaId != -1) {
                    Timber.d("addOutletRun");
                    if (NetworkUtility.isNetworkAvailable(EditOutletActivity.this)) {
                        if (spPaymentType.getSelectedItemPosition() > 0) {
                            if (!isEmpty(spPaymentType.getSelectedItemPosition())) {
                                updatedOutlet(email, password, outletTypeId, outletName.getText().toString().trim(),
                                        outletOwner.getText().toString().trim(),
                                        outletAddress.getText().toString().trim(), thanaId,
                                        outletContactNumber.getText().toString().trim(), outlet.outletLatitude, outlet.outletLongitude);
                            }
                        }
                    } else
                        Toast.makeText(EditOutletActivity.this, "Connect to Wifi or Mobile Data",
                                Toast.LENGTH_SHORT).show();
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

                        etCreditLimit.setVisibility(View.GONE);
                        break;
                    case 2:
                        paymentType = 2;
                        etCreditLimit.setVisibility(View.VISIBLE);

                        etBankName.setVisibility(View.GONE);
                        etBGAmount.setVisibility(View.GONE);
                        etExpDate.setVisibility(View.GONE);
                        break;
                    case 3:
                        etBankName.setVisibility(View.GONE);
                        etBGAmount.setVisibility(View.GONE);
                        etExpDate.setVisibility(View.GONE);
                        etCreditLimit.setVisibility(View.GONE);
                        break;
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
        if (selectedItemPosition == 0) {
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
        if (selectedItemPosition == 1) {
            if (etCreditLimit.getText().toString().isEmpty()) {
                etCreditLimit.setError(getString(R.string.error_field_required));
                view = etCreditLimit;
                view.requestFocus();
                count++;
            }
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
                List<PrefixItem> prefixItemList = response.body().getPrefixResult().getPrefix();

                for (PrefixItem prefixItem : prefixItemList) {
                    prefixID.add(prefixItem.getId());
                    prefixName.add(prefixItem.getPrefix());
                }
                spOutletPrefix.setAdapter(new ArrayAdapter<>(EditOutletActivity.this, android.R.layout.simple_list_item_1, prefixName));
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<PrefixResponse> call, Throwable t) {

            }
        });
    }

    private void updatedOutlet(String email, String password, int outletTypeId, String outletName,
                               String ownerName, String address, int thanaId, String phone,
                               double latitude, double longitude) {
        pd.setMessage("Outlet updating.....");
        pd.setCancelable(false);
        pd.show();

        Call<ResponseBody> call = apiService.updateOutlet(outlet.outletId, new CreateOutlet(
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
                pd.dismiss();
                Gson gson = new Gson();
                System.out.println("Response code: " + response.code());
                if (response.code() == 202) {
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
        super.onDestroy();
    }
}

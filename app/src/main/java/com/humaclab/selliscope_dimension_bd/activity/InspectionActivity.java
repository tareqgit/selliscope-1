package com.humaclab.selliscope_dimension_bd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_dimension_bd.R;
import com.humaclab.selliscope_dimension_bd.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_dimension_bd.SelliscopeApplication;
import com.humaclab.selliscope_dimension_bd.databinding.ActivityInspectionBinding;
import com.humaclab.selliscope_dimension_bd.model.InspectionResponse;
import com.humaclab.selliscope_dimension_bd.model.Outlets;
import com.humaclab.selliscope_dimension_bd.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InspectionActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    private SelliscopeApiEndpointInterface apiService;
    private ActivityInspectionBinding binding;
    private ProgressDialog pd;
    private String promotionImage;

    private List<Integer> outletIDs;
    private List<String> outletNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inspection);

        pd = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Inspection");
        setSupportActionBar(toolbar);

        getOutlets();

        binding.ivTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        binding.btnSubmitInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Submitting your inspection......");
                pd.setCancelable(false);
                pd.show();

                InspectionResponse.Inspection inspection = new InspectionResponse.Inspection();
                inspection.image = promotionImage;
                inspection.condition = binding.spCondition.getSelectedItem().toString();
                inspection.iDamaged = binding.spIsDamaged.getSelectedItem().toString().equals("Yes");
                inspection.outletID = outletIDs.get(binding.spOutlets.getSelectedItemPosition());
                try {
                    inspection.quantity = Integer.parseInt(binding.etQty.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                inspection.promotionType = binding.spPromotionType.getSelectedItem().toString();

                SessionManager sessionManager = new SessionManager(InspectionActivity.this);
                SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
                Call<InspectionResponse> call = apiService.inspectOutlet(inspection);
                call.enqueue(new Callback<InspectionResponse>() {
                    @Override
                    public void onResponse(Call<InspectionResponse> call, Response<InspectionResponse> response) {
                        pd.dismiss();
                        if (response.code() == 201) {
                            Toast.makeText(InspectionActivity.this, "Inspection sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (response.code() == 401) {
                            Toast.makeText(InspectionActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InspectionActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<InspectionResponse> call, Throwable t) {
                        pd.dismiss();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    void getOutlets() {
        SessionManager sessionManager = new SessionManager(InspectionActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getOutlets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        Outlets.Successful getOutletListSuccessful = gson.fromJson(response.body()
                                .string(), Outlets.Successful.class);
                        if (!getOutletListSuccessful.outletsResult.outlets.isEmpty()) {
                            outletIDs = new ArrayList<>();
                            outletNames = new ArrayList<>();

                            for (Outlets.Successful.Outlet outlet : getOutletListSuccessful.outletsResult.outlets) {
                                outletIDs.add(outlet.outletId);
                                outletNames.add(outlet.outletName);
                            }
                            binding.spOutlets.setAdapter(new ArrayAdapter<>(InspectionActivity.this, R.layout.spinner_item, outletNames));
                            if (getIntent().hasExtra("outletName")) {
                                binding.spOutlets.setSelection(outletNames.indexOf(getIntent().getStringExtra("outletName")));
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "You don't have any outlet in your list.", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(InspectionActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InspectionActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
            promotionImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            binding.ivTakeImage.setImageBitmap(photo);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

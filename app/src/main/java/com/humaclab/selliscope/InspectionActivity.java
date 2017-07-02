package com.humaclab.selliscope;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.databinding.ActivityInspectionBinding;
import com.humaclab.selliscope.model.InspectionResponse;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InspectionActivity extends AppCompatActivity {
    private ActivityInspectionBinding binding;

    private final int CAMERA_REQUEST = 3214;
    private String promotionImage;
    private int outletID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inspection);

        outletID = getIntent().getIntExtra("outletID", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Inspection");
        setSupportActionBar(toolbar);

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
                InspectionResponse.Inspection inspection = new InspectionResponse.Inspection();
                inspection.image = promotionImage;
                inspection.condition = binding.spCondition.getSelectedItem().toString();
                inspection.iDamaged = binding.spIsDamaged.getSelectedItem().toString().equals("Yes");
                inspection.outletID = outletID;
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
                        t.printStackTrace();
                    }
                });
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

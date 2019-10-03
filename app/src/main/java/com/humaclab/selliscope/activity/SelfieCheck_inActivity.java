package com.humaclab.selliscope.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.humaclab.selliscope.LocationMonitoringService;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.databinding.ActivitySelfieCheckInBinding;
import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.utility_db.db.UtilityDatabase;
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope.utils.GetAddressFromLatLang;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;
import com.mti.pushdown_ext_onclick_single.PushDown;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;
import com.mti.pushdown_ext_onclick_single.SingleClick;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfieCheck_inActivity extends AppCompatActivity {

    ActivitySelfieCheckInBinding mActivitySelfieCheckInBinding;
    private File output = null;
    public static final String AUTHORITY = "com.humaclab.selliscope.fileprovider";
    private final int CAMERA_REQUEST = 3214;
    public String selfieImage;
    UtilityDatabase mUtilityDatabase;
    private SelliscopeApiEndpointInterface apiService;
    public int outletId;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_selfie_check_in);
        mActivitySelfieCheckInBinding = DataBindingUtil.setContentView(this, R.layout.activity_selfie_check_in);
        mContext=this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Selfie Check-in");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        outletId = getIntent().getIntExtra("outletId", -1);


        Log.d("tareq_test", "SelfieCheck_inActivity #67: onCreate:  " + outletId);


        SessionManager sessionManager = new SessionManager(this);

        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);


        mUtilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(this);

        mActivitySelfieCheckInBinding.imageViewSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        output = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.d("tareq_test", "InspectionActivity #87: onCreate:  " + ex.getMessage());
                    }
                    // Continue only if the File was successfully created
                    if (output != null) {

                        Uri photoURI = FileProvider.getUriForFile(SelfieCheck_inActivity.this, AUTHORITY, output);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

       SingleClick.get(mActivitySelfieCheckInBinding.tvSend).setOnSingleClickListener(v -> {
           v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
            if (NetworkUtility.isNetworkAvailable(getApplicationContext())) {
                if (LocationMonitoringService.sLocation != null) {
                    List<UserLocation.Visit> visitList = new ArrayList<UserLocation.Visit>() ;

                    visitList.add(  new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(),LocationMonitoringService.sLocation.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(mContext,LocationMonitoringService.sLocation.getLatitude(),LocationMonitoringService.sLocation.getLongitude()),CurrentTimeUtilityClass.getCurrentTimeStamp(),outletId,selfieImage,mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));
                    apiService.sendUserLocation(new UserLocation(visitList)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(mContext, "Selfie Check-in Successful", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                                finish();

                            }else if(response.code()>=500 && response.code()<=599){
                                Executors.newSingleThreadExecutor().execute(()->{
                                    mUtilityDatabase.returnCheckInDao().addCheck_In(new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(), LocationMonitoringService.sLocation.getLongitude(), "", CurrentTimeUtilityClass.getCurrentTimeStamp(), outletId, selfieImage, mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));
                                    runOnUiThread(()->Toast.makeText(SelfieCheck_inActivity.this, "Selfie check-in Queued", Toast.LENGTH_SHORT).show());
                                    startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                                    finish();

                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Executors.newSingleThreadExecutor().execute(()->{
                                mUtilityDatabase.returnCheckInDao().addCheck_In(new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(), LocationMonitoringService.sLocation.getLongitude(), "", CurrentTimeUtilityClass.getCurrentTimeStamp(), outletId, selfieImage, mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));
                                runOnUiThread(()->Toast.makeText(SelfieCheck_inActivity.this, "Selfie check-in Queued", Toast.LENGTH_SHORT).show());
                                startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                                finish();

                            });
                        }
                    });
                }else
                    Toast.makeText(SelfieCheck_inActivity.this, "Please wait a while and try again after some minutes", Toast.LENGTH_SHORT).show();

            } else {
                Executors.newSingleThreadExecutor().execute(() -> {
                    if (LocationMonitoringService.sLocation != null) {
                        mUtilityDatabase.returnCheckInDao().addCheck_In(new UserLocation.Visit(LocationMonitoringService.sLocation.getLatitude(), LocationMonitoringService.sLocation.getLongitude(), "", CurrentTimeUtilityClass.getCurrentTimeStamp(), outletId, selfieImage, mActivitySelfieCheckInBinding.tvComment.getEditText().getText().toString()));
                        runOnUiThread(()->Toast.makeText(SelfieCheck_inActivity.this, "Selfie check-in Queued", Toast.LENGTH_SHORT).show());
                        startActivity(new Intent(SelfieCheck_inActivity.this, OutletActivity.class));
                        finish();
                    } else
                        runOnUiThread(()->  Toast.makeText(SelfieCheck_inActivity.this, "Please wait a while and try again after some minutes", Toast.LENGTH_SHORT).show());
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


            Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);
            final Uri imageUri = outputUri;
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap photo = BitmapFactory.decodeStream(imageStream);

                int factor = 10;
                while (photo.getWidth() > 600) {
                    photo = Bitmap.createScaledBitmap(photo, (Math.round(photo.getWidth() * 0.1f * factor)), (Math.round(photo.getHeight() * 0.1f * factor)), false);
                    factor--;
                }


                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                selfieImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);


                Glide.with(mActivitySelfieCheckInBinding.imageViewStore)
                        .load(photo)
                        // .transform(new CircleCrop())
                        .into(mActivitySelfieCheckInBinding.imageViewStore);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
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

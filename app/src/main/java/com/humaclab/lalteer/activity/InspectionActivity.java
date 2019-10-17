package com.humaclab.lalteer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.databinding.ActivityInspectionBinding;
import com.humaclab.lalteer.model.InspectionResponse;
import com.humaclab.lalteer.model.outlets.Outlets;
import com.humaclab.lalteer.utils.AccessPermission;
import com.humaclab.lalteer.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
    SessionManager sessionManager;
    private List<Integer> outletIDs;
    private List<String> outletNames;

    String currentPhotoPath;
    public static final String AUTHORITY = "com.humaclab.lalteer.fileprovider";
    private File output=null;

    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inspection);
        checkPermission();
        sessionManager = new SessionManager(InspectionActivity.this);
        pd = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.dashboard_inspection));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getOutlets();

        binding.ivTakeImage.setOnClickListener(new View.OnClickListener() {
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
                        Log.d("tareq_test", "InspectionActivity #87: onCreate:  "+ ex.getMessage());
                    }
                    // Continue only if the File was successfully created
                    if (output != null) {

                        Uri photoURI = FileProvider.getUriForFile(InspectionActivity.this, AUTHORITY,                            output);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

        binding.btnSubmitInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Submitting your inspection......");
                pd.setCancelable(false);
                pd.show();

                InspectionResponse.Inspection inspection = new InspectionResponse.Inspection();
                inspection.img = String.valueOf(promotionImage);
                inspection.condition = binding.spCondition.getSelectedItem().toString();
                inspection.damaged = binding.spIsDamaged.getSelectedItem().toString().equals("Yes") ? 1 : 0;

                inspection.dealer_id = outletIDs.get(binding.spOutlets.getSelectedItemPosition());
                try {
                    inspection.qty = Integer.parseInt(binding.etQty.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                inspection.promotion = binding.spPromotionType.getSelectedItem().toString();
                InspectionResponse.Inspection a =inspection;
                Log.d("response", new Gson().toJson(a));

                sessionManager = new SessionManager(InspectionActivity.this);
                apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
              Log.d("tareq_test" , ""+new Gson().toJson(a));
                Call<InspectionResponse> call = apiService.inspectOutlet(inspection);
                call.enqueue(new Callback<InspectionResponse>() {
                    @Override
                    public void onResponse(Call<InspectionResponse> call, Response<InspectionResponse> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            Toast.makeText(InspectionActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
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

        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);


        apiService.getOutlets().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<Outlets>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Response<Outlets> response) {

                        if (response.code() == 200) {


                            if (response.body() != null) {
                                if (!response.body().outletsResult.outlets.isEmpty()) {
                                    outletIDs = new ArrayList<>();
                                    outletNames = new ArrayList<>();

                                    for (Outlets.Outlet outlet : response.body().outletsResult.outlets) {
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
                            }

                        } else if (response.code() == 401) {
                            Toast.makeText(InspectionActivity.this, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InspectionActivity.this, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });



       /* call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });*/
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
                while (photo.getWidth() >= 1200) {
                    photo = Bitmap.createScaledBitmap(photo, (Math.round(photo.getWidth() * 0.1f * factor)), (Math.round(photo.getHeight() * 0.1f * factor)), false);
                    factor--;
                }


                Log.d("tareq_test", "InspectionActivity #205: onActivityResult:  " + photo.getHeight() + " , " + photo.getWidth());
                photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                promotionImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);


                Glide.with(binding.ivTakeImage)
                        .load(decodeBase64(promotionImage))
                        // .transform(new CircleCrop())
                        .into(binding.ivTakeImage);


                SharedPreferences sharedPreferencesLanguage = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencesLanguage.edit();
                editor.putString("img", promotionImage);
                editor.apply();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

           /* ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            assert photo != null;
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            promotionImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            binding.ivTakeImage.setImageBitmap(photo);*/
        }
    }
    private void checkPermission() {
        AccessPermission.accessPermission(InspectionActivity.this);
    }


    @Override
    public void onDestroy() {

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

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

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
}

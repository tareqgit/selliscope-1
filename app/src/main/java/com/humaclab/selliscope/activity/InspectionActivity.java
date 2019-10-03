package com.humaclab.selliscope.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.databinding.ActivityInspectionBinding;
import com.humaclab.selliscope.model.InspectionResponse;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
   public static final String AUTHORITY = "com.humaclab.selliscope.fileprovider";
    private File output=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inspection);

        pd = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.inspection));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getOutlets();

        binding.ivTakeImage.setOnClickListener(v -> {
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

        });

        binding.btnSubmitInspection.setOnClickListener(v -> {
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

            Log.d("tareq_test", "InspectionActivity #126: onCreate:  "+ new Gson().toJson(inspection));

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
                        Outlets getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.class);
                        if (!getOutletListSuccessful.outletsResult.outlets.isEmpty()) {
                            outletIDs = new ArrayList<>();
                            outletNames = new ArrayList<>();

                            for (Outlets.Outlet outlet : getOutletListSuccessful.outletsResult.outlets) {
                                outletIDs.add(outlet.outletId);
                                outletNames.add(outlet.outletName);
                            }
                            binding.spOutlets.setAdapter(new ArrayAdapter<>(InspectionActivity.this, R.layout.color_spinner_layout_black, outletNames));
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


        }
    }


    void sendMail(String msg){
        String mailto = "mailto:tareq.android@humaclab.com" +
                "?cc=" + "mti.tareq2@gmail.com" +
                "&subject=" + Uri.encode("Nai") +
                "&body=" + Uri.encode(msg);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
            Toast.makeText(this, "Email send", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
            Toast.makeText(InspectionActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


    public static Bitmap decodeBase64(String input)
    {
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

package com.easyopstech.easyops.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.RootApplication;
import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivityProfileBinding;
import com.easyopstech.easyops.model.update_profile.UpdateProfile;
import com.easyopstech.easyops.model.update_profile.UpdateProfileResponse;
import com.easyopstech.easyops.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Map;

import lib.kingja.switchbutton.SwitchMultiButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private final int CAMERA_REQUEST = 3214;
    private ActivityProfileBinding binding;
    private SessionManager sessionManager;
    private String profileImage = "";
    private RootApiEndpointInterface apiService;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        sessionManager = new SessionManager(this);
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(RootApiEndpointInterface.class);
        pd = new ProgressDialog(this);
        pd.setMessage("Updating profile...");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Update Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Map<String, String> map = sessionManager.getUserDetails();

/*        Picasso.get()
                .load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .into(binding.ivProfileImage);*/

        binding.tvEmail.setText(map.get("email"));
        binding.tvUserName.setText(map.get("userName"));
        binding.tvDateOfBirth.setText(map.get("dob"));

        String gender = sessionManager.getUserDetails().get("gender");

        binding.switchMultiButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {

            }
        });



        if (gender != null) {
            if (gender.equals("Male")) {

                binding.switchMultiButton.setSelectedTab(0);
              //  binding.rgGender.check(binding.rbMale.getId());
               /* binding.rbMale.setSelected(true);
                binding.rbFemale.setSelected(false);*/
            } else {

                binding.switchMultiButton.setSelectedTab(1);
               // binding.rgGender.check(binding.rbFemale.getId());
/*                binding.rbFemale.setSelected(true);
                binding.rbMale.setSelected(false);*/
            }
        }

        Log.d("tareq_test" , ""+sessionManager.getUserDetails().get("profilePictureUrl"));
        Glide.with(getApplicationContext())
                .load( sessionManager.getUserDetails().get("profilePictureUrl"))
                .placeholder(R.drawable.ic_employee)
                .thumbnail(0.1f)
                .transform(new CircleCrop())
                .into(binding.ivProfileImage);

        binding.ivProfileImage.setOnClickListener(v -> {
           /* ImagePicker.create(ProfileActivity.this)
                    .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                    .single() // single mode
//                .multi() // multi mode (default mode)
//                        .limit(10) // max images can be selected (99 by default)
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//                .origin(images) // original selected images, used in multi mode
//                .exclude(images) // exclude anything that in image.getPath()
//                .excludeFiles(files) // same as exclude but using ArrayList<File>
//                .theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
//                .enableLog(false) // disabling log
//                .imageLoader(new GrayscaleImageLoder()) // custom image loader, must be serializeable
                    .start(); // start image picker activity with request code*/

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
        });
        binding.tvDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.tvDateOfBirth.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        binding.btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.tvDateOfBirth.getText().toString().equals("")) {
                    if (!profileImage.equals("")) {
                        pd.show();
                     Log.d("tareq_test" , ""+profileImage);
                        UpdateProfile updateProfile = new UpdateProfile(
                                sessionManager.getUserDetails().get("userName"),
                                sessionManager.getUserEmail(),
                                binding.etAddress.getText().toString(),
                               // binding.rgGender.getCheckedRadioButtonId() == R.id.rb_male ? "Male" : "Female",
                                binding.switchMultiButton.getSelectedTab() == 0 ? "Male" : "Female",
                                binding.tvDateOfBirth.getText().toString(),
                                profileImage
                        );
                        Log.e("test", new Gson().toJson(updateProfile));

                        Call<UpdateProfileResponse> call = apiService.updateProfile(updateProfile);
                        call.enqueue(new Callback<UpdateProfileResponse>() {
                            @Override
                            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                                pd.dismiss();
                                if (!response.body().isError()) {
                                    sessionManager.updateProfile(response.body().getResult().getUser());
                                    Toast.makeText(getApplicationContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }

                            @Override
                            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                                pd.dismiss();
                                t.printStackTrace();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select your profile image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select your birth date.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            Bitmap bmp;
            ByteArrayOutputStream bos;
            try {
                bmp = BitmapFactory.decodeFile(image.getPath());
                binding.ivProfileImage.setImageBitmap(bmp);
                bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                profileImage = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            assert photo != null;
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            profileImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

            byte[] decode = Base64.decode(profileImage, Base64.DEFAULT);
            Glide.with(getApplicationContext())
                    .load(decode)
                    .transform(new CircleCrop())
                    .thumbnail(0.1f)
                    .into(binding.ivProfileImage);


        }
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

package com.humaclab.lalteer.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.databinding.ActivityProfileBinding;
import com.humaclab.lalteer.model.UpdateProfile.UpdateProfile;
import com.humaclab.lalteer.model.UpdateProfile.UpdateProfileResponse;
import com.humaclab.lalteer.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SessionManager sessionManager;
    private String profileImage = "";
    private SelliscopeApiEndpointInterface apiService;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);
        pd = new ProgressDialog(this);
        pd.setMessage("Updating profile...");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Map<String, String> map = sessionManager.getUserDetails();

        Picasso.with(this)
                .load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .into(binding.ivProfileImage);

        binding.tvEmail.setText(map.get("email"));
        binding.tvUserName.setText(map.get("userName"));
        binding.tvDateOfBirth.setText(map.get("dob"));
        binding.etAddress.setText(map.get("address"));
        if (map.get("gender") != null) {
            if (map.get("gender").equals("Male")) {
                binding.rbMale.setSelected(true);
                binding.rbFemale.setSelected(false);
            } else {
                binding.rbFemale.setSelected(true);
                binding.rbMale.setSelected(false);
            }
        }

/*        Glide.with(getApplicationContext()).load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .thumbnail(0.5f)
                .into(binding.ivProfileImage);*/

        Picasso.with(this)
                .load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .placeholder(R.drawable.default_profile_pic)
                .into(binding.ivProfileImage);

        binding.btnChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(ProfileActivity.this)
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
                        .start(); // start image picker activity with request code
            }
        });
        binding.btnChangeDateOfBirth.setOnClickListener(new View.OnClickListener() {
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

                        UpdateProfile updateProfile = new UpdateProfile(
                                sessionManager.getUserDetails().get("userName"),
                                sessionManager.getUserEmail(),
                                binding.etAddress.getText().toString(),
                                binding.rgGender.getCheckedRadioButtonId() == R.id.rb_male ? "Male" : "Female",
                                binding.tvDateOfBirth.getText().toString(),
                                profileImage
                        );
                        Log.e("test", new Gson().toJson(updateProfile));

                        Call<UpdateProfileResponse> call = apiService.updateProfile(updateProfile);
                        call.enqueue(new Callback<UpdateProfileResponse>() {
                            @Override
                            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                                pd.dismiss();
                                    if (!response.body().getError() == true) {
                                        sessionManager.updateProfile(response.body().getResult().getUser().getDob(),
                                                response.body().getResult().getUser().getGender(),
                                                response.body().getResult().getUser().getImage(),
                                                response.body().getResult().getUser().getAddress());
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

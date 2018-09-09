package com.humaclab.selliscope.activity;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.databinding.ActivityChangePasswordBinding;
import com.humaclab.selliscope.model.UpdatePassword.ChangePassword;
import com.humaclab.selliscope.model.UpdatePassword.ChangePasswordResponse;
import com.humaclab.selliscope.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Change Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this);

        binding.btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etCurrentPassword.getText().toString().equals(sessionManager.getUserPassword())) {
                    if (binding.etNewPassword.getText().toString().equals(binding.etReNewPassword.getText().toString())) {
                        pd.setMessage("Updating password...");
                        pd.show();
                        updatePassword();
                    } else {
                        View view = binding.etReNewPassword;
                        binding.etReNewPassword.setError("Password does not match");
                        view.requestFocus();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Your current password does not match.\nPlease give the correct current password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Glide.with(getApplicationContext()).load(sessionManager.getUserDetails().get("profilePictureUrl"))
                .thumbnail(0.5f)
                .into(binding.ivProfileImage);
    }

    private void updatePassword() {
        final ChangePassword changePassword = new ChangePassword();
        changePassword.setCurrentPassword(binding.etCurrentPassword.getText().toString());
        changePassword.setNewPassword(binding.etNewPassword.getText().toString());

        Call<ChangePasswordResponse> call = apiService.changePassword(changePassword);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                pd.dismiss();
                ChangePasswordResponse changePasswordResponse = response.body();
                if (!changePasswordResponse.isError()) {
                    sessionManager.setNewPassword(changePassword.getNewPassword());
                    Toast.makeText(getApplicationContext(), changePasswordResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), changePasswordResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    sessionManager.logoutUser(true);
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                pd.dismiss();
                t.printStackTrace();
            }
        });
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

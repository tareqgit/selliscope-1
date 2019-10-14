package com.humaclab.lalteer.activity;

import android.app.ProgressDialog;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.databinding.ActivityChangePasswordBinding;
import com.humaclab.lalteer.model.update_password.ChangePassword;
import com.humaclab.lalteer.model.update_password.ChangePasswordResponse;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private ProgressDialog pd;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        mContext=this;
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
    }

    private void updatePassword() {

        final ChangePassword changePassword = new ChangePassword();
        changePassword.setCurrentPassword(binding.etCurrentPassword.getText().toString());
        changePassword.setNewPassword(binding.etNewPassword.getText().toString());
        Log.d("tareq_test" , "pass-body "+ new Gson().toJson(changePassword));

        if(NetworkUtility.isNetworkAvailable(mContext)) {
            Call<ChangePasswordResponse> call = apiService.changePassword(changePassword);
            call.enqueue(new Callback<ChangePasswordResponse>() {
                @Override
                public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                    pd.dismiss();

                    if (response.isSuccessful()) {
                        //    ChangePasswordResponse changePasswordResponse = response.body();
                        if (response.body() != null) {
                            if (!response.body().isError()) {
                                sessionManager.setNewPassword(changePassword.getNewPassword());
                                Toast.makeText(getApplicationContext(), response.body().getResult(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), response.body().getResult(), Toast.LENGTH_SHORT).show();
                                sessionManager.logoutUser(true);
                            }
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "No response from server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Response failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                    pd.dismiss();
                    Log.d("tareq_test", "Change Pass failed: ");
                    Toast.makeText(ChangePasswordActivity.this, "Change Password failed due to Server communication failed. \n please check your network connection", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(mContext, "Netword Connection not Available please turn your network connection", Toast.LENGTH_SHORT).show();
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

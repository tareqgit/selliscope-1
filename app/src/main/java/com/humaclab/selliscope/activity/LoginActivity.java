package com.humaclab.selliscope.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.BuildConfig;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.model.IMEIandVerison;
import com.humaclab.selliscope.model.Login;
import com.humaclab.selliscope.utils.AccessPermission;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {
    private TextView forgotPassword;
    private EditText email, password;
    private Button signIn;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private ProgressBar loginProgresssBar;

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermission();

        //View version
        TextView tv_selliscope_version = findViewById(R.id.tv_selliscope_version);
        tv_selliscope_version.setText("Version - " + BuildConfig.VERSION_NAME);
        //View version

        loginProgresssBar = findViewById(R.id.pb_login);
        forgotPassword = findViewById(R.id.tv_forgot_password);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        signIn = findViewById(R.id.btn_sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidEmail = true;
                boolean isValidPassword = true;
                if (email.getText().toString().trim().isEmpty()) {
                    email.setError("Email is required!");
                    isValidEmail = false;
                } else if (!isValidEmail(email.getText().toString().trim())) {
                    email.setError("Invalid email address!");
                    isValidEmail = false;
                } else {
                    isValidEmail = true;
                }
                if (password.getText().toString().trim().isEmpty()) {
                    password.setError("Password is required!");
                    isValidPassword = false;
                } else {
                    isValidPassword = true;
                }
                if (isValidEmail && isValidPassword) {
                    if (NetworkUtility.isNetworkAvailable(LoginActivity.this) == true) {
                        loginProgresssBar.setVisibility(View.VISIBLE);
                        getUser(email.getText().toString().trim(), password.getText().toString()
                                .trim());
                    } else {
                        Toast.makeText(LoginActivity.this, "Network Unavailable.Please, connect " +
                                "to wifi or use mobile data.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    void getUser(final String email, final String password) {
        Timber.d(email + " " + password);
        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(email, password, true)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getUser();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 202) {
                    try {
                        Login.Successful loginSuccessful = gson.fromJson(response.body().string()
                                , Login.Successful.class);
                        Timber.d("Login Successful");

                        sessionManager.createLoginSession(
                                loginSuccessful.result.user.name,
                                loginSuccessful.result.clientId,
                                loginSuccessful.result.user.profilePictureUrl,
                                email,
                                password
                        );
                        loginProgresssBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        sendIMEIAndVersion();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    try {
                        loginProgresssBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this,
                                response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    loginProgresssBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            private void sendIMEIAndVersion() {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                    sendIMEIAndVersion();
                } else {
                    IMEIandVerison imeIandVerison = new IMEIandVerison();
                    imeIandVerison.setIMEIcode(telephonyManager.getDeviceId());
                    imeIandVerison.setAppVersion(BuildConfig.VERSION_NAME);
                    System.out.println("IMEI and version: " + new Gson().toJson(imeIandVerison));
                    Call<ResponseBody> call = apiService.sendIMEIAndVersion(imeIandVerison);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Timber.d("Status code: " + String.valueOf(response.code()));
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginProgresssBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());
            }
        });
    }

    private void checkPermission() {
        AccessPermission.accessPermission(LoginActivity.this);
    }
}

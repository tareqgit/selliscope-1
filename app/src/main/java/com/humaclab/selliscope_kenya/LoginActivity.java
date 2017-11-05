package com.humaclab.selliscope_kenya;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_kenya.Utils.NetworkUtility;
import com.humaclab.selliscope_kenya.Utils.SessionManager;
import com.humaclab.selliscope_kenya.model.Login;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {
    TextView forgotPassword;
    EditText email, password;
    Button signIn;
    SelliscopeApiEndpointInterface apiService;
    SessionManager sessionManager;
    ProgressBar loginProgresssBar;

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

        //View version
        TextView tv_selliscope_version = (TextView) findViewById(R.id.tv_selliscope_version);
        tv_selliscope_version.setText("Version - " + BuildConfig.VERSION_NAME);
        //View version

        loginProgresssBar = (ProgressBar) findViewById(R.id.pb_login);
        forgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        signIn = (Button) findViewById(R.id.btn_sign_in);
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
                        finish();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    try {
//                        Login.Unsuccessful loginUnsuccessful = gson.fromJson(response.errorBody()
//                                        .string()
//                                , Login.Unsuccessful.class);
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

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginProgresssBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());
            }
        });

    }
}

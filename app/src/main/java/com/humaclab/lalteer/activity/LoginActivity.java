package com.humaclab.lalteer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.BuildConfig;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.model.Dashboard.Access;
import com.humaclab.lalteer.model.IMEIandVerison;
import com.humaclab.lalteer.model.Login;
import com.humaclab.lalteer.utils.AccessPermission;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SessionManager;

import java.io.IOException;
import java.util.Locale;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView forgotPassword;
    private EditText email, password;
    private Button signIn;
    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    private ProgressBar loginProgresssBar;
    private RadioGroup rg_language;
    private RadioButton rbEnglish, rbBangla;
    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    private Realm realm;
    private Login.Successful.LoginResult loginResult;
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
        LoadLocale();
        setContentView(R.layout.activity_login);

        realm = Realm.getDefaultInstance();

        rg_language = findViewById(R.id.rg_language);
        rg_language.check(R.id.rbEnglish);
        rbBangla = findViewById(R.id.rbBangle);
        rbEnglish = findViewById(R.id.rbEnglish);
        rbBangla.setOnClickListener(this);
        rbEnglish.setOnClickListener(this);

        LoadPreferences();
        checkPermission();


/*        rg_language.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("chk", "id" + checkedId);
                if (checkedId == R.id.rbBangle) {
                    //some code
                    Toast.makeText(LoginActivity.this, "bangla", Toast.LENGTH_SHORT).show();
//                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "bn").apply();
//                    setLangRecreate("bn");
                } else if (checkedId == R.id.rbEnglish) {
                    //some code
                    Toast.makeText(LoginActivity.this, "English", Toast.LENGTH_SHORT).show();
//                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").apply();
//                    setLangRecreate("en");
                }

            }

        });*/
        //  rg_language.check(R.id.rbEnglish);

        //View version
        TextView tv_selliscope_version = findViewById(R.id.tv_selliscope_version);
        tv_selliscope_version.setText("Version - " + BuildConfig.VERSION_NAME);
        //View version

        loginProgresssBar = findViewById(R.id.pb_login);
        forgotPassword = findViewById(R.id.tv_forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, getString(R.string.contactAdmin), Toast.LENGTH_SHORT).show();
            }
        });
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        signIn = findViewById(R.id.btn_sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidEmail = true;
                boolean isValidPassword = true;
                if (email.getText().toString().trim().isEmpty()) {
                    email.setError(getString(R.string.emailRequired));
                    isValidEmail = false;
                }
                /*else if (!isValidEmail(email.getText().toString().trim())) {
                    email.setError("Invalid email address!");
                    isValidEmail = false;
                } */
                else {
                    isValidEmail = true;
                }
                if (password.getText().toString().trim().isEmpty()) {
                    password.setError(getString(R.string.passwordRequire));
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
                if (response.code() == 200) {
                    try {

                        loginResult = new Login.Successful().result;

                        Login.Successful loginSuccessful = gson.fromJson(response.body().string()
                                , Login.Successful.class);
                        Timber.d("Login Successful");

                        loginResult = loginSuccessful.result;

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(loginSuccessful.result.access);
                        realm.commitTransaction();
                        realm.close();
                        sessionManager.createLoginSession(
                                loginSuccessful.result.user.name,
                                loginSuccessful.result.clientId,
                                loginSuccessful.result.user.profilePictureUrl,
                                loginSuccessful.result.user.dob,
                                loginSuccessful.result.user.gender,
                                loginSuccessful.result.user.address,
                                email,
                                password
                        );
                        loginProgresssBar.setVisibility(View.INVISIBLE);

                        sendIMEIAndVersion();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

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
                            response.code()+
                            " Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
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


   /* public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_dialog, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);

        dialogBuilder.setTitle(getResources().getString(R.string.language_setting));
        dialogBuilder.setMessage(getResources().getString(R.string.language_setting_additional));
        dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spinner1.getSelectedItemPosition();
                switch (langpos) {
                    case 0: //English
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").apply();
                        setLangRecreate("en");
                        return;
                    case 1: //Bangla
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "bn").apply();
                        setLangRecreate("bn");
                        return;
                    default: //By default set to english
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").apply();
                        setLangRecreate("en");
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }*/

/*    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }*/


//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        Log.d("chk", "id" + checkedId);
//        if (checkedId == R.id.rbBangle) {
//            //some code
//            Toast.makeText(LoginActivity.this, "bangla", Toast.LENGTH_SHORT).show();
//                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "bn").apply();
//                    setLangRecreate("bn");
//        } else {
//            //some code
//            Toast.makeText(LoginActivity.this, "English", Toast.LENGTH_SHORT).show();
//                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").apply();
//                    setLangRecreate("en");
//        }
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.rbBangle) {
            //Do something
            SavePreferences(KEY_SAVED_RADIO_BUTTON_INDEX, 0);
            setLocale("bn");
            recreate();
           /* PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "bn").apply();
            setLangRecreate("bn");*/
            //  Toast.makeText(this, ""+checkedIndex, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.rbEnglish) {
            SavePreferences(KEY_SAVED_RADIO_BUTTON_INDEX, 1);
            setLocale("en");
            recreate();
            /*PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").apply();
            setLangRecreate("en");*/
            //   Toast.makeText(this, ""+checkedIndex, Toast.LENGTH_SHORT).show();
        }


    }

    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void LoadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        int savedRadioIndex = sharedPreferences.getInt(KEY_SAVED_RADIO_BUTTON_INDEX, 1);
        RadioButton savedCheckedRadioButton = (RadioButton) rg_language.getChildAt(savedRadioIndex);
        savedCheckedRadioButton.setChecked(true);
    }

    //Set Language
    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences sharedPreferencesLanguage = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesLanguage.edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }


    //Load language
    public void LoadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

}

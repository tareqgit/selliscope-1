package com.humaclab.selliscope.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ActivityVerificationBinding;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean mVerificationInProgress = false;
    ActivityVerificationBinding mBinding;
    //These are the objects needed
    //It is the verification id that will be sent to the user
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;


    //firebase auth object
    private FirebaseAuth mAuth;

    public int country_code=880; //for bd
    private String mPhone;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_verification);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        // Full Notch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //initializing objects
        mAuth = FirebaseAuth.getInstance();

        startCountDown();


  //      country_code=getIntent().getIntExtra("country_code",880);
        country_code=880;
        String phone = getIntent().getStringExtra("phone");
        phone="1838019021";
        mPhone = country_code + "" + phone;
        final String mobile= "+" + mPhone;


        mBinding.textViewPhone.setText(mobile);

        sendVerificationCode(mobile);

        PushDownAnim.setPushDownAnimTo(mBinding.textViewResend).setOnSingleClickListener(v->{
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
            Toast.makeText(this, "Sending verification code again!", Toast.LENGTH_SHORT).show();
            resendVerificationCode(mobile,mResendToken);
        });

        PushDownAnim.setPushDownAnimTo(mBinding.textViewNext).setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
            String code = mBinding.etOtp.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                mBinding.etOtp.setError("Enter valid code");
                mBinding.etOtp.requestFocus();
                return;
            }

            //verifying the code entered manually
            verifyVerificationCode(mVerificationId, code);
        });
    }

    private void startCountDown() {
        new CountDownTimer(60000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.textViewTimer.setText(String.format(Locale.ENGLISH, "0:%02d",(int) Math.floor(millisUntilFinished*.001)));
            }

            @Override
            public void onFinish() {
                startCountDown();
            }
        }.start();
    }


    private void sendVerificationCode(String mobile) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        mBinding.etOtp.requestFocus();

        mVerificationInProgress=true;
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                mBinding.etOtp.setText(code);
                //verifying the code
                // verifyVerificationCode(code);
            }
            mVerificationInProgress = false;

            mBinding.progressBar.setVisibility(View.INVISIBLE);

            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            mVerificationInProgress = false;

            mBinding.progressBar.setVisibility(View.INVISIBLE);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }
        }

        @Override
        public void onCodeSent(String varificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(varificationId, forceResendingToken);
            mVerificationId = varificationId;
            mResendToken = forceResendingToken;
        }
    };

    private void verifyVerificationCode(String verificationId,String code) {
        //creating the credential
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

            //signing the user
            signInWithPhoneAuthCredential(credential);

        } catch (Exception e) {
            Log.d("tareq_test", "VerificationActivity #179: verifyVerificationCode:  "+ e.getMessage());
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), "Verification Successful", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            Intent intent = new Intent(VerificationActivity.this, LoadingActivity.class);
                            intent.putExtra("phone",mPhone);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                           // overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", v -> {

                            });
                            snackbar.show();
                        }
                    }
                });
    }
    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks

        mVerificationInProgress = true;
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }
    // [

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


    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

package com.example.dankmemes2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    LottieAnimationView signupBackground;

    Button getOtpButton, verifyNowButton;
    TextView logInWithEmailTextView, otpTimerTextView;
    EditText phoneEditText, enterOtpEditText;
    CountryCodePicker ccp;

    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String verificationCodeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        getOtpButton = findViewById(R.id.getOtpButton);
        verifyNowButton = findViewById(R.id.verifyNowOtpButton);
        logInWithEmailTextView = findViewById(R.id.logInWithEmailTextView);
        otpTimerTextView = findViewById(R.id.otpTimerTextView);

        phoneEditText = findViewById(R.id.phoneEditText);
        enterOtpEditText = findViewById(R.id.enterOtpEditText);
        ccp = findViewById(R.id.ccp);

        firebaseSignIn();
        progressDialog.dismiss();
        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String onlyNo = phoneEditText.getText().toString();
                String phoneNo = ccp.getSelectedCountryCodeWithPlus()+phoneEditText.getText().toString();

                if(TextUtils.isEmpty(phoneNo)|| TextUtils.isEmpty(onlyNo)){
                    progressDialog.dismiss();

                    phoneEditText.setError("please enter phone number");
//                    Toast.makeText(PhoneLoginActivity.this, "please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    enterOtpEditText.setVisibility(View.VISIBLE);
                    verifyNowButton.setVisibility(View.VISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNo,
                            60,
                            TimeUnit.SECONDS,
                            PhoneLoginActivity.this,
                            callbacks
                    );

                    startTimer(60*1000, 1000);
                    getOtpButton.setVisibility(View.GONE);
                }

            }
        });

        verifyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String onlyNo = phoneEditText.getText().toString();
                String phoneNo = ccp.getSelectedCountryCodeWithPlus()+phoneEditText.getText().toString();
                String otp = enterOtpEditText.getText().toString();;

                if(TextUtils.isEmpty(phoneNo) || TextUtils.isEmpty(onlyNo)){
                    progressDialog.dismiss();

                    phoneEditText.setError("please enter phone number");
//                    Toast.makeText(PhoneLoginActivity.this, "please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(otp)){
                    progressDialog.dismiss();

                    enterOtpEditText.setError("please enter otp");
//                    Toast.makeText(PhoneLoginActivity.this, "please enter otp", Toast.LENGTH_SHORT).show();
                }
                else {

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeSent, otp);
                    signInWithCredential(credential);

                }


            }
        });

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        Toast.makeText(this, auth.getUid(), Toast.LENGTH_SHORT).show();
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();

                    startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    progressDialog.dismiss();

                    Toast.makeText(PhoneLoginActivity.this, "Incorrect Otp:"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startTimer(final long finish, long tick) {
        progressDialog.dismiss();

        otpTimerTextView.setVisibility(View.VISIBLE);

        CountDownTimer countDownTimer;
        countDownTimer = new CountDownTimer(finish, tick) {
            @Override
            public void onTick(long l) {
                long remindSec = l/1000;
                otpTimerTextView.setText("Retry After: "+ (remindSec/60) + ":" + (remindSec % 60));
            }

            @Override
            public void onFinish() {
                getOtpButton.setText("Regenerate Otp");
                getOtpButton.setVisibility(View.VISIBLE);
//                Toast.makeText(PhoneLoginActivity.this, "Finish", Toast.LENGTH_SHORT).show();

                otpTimerTextView.setVisibility(View.GONE);
                cancel();

            }
        }.start();
    }

    public void firebaseSignIn(){
        progressDialog.show();
        auth = FirebaseAuth.getInstance();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "verification failed:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressDialog.dismiss();

                verificationCodeSent = s;
                Toast.makeText(PhoneLoginActivity.this, "code sent", Toast.LENGTH_SHORT).show();
            }
        };

        logInWithEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneLoginActivity.this, LoginActivity.class));
                finish();
            }
        });

    }
}
package com.example.dankmemes2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PhoneSignupActivity extends AppCompatActivity {

    LottieAnimationView signupBackground;

    Button getOtpButton, verifyNowButton;
    TextView haveEmailTextView, otpTimerTextView;
    EditText nameEditText, addressEditText, phoneEditText, enterOtpEditText;
    CountryCodePicker ccp;

    CircularImageView profileImageView;
    private static final int PERMISSION_FILE = 23;  // for taking image
    private static final int ACCESS_FILE = 43;

    String imageURI; // To store default image uri/firebase image token i.e.,default profile_image

    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String verificationCodeSent;


    Uri imageUri;   // used to pic image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_signup);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        getOtpButton = findViewById(R.id.getOtpButton);
        verifyNowButton = findViewById(R.id.verifyNowOtpButton);
        haveEmailTextView = findViewById(R.id.haveEmailTextView);
        otpTimerTextView = findViewById(R.id.otpTimerTextView);
        profileImageView = findViewById(R.id.profileImageView);

        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        enterOtpEditText = findViewById(R.id.enterOtpEditText);
        ccp = findViewById(R.id.ccp);

        firebaseSignup();
        progressDialog.dismiss();
        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String onlyNo = phoneEditText.getText().toString();
                String phoneNo = ccp.getSelectedCountryCodeWithPlus()+phoneEditText.getText().toString();

                if(TextUtils.isEmpty(name)){
                    progressDialog.dismiss();

                    nameEditText.setError("please enter Your full name");
//                    Toast.makeText(PhoneSignupActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(address)){
                    progressDialog.dismiss();

                    addressEditText.setError("please enter Your Address");
//                    Toast.makeText(PhoneSignupActivity.this, "please enter Your Address", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phoneNo)|| TextUtils.isEmpty(onlyNo)){
                    progressDialog.dismiss();

                    phoneEditText.setError("please enter phone number");
//                    Toast.makeText(PhoneSignupActivity.this, "please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    enterOtpEditText.setVisibility(View.VISIBLE);
                    verifyNowButton.setVisibility(View.VISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNo,
                            60,
                            TimeUnit.SECONDS,
                            PhoneSignupActivity.this,
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

                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String onlyNo = phoneEditText.getText().toString();
                String phoneNo = ccp.getSelectedCountryCodeWithPlus()+phoneEditText.getText().toString();
                String otp = enterOtpEditText.getText().toString();;

                if(TextUtils.isEmpty(name)){
                    progressDialog.dismiss();

                    nameEditText.setError("please enter Your full name");
//                    Toast.makeText(PhoneSignupActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(address)){
                    progressDialog.dismiss();

                    addressEditText.setError("please enter Your Address");
//                    Toast.makeText(PhoneSignupActivity.this, "please enter Your Address", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phoneNo) || TextUtils.isEmpty(onlyNo)){
                    progressDialog.dismiss();

                    phoneEditText.setError("please enter phone number");
//                    Toast.makeText(PhoneSignupActivity.this, "please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(otp)){
                    progressDialog.dismiss();

                    enterOtpEditText.setError("please enter otp");
//                    Toast.makeText(PhoneSignupActivity.this, "please enter otp", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeSent, otp);
                    signInWithCredential(credential);

                }


            }
        });

    }



    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.show();

                    String name = nameEditText.getText().toString();
                    String address = addressEditText.getText().toString();
                    String phoneNo = ccp.getSelectedCountryCodeWithPlus()+phoneEditText.getText().toString();

                    // storage
                    DatabaseReference databaseReference = database.getReference().child("user").child(auth.getUid());
                    StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

                    if (imageUri !=null){  // if we choose/pic any profile pic from our phone storage
                        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageURI = uri.toString();
                                            Users Users = new Users(auth.getUid(), name, phoneNo, address, imageURI);
                                            databaseReference.setValue(Users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        progressDialog.dismiss();

                                                        Toast.makeText(PhoneSignupActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
//                                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                    }
                                                    else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(PhoneSignupActivity.this, "Error in creating a new user", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else {            // if user doesn't chose any profile pic then this access token url will be the profile pic
                        imageURI = "https://firebasestorage.googleapis.com/v0/b/menstruationcare-52744.appspot.com/o/profile_image.png?alt=media&token=b19905f7-83dc-481e-a049-19d4716aab23";
                        Users Users = new Users(auth.getUid(), name, phoneNo, address, imageURI);
                        databaseReference.setValue(Users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();

                                    Toast.makeText(PhoneSignupActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    progressDialog.dismiss();

                                    Toast.makeText(PhoneSignupActivity.this, "Error in creating a new user:"+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    progressDialog.dismiss();
                    startActivity(new Intent(PhoneSignupActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(PhoneSignupActivity.this, "Incorrect Otp:"+task.getException(), Toast.LENGTH_SHORT).show();
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
                otpTimerTextView.setText("Retry After: "+ (remindSec/60) + " : " + (remindSec % 60));
            }

            @Override
            public void onFinish() {
                getOtpButton.setText("Regenerate Otp");
                getOtpButton.setVisibility(View.VISIBLE);
//                Toast.makeText(PhoneSignupActivity.this, "Finish", Toast.LENGTH_SHORT).show();
                otpTimerTextView.setVisibility(View.GONE);
                cancel();

            }
        }.start();
    }

    public void firebaseSignup(){
        progressDialog.show();
        auth = FirebaseAuth.getInstance();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();

                Toast.makeText(PhoneSignupActivity.this, "Code Received", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                progressDialog.dismiss();

                Toast.makeText(PhoneSignupActivity.this, "verification failed:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressDialog.dismiss();

                verificationCodeSent = s;
                Toast.makeText(PhoneSignupActivity.this, "code sent", Toast.LENGTH_SHORT).show();
            }
        };

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(PhoneSignupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PhoneSignupActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FILE);
                }
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACCESS_FILE);
                }
            }

        });

        haveEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneSignupActivity.this, SignupActivity.class));
                finish();
            }
        });

    }

    // belows code is a part of profileImageView.clickListener

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_FILE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setActivityTitle("Crop Image")
                    .setFixAspectRatio(true)
                    .setCropMenuCropButtonTitle("Done")
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                profileImageView.setImageURI(imageUri); // setting image
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }



}
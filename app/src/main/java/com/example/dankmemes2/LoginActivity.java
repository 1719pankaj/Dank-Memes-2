package com.example.dankmemes2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextView signupTextView, forgotPasswordTextView;
    EditText emailEditText, passwordEditText, resetPasswordEditText;
    Button loginButton;

    LinearLayout phoneLoginLayout;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    LottieAnimationView loginBackground;


    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneLoginLayout = findViewById(R.id.phoneLoginLayout);

        loginBackground = findViewById(R.id.loginBackground);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

        signupTextView = findViewById(R.id.signupTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(TextUtils.isEmpty(email)){
                    progressDialog.dismiss();
                    emailEditText.setError("please enter email");
                    Toast.makeText(LoginActivity.this, "Enter valid data", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    passwordEditText.setError("please enter password");
                    Toast.makeText(LoginActivity.this, "Enter valid data", Toast.LENGTH_SHORT).show();
                }
                else if (!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    emailEditText.setError("invalid email");
                    Toast.makeText(LoginActivity.this, "invalid email", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6){
                    progressDialog.dismiss();
                    passwordEditText.setError("password length should be atleast 6");
                    Toast.makeText(LoginActivity.this, "Please enter valid password", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                if (!(currentUser.isEmailVerified())) {
                                    progressDialog.dismiss();

                                    Toast.makeText(LoginActivity.this, "Your email is not verified\nClick on verify now button to send verification code to your registered email id", Toast.LENGTH_LONG).show();

                                    Dialog dialog = new Dialog(LoginActivity.this,R.style.Dialog);
                                    dialog.setContentView(R.layout.verification_dialog_layout);
                                    dialog.show();
                                    Button verifyButton;
                                    verifyButton = dialog.findViewById(R.id.verifyButton);

                                    verifyButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    Toast.makeText(LoginActivity.this, "Verification link has been sent to your registered email id\nPlease verify that...", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    Toast.makeText(LoginActivity.this, "something went wrong"+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                                else {
                                    progressDialog.dismiss();

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Error in login! "+ Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        phoneLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PhoneLoginActivity.class));
                finish();
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(LoginActivity.this,R.style.Dialog);
                dialog.setContentView(R.layout.reset_password_dialog_layout);
                Button cancelButton, confirmButton;
                cancelButton = dialog.findViewById(R.id.cancelButton);
                confirmButton = dialog.findViewById(R.id.confirmButton);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetPasswordEditText = dialog.findViewById(R.id.resetPasswordEditText);
                        String registeredMail = resetPasswordEditText.getText().toString();
                        if (registeredMail.isEmpty() || !registeredMail.matches(emailPattern)){
                            resetPasswordEditText.setError("Please enter valid email");
                        }
                        else {
                            auth.sendPasswordResetEmail(registeredMail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(LoginActivity.this, "Reset password link has been sent to your registered emailId\nPlease reset your password from there...", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Error! Reset link is not sent: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        super.onBackPressed();
    }
}
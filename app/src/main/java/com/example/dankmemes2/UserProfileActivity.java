package com.example.dankmemes2;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {



    CircularImageView profileImageView;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;

    ProgressDialog progressDialog;
    public ArrayList<String> profileItems;

    LottieAnimationView headerBackground;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        profileImageView = findViewById(R.id.user_image);







//        View view = navigationView.getHeaderView(0);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);

//        progressDialog.show();    will use it later


        if (auth.getCurrentUser() == null){
//            Toast.makeText(this, "no user logined", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserProfileActivity.this, SignupActivity.class));
        }
        else {
            profileItems = new ArrayList<>();

            DatabaseReference databaseReference = firebaseDatabase.getReference().child("user");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren() ){
                        if (dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())){
                            Toast.makeText(UserProfileActivity.this, "uid matches", Toast.LENGTH_SHORT).show();
                            profileItems.add(dataSnapshot.child("name").getValue().toString());
                            profileItems.add(dataSnapshot.child("email").getValue().toString());
                            profileItems.add(dataSnapshot.child("phoneNo").getValue().toString());
                            profileItems.add(dataSnapshot.child("address").getValue().toString());
                            profileItems.add(dataSnapshot.child("imageUri").getValue().toString());

                            TextView nameTextView = findViewById(R.id.user_name);
                            TextView emailTextView = findViewById(R.id.user_email_id);
                            TextView phoneTextView = findViewById(R.id.user_phone);
                            TextView addressTextView = findViewById(R.id.user_address);
                            nameTextView.setText(profileItems.get(0));
                            emailTextView.setText(profileItems.get(1));
                            phoneTextView.setText(profileItems.get(2));
                            addressTextView.setText(profileItems.get(3));
                            try {      // uploading image not working
                                Picasso.get().load(profileItems.get(4)).into(profileImageView);
                            }
                            catch (Exception e){
                                Toast.makeText(UserProfileActivity.this, profileItems.get(4), Toast.LENGTH_SHORT).show();
                                Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("picaso's execption",e.getLocalizedMessage() );

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

}
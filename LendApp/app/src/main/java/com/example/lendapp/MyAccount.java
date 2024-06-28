package com.example.lendapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccount extends AppCompatActivity {

    private static final String TAG = "MyAccountActivity";

    private TextView UserfullName;
    private ImageButton editProfileButton;
    private RelativeLayout aboutUsButton, termsButton, onlineServicesButton, logoutButton;
    private LinearLayout homeButton, billButton, meButton;
    private ImageView userProfileImageView, coverPictureImageView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        UserfullName = findViewById(R.id.userfullname);
        userProfileImageView = findViewById(R.id.user_profile);
        coverPictureImageView = findViewById(R.id.cover_picture);
        logoutButton = findViewById(R.id.logout_btn);
        aboutUsButton = findViewById(R.id.about_us);
        termsButton = findViewById(R.id.terms_btn);
        onlineServicesButton = findViewById(R.id.online_services_btn);
        editProfileButton = findViewById(R.id.editProfile_btn);
        homeButton = findViewById(R.id.home_btn);
        billButton = findViewById(R.id.bill_btn);
        meButton = findViewById(R.id.me_btn);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        // Load profile data
        loadProfileData();

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(intent);
            finish();
        });

        // Set click listeners for the navigation buttons
        homeButton.setOnClickListener(v -> navigateTo(Home.class));
        billButton.setOnClickListener(v -> navigateTo(Community.class));

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Log.d(TAG, "User logged out. Redirecting to Login activity.");
            navigateTo(Login.class);
        });

        aboutUsButton.setOnClickListener(v -> navigateTo(AboutUs.class));
        termsButton.setOnClickListener(v -> navigateTo(Terms.class));
        onlineServicesButton.setOnClickListener(v ->
                Toast.makeText(MyAccount.this, "Online Services Clicked", Toast.LENGTH_SHORT).show());
    }

    private void loadProfileData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        UserfullName.setText(userProfile.fullName);

                        String profilePictureUrl = userProfile.profilePicture;
                        String coverPictureUrl = userProfile.coverPictureUrl;

                        Log.d(TAG, "Profile Picture URL: " + profilePictureUrl);
                        Log.d(TAG, "Cover Picture URL: " + coverPictureUrl);

                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Glide.with(MyAccount.this).load(profilePictureUrl).into(userProfileImageView);
                        } else {
                            Log.e(TAG, "Profile Picture URL is null or empty");
                        }

                        if (coverPictureUrl != null && !coverPictureUrl.isEmpty()) {
                            Glide.with(MyAccount.this).load(coverPictureUrl).into(coverPictureImageView);
                        } else {
                            Log.e(TAG, "Cover Picture URL is null or empty");
                        }
                    } else {
                        Log.e(TAG, "User profile is null");
                    }
                } else {
                    Log.e(TAG, "Data snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyAccount.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
                Log.e("LoadProfileError", databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
        finish();
    }
}

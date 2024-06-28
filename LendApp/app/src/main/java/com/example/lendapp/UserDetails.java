package com.example.lendapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class UserDetails extends AppCompatActivity {
    private TextView fullName, phoneNumber, email;
    private ImageView backArrow;
    private ImageView profileImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        fullName = findViewById(R.id.fullname);
        phoneNumber = findViewById(R.id.phoneNumber);
        backArrow = findViewById(R.id.back_arrow);
        email = findViewById(R.id.email);
        profileImage = findViewById(R.id.img);

        // Get data from intent
        String userFullName = getIntent().getStringExtra("fullName");
        String userPhoneNumber = getIntent().getStringExtra("phoneNumber");
        String userEmail = getIntent().getStringExtra("email");
        String userProfilePicture = getIntent().getStringExtra("profilePicture");

        // Set data to views
        fullName.setText(userFullName);
        phoneNumber.setText(userPhoneNumber);
        email.setText(userEmail);
        Glide.with(this)
                .load(userProfilePicture)
                .placeholder(R.drawable.placeholder) // Ensure you have a placeholder image in res/drawable
                .into(profileImage);

        // Handle back arrow click
        backArrow.setOnClickListener(v -> onBackPressed());
    }
}

package com.example.lendapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NonUserDashboard extends AppCompatActivity {

    private static final String TAG = "NonUserDashboardActivity";

    private RelativeLayout aboutUsButton, termsButton, onlineServicesButton, loginNow;
    private LinearLayout homeButton, Community, meButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_user_dashboard);

        loginNow = findViewById(R.id.login);
        aboutUsButton = findViewById(R.id.about_us);
        termsButton = findViewById(R.id.terms_btn);
        onlineServicesButton = findViewById(R.id.online_services_btn);

        homeButton = findViewById(R.id.home_btn);
        Community = findViewById(R.id.bill_btn);
        meButton = findViewById(R.id.me_btn);

        Community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(Community.class);
            }
        });

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(Login.class);
            }
        });

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(AboutUs.class);
            }
        });

        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(Terms.class);
            }
        });

        onlineServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NonUserDashboard.this, "Online Services Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listeners for the navigation buttons
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(Home.class);
            }
        });
    }

    // Helper method to navigate to another activity
    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
        finish();
    }
}

package com.example.lendapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private Button confirmButton;
    private LinearLayout meButton, communityButton, homeButton;
    private FirebaseAuth auth;
    private SeekBar seekBarLoanAmount;
    private TextView tvLoanAmount;
    private int selectedLoanAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        auth = FirebaseAuth.getInstance();

        homeButton = findViewById(R.id.home_btn);
        communityButton = findViewById(R.id.bill_btn);
        meButton = findViewById(R.id.me_btn);
        confirmButton = findViewById(R.id.confirm_btn);
        seekBarLoanAmount = findViewById(R.id.seekBarLoanAmount);
        tvLoanAmount = findViewById(R.id.tvLoanAmount);

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            communityButton.setVisibility(View.GONE);
        } else {
            communityButton.setVisibility(View.VISIBLE);
        }

        setupSeekBar();
        setupButtonListeners();
    }

    private void setupSeekBar() {
        seekBarLoanAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedLoanAmount = 10000 + progress * 100;
                tvLoanAmount.setText(String.format("%,d", selectedLoanAmount));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void setupButtonListeners() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    navigateTo(Login.class);
                } else {
                    navigateTo(ApplyLoan.class);
                }
            }
        });

        meButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    navigateTo(NonUserDashboard.class);
                } else {
                    navigateTo(MyAccount.class);
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    navigateTo(Home.class);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting Home activity", e);
                }
            }
        });

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    navigateTo(Community.class);
                } catch (Exception e) {
                    Log.e(TAG, "Error starting Bill activity", e);
                }
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

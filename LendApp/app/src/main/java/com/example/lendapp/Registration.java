package com.example.lendapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    EditText fullName, phoneNumber, email, password;
    Button registerButton, passwordLoginButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;  // Realtime Database reference
    CheckBox privacyPolicyCheckbox;
    TextView privacyPolicyLink;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), AuthorizationDescription.class));
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");  // Initialize Realtime Database

        fullName = findViewById(R.id.fullname);
        phoneNumber = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerButton = findViewById(R.id.login_register_button);
        passwordLoginButton = findViewById(R.id.password_login);
        progressBar = findViewById(R.id.progressbar);
        privacyPolicyCheckbox = findViewById(R.id.privacy_policy_checkbox);
        privacyPolicyLink = findViewById(R.id.privacy_policy_link);

        passwordLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(Login.class);
            }
        });

        privacyPolicyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(Terms.class);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserExists();
            }
        });
    }

    private void registerUserWithEmail() {
        final String regFullName = fullName.getText().toString().trim();
        final String regEmail = email.getText().toString().trim();
        final String regPhoneNumber = phoneNumber.getText().toString().trim();
        final String regPassword = password.getText().toString().trim();
        if (TextUtils.isEmpty(regFullName)) {
            Toast.makeText(Registration.this, "Enter Full Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(regEmail)) {
            Toast.makeText(Registration.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(regEmail).matches()) {
            Toast.makeText(Registration.this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(regPhoneNumber)) {
            Toast.makeText(Registration.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(regPassword)) {
            Toast.makeText(Registration.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!privacyPolicyCheckbox.isChecked()) {
            Toast.makeText(Registration.this, "Please agree to the Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(regEmail, regPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Registration.this, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show();
                                            navigateTo(EmailVerification.class, regFullName, regEmail, regPhoneNumber, regPassword);
                                        } else {
                                            Toast.makeText(Registration.this, "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Registration.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfUserExists() {
        final String regEmail = email.getText().toString().trim();
        final String regUsername = email.getText().toString().trim().split("@")[0];  // Extract username from email

        databaseReference.orderByChild("email").equalTo(regEmail).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, "Email already registered", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.orderByChild("username").equalTo(regUsername).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Registration.this, "Username already taken", Toast.LENGTH_SHORT).show();
                            } else {
                                registerUserWithEmail();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Registration.this, "Error checking registration: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Registration.this, "Error checking registration: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
        finish();
    }

    private void navigateTo(Class<?> cls, String fullName, String email, String phoneNumber, String password) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra("fullName", fullName);
        intent.putExtra("email", email);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }
}

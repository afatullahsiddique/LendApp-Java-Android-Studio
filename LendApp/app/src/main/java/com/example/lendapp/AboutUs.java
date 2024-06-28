package com.example.lendapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AboutUs extends AppCompatActivity {

    private ImageView backArrow;
    private LinearLayout aboutLendDo;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        auth = FirebaseAuth.getInstance();

        backArrow = findViewById(R.id.back_arrow);
        aboutLendDo = findViewById(R.id.about_LendDo);

        aboutLendDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(AboutMyApp.class);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
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
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
        finish();
    }
}

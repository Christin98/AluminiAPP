package com.project.major.alumniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.project.major.alumniapp.R;

public class SplashScreen extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        intent = new Intent(this, Login.class);

        new Handler().postDelayed(() -> startActivity(intent), 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

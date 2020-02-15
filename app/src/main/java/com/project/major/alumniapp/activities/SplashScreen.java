package com.project.major.alumniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.SessionManager;

public class SplashScreen extends AppCompatActivity {
    Intent intent;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sessionManager = new SessionManager(getApplicationContext());

        intent = new Intent(this, MainActivity.class);

        new Handler().postDelayed(() -> sessionManager.checkLogin(), 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

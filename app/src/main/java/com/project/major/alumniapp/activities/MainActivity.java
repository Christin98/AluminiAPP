package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.fragment.AboutUsFragment;
import com.project.major.alumniapp.fragment.ConnectFragment;
import com.project.major.alumniapp.fragment.EventFragment;
import com.project.major.alumniapp.fragment.FeedbackFragment;
import com.project.major.alumniapp.fragment.FeedsFragment;
import com.project.major.alumniapp.fragment.JobsFragment;
import com.project.major.alumniapp.fragment.MessageFragment;
import com.project.major.alumniapp.fragment.NotificationFragment;
import com.project.major.alumniapp.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.navigationBottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        drawerLayout = findViewById(R.id.drawer_lay);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle = new  ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        setNavDraw();
        getSupportActionBar().setTitle("Feeds");
        loadFragment(new FeedsFragment());
        drawerToggle.syncState();
    }

    private void setNavDraw() {
        drawerLayout = findViewById(R.id.drawer_lay);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile){
                    toolbar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    drawerLayout.closeDrawers();
                    return true;
                }else if (itemId == R.id.nav_messages){
                    toolbar.setTitle("Message");
                    fragment = new MessageFragment();
                    loadFragment(fragment);
                    drawerLayout.closeDrawers();
                    return true;
                }else if (itemId == R.id.nav_about_us){
                    toolbar.setTitle("About Us");
                    fragment = new AboutUsFragment();
                    loadFragment(fragment);
                    drawerLayout.closeDrawers();
                    return true;
                }else if (itemId == R.id.nav_feedback){
                    toolbar.setTitle("Feedback");
                    fragment = new FeedbackFragment();
                    loadFragment(fragment);
                    drawerLayout.closeDrawers();
                    return true;
                }else if (itemId == R.id.nav_logout){
                    startActivity(new Intent(MainActivity.this, Login.class));
                    drawerLayout.closeDrawers();
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()){
                case R.id.bottom_news:
                    toolbar.setTitle("Feeds");
                    fragment = new FeedsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_jobs:
                    toolbar.setTitle("Jobs");
                    fragment = new JobsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_connect:
                    toolbar.setTitle("Connect People");
                    fragment = new ConnectFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_event:
                    toolbar.setTitle("Events");
                    fragment = new EventFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_notification:
                    toolbar.setTitle("Notification");
                    fragment = new NotificationFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

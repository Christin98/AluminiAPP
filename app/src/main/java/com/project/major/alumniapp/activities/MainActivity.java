package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.fragment.ConnectFragment;
import com.project.major.alumniapp.fragment.EventFragment;
import com.project.major.alumniapp.fragment.FeedsFragment;
import com.project.major.alumniapp.fragment.JobsFragment;
import com.project.major.alumniapp.fragment.NotificationFragment;
import com.project.major.alumniapp.utils.BottomLayoutBehaviour;
import com.project.major.alumniapp.utils.GlideApp;
import com.project.major.alumniapp.utils.ImageUtils;
import com.project.major.alumniapp.utils.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    SessionManager sessionManager;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.navigationBottom);
        drawerLayout = findViewById(R.id.drawer_lay);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomLayoutBehaviour());
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setSupportActionBar(toolbar);
        drawerToggle = new  ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Feeds");
        setNavDraw();
        View hView = navigationView.getHeaderView(0);
        ImageView imgvw = hView.findViewById(R.id.nav_header_imageView);
        TextView tv = hView.findViewById(R.id.nav_header_textView);
        loadFragment(new FeedsFragment());
        drawerToggle.syncState();


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            token = task.getResult().getToken();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(firebaseUser.getUid());
            reference.child("token").setValue(token);
            reference.child("user_name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    tv.setText(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            reference.child("user_image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String url = dataSnapshot.getValue(String.class);
                    if (url.equals("default")){
                        imgvw.setImageResource(R.drawable.profle_user);
                    } else {
                        ImageUtils.loadImageCenterCrop(GlideApp.with(MainActivity.this), url, imgvw, 100, 100);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + "new_post");
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + "new_event");



//        user_tv.setText(name);
    }

    private void setNavDraw() {
        drawerLayout = findViewById(R.id.drawer_lay);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragment;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile){
                toolbar.setTitle("Profile");
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("uid", auth.getCurrentUser().getUid());
                startActivity(intent);
                drawerLayout.closeDrawers();
                return true;
//            }else if (itemId == R.id.nav_messages){
//                toolbar.setTitle("Message");
////                fragment = new MessageFragment();
////                loadFragment(fragment);
//                drawerLayout.closeDrawers();
//                return true;
//            }else if (itemId == R.id.nav_about_us){
//                toolbar.setTitle("About Us");
////                fragment = new AboutUsFragment();
////                loadFragment(fragment);
//                drawerLayout.closeDrawers();
//                return true;
//            }else if (itemId == R.id.nav_feedback){
//                toolbar.setTitle("Feedback");
////                fragment = new FeedbackFragment();
////                loadFragment(fragment);
//                drawerLayout.closeDrawers();
//                return true;
            }else if (itemId == R.id.nav_logout){
                startActivity(new Intent(MainActivity.this, Login.class));
                sessionManager.logoutUser();
                drawerLayout.closeDrawers();
                finish();
                return true;
            }
            return false;
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
                    toolbar.setTitle("Users");
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
//        transaction.addToBackStack();
        transaction.commit();
    }

}

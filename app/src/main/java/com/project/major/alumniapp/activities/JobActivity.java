package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.Jobs;

public class JobActivity extends AppCompatActivity {

    TextView companyName,jobdetail, jobprofile, lastdate, location,experience;

    FirebaseDatabase database;
    DatabaseReference reference;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        companyName = findViewById(R.id.companyNameD);
        jobdetail = findViewById(R.id.job_description);
        jobprofile = findViewById(R.id.job_profiled);
        lastdate = findViewById(R.id.last_dated);
        location = findViewById(R.id.job_location);
        experience = findViewById(R.id.exprienced);

        id = getIntent().getStringExtra("id");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("alumni_app").child("Jobs").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Jobs jobs = dataSnapshot.getValue(Jobs.class);

                companyName.setText(jobs.getCompanyName());
                jobdetail.setText(jobs.getJobDescription());
                jobprofile.setText(jobs.getJobProfile());
                lastdate.setText(jobs.getLastDate());
                location.setText(jobs.getLocation());
                experience.setText(jobs.getExperience());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

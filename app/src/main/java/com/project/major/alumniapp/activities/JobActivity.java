package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.Jobs;
import com.project.major.alumniapp.utils.ImageUtils;

import timber.log.Timber;

public class JobActivity extends AppCompatActivity {

    TextView companyName;
    TextView jobdetail;
    TextView jobprofile;
    TextView lastdate;
    TextView location;
    TextView experience;
    Button apply_btn;
    ImageView comapnyImage;

    FirebaseDatabase database;
    DatabaseReference reference;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        comapnyImage = findViewById(R.id.companyImageD);
        companyName = findViewById(R.id.companyNameD);
        jobdetail = findViewById(R.id.job_description);
        jobprofile = findViewById(R.id.job_profiled);
        lastdate = findViewById(R.id.last_dated);
        location = findViewById(R.id.job_location);
        apply_btn = findViewById(R.id.apply_btnn);
        experience = findViewById(R.id.exprienced);

        id = getIntent().getStringExtra("id");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("alumni_app").child("Jobs").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Jobs jobs = dataSnapshot.getValue(Jobs.class);

                Timber.e("Jobs"+id);

                if (jobs != null) {
                    if (jobs.getCompanyImage() == null) {
                        comapnyImage.setVisibility(View.GONE);
                    } else {
                        ImageUtils.loadImageWithOutTransition(JobActivity.this, jobs.getCompanyImage(), comapnyImage);
                    }

                    companyName.setText(jobs.getCompanyName());
                    jobdetail.setText(jobs.getJobDescription());
                    jobprofile.setText(jobs.getJobProfile());
                    lastdate.setText(jobs.getLastDate());
                    location.setText(jobs.getLocation());
                    experience.setText(jobs.getExperience());

                    if (jobs.getApplyLink() == null) {
                        apply_btn.setVisibility(View.GONE);
                    } else {
                        apply_btn.setOnClickListener(v -> {
                            String link = jobs.getApplyLink();
                            if (link.startsWith("https://") || link.startsWith("http://")) {
                                Uri uri = Uri.parse(link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } else {
                                String url = "http://" + link;
                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

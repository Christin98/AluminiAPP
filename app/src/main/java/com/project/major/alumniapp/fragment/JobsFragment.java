package com.project.major.alumniapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.AddJob;
import com.project.major.alumniapp.adapter.JobsAdapter;
import com.project.major.alumniapp.models.Jobs;

import java.util.ArrayList;

import timber.log.Timber;


public class JobsFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button addEvent;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private JobsAdapter adapter;
    private FirebaseAuth mAuth;
   private ArrayList<Jobs> list;

    public JobsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        mAuth = FirebaseAuth.getInstance();
        addEvent = view.findViewById(R.id.addJob);
        recyclerView = view.findViewById(R.id.job_recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("Jobs");
        databaseReference.keepSynced(true);
        list = new ArrayList<>();
        getJobs();
        adapter = new JobsAdapter((AppCompatActivity) getActivity(), R.layout.job_item, list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() -1);
        addEvent.setOnClickListener(v -> startActivity(new Intent(getContext(), AddJob.class)));
        list.clear();
        return view;
    }

    private void getJobs() {
        Timber.e("getJobs", "function list jobs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnapshot1: dataSnapshot.getChildren()) {
                    Jobs jobs = datasnapshot1.getValue(Jobs.class);
                    list.add(jobs);
                    adapter.notifyDataSetChanged();
                    Timber.e("Job " + jobs.getCompanyName(), jobs.getCompanyName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

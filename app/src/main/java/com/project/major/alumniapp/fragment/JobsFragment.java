package com.project.major.alumniapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.AddJob;
import com.project.major.alumniapp.models.Jobs;


public class JobsFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button addEvent;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private boolean mLikedByCurrentUser = false;
    private String likeId;
    private String mLikesString;
    private StringBuilder mStringBuilder;

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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("Jobs");
        databaseReference.keepSynced(true);

        addEvent.setOnClickListener(v -> startActivity(new Intent(getContext(), AddJob.class)));

        return view;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("alumni_app").child("Jobs");

        FirebaseRecyclerOptions<Jobs> options = new FirebaseRecyclerOptions.Builder<Jobs>().setQuery(query, Jobs.class).build();

        adapter = new FirebaseRecyclerAdapter<Jobs, JobsFragment.ViewHolder>(options) {

            @NonNull
            @Override
            public JobsFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.job_item, parent, false);

                return new JobsFragment.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull JobsFragment.ViewHolder holder, int position, @NonNull Jobs jobs) {
                String post_key = getRef(position).getKey();
                if (jobs.getCompanyImage() != null) {
                    Glide.with(getContext())
                            .load(jobs.getCompanyImage())
                            .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.postimg).diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(R.drawable.error_img))
                            .into(holder.companyImage);
                    holder.companyImage.setVisibility(View.VISIBLE);
                } else {
                    holder.companyImage.setVisibility(View.GONE);
                }
                holder.companyName.setText(jobs.getCompanyName());
                holder.jobProfile.setText(jobs.getJobProfile());
                holder.lastDate.setText(jobs.getLastDate());
                holder.experience.setText(jobs.getExperience());
                holder.location.setText(jobs.getLocation());
            }
        };
        recyclerView.setAdapter(adapter);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView companyImage;
        TextView companyName;
        TextView jobProfile;
        TextView lastDate;
        TextView experience;
        TextView location;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyImage = itemView.findViewById(R.id.companyImage);
            companyName = itemView.findViewById(R.id.companyName);
            jobProfile = itemView.findViewById(R.id.job_profile);
            lastDate = itemView.findViewById(R.id.last_date);
            experience = itemView.findViewById(R.id.exprience);
            location = itemView.findViewById(R.id.location);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

}

package com.project.major.alumniapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.AddEvent;
import com.project.major.alumniapp.adapter.EventAdapter;
import com.project.major.alumniapp.models.Event;
import com.project.major.alumniapp.models.User;

import java.util.ArrayList;
import java.util.Objects;


public class EventFragment extends Fragment {

    private RecyclerView mainList;
    private Context context;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ArrayList<Object> mediaList;
    private EventAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar pb;
    private Button add_event;
    private String type;


    public EventFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mainList = view.findViewById(R.id.event_recyclerView);
        pb = view.findViewById(R.id.progress);
        add_event = view.findViewById(R.id.addEvent);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mainList.setLayoutManager(linearLayoutManager);
        mainList.setHasFixedSize(true);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mediaList = new ArrayList<>();
        myRef = database.getReference("alumni_app");


        getPhoto();
        getEvents();

        adapter = new EventAdapter(getActivity(), R.layout.event_item, mediaList);
        mainList.setAdapter(adapter);
        mainList.scrollToPosition(adapter.getItemCount() -1);

        mediaList.clear();
        add_event.setVisibility(View.GONE);
        myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("user_type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                type = dataSnapshot.getValue(String.class);
                if ((type.equals("admin")) || (type.equals("super_admin"))) {
                    add_event.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add_event.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEvent.class)));

    }



    private void getPhoto(){

        Query query = myRef.child("Events").child("photos").orderByChild("date_added");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Event event = ds.getValue(Event.class);
                    mediaList.add(0,event);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void getEvents(){

        Query query = myRef.child("Events").child("textevents").orderByChild("date_added");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Event event = ds.getValue(Event.class);
                    mediaList.add(event);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
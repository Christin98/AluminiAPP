package com.project.major.alumniapp.fragment;

import android.content.Context;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.adapter.EventAdapter;
import com.project.major.alumniapp.models.Event;
import com.project.major.alumniapp.utils.AddEvent;

import java.util.ArrayList;
import java.util.List;


public class EventFragment extends Fragment {

    private List<Event> list =new ArrayList<>();

    public EventFragment(){
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        Button addEvent = view.findViewById(R.id.addEvent);
        RecyclerView recyclerView = view.findViewById(R.id.event_recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        final EventAdapter eventAdapter = new  EventAdapter(list);
        recyclerView.setAdapter(eventAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("alumni_app").getRef().child("Events");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Event listdata = dataSnapshot1.getValue(Event.class);
                    list.add(listdata);

                }
                eventAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addEvent.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddEvent.class));
        });

        return view;
    }

}

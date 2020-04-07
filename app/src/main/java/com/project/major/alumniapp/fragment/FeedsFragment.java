package com.project.major.alumniapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.AddFeeds;
import com.project.major.alumniapp.activities.CommentsActivity;
import com.project.major.alumniapp.adapter.FeedAdapter;
import com.project.major.alumniapp.models.Event;
import com.project.major.alumniapp.models.Feeds;
import com.project.major.alumniapp.models.NotificationModel;
import com.project.major.alumniapp.models.Photo;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.utils.FcmNotification;
import com.project.major.alumniapp.utils.HeartAnimation;
import com.project.major.alumniapp.utils.LoadingDialog;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedsFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button addEvent;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FeedAdapter adapter;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private HeartAnimation heartAnimation;
    private boolean mLikedByCurrentUser = false;
    private String likeId;
    private String mLikesString;
    private StringBuilder mStringBuilder;
    private LoadingDialog loadingDialog;
    private ArrayList<Object> mediaList;
    private TextView textView;
    private ProgressBar pb;
    private Context context;

    public FeedsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        addEvent = view.findViewById(R.id.addFeed);
        recyclerView = view.findViewById(R.id.feed_recyclerView);
        textView = view.findViewById(R.id.textView4);
        pb = view.findViewById(R.id.progress);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app");
        databaseReference.keepSynced(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mediaList = new ArrayList<>();

        getPhoto();
        getVideo();
        getEvent();

        addEvent.setOnClickListener(v -> startActivity(new Intent(getContext(), AddFeeds.class)));

        adapter = new FeedAdapter(Objects.requireNonNull(getActivity()), R.layout.feed_item, mediaList);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() -1);
    }


    private void getPhoto(){

        Query query = databaseReference.child("Feeds").child("photos").orderByChild("timestamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Photo photo = ds.getValue(Photo.class);
                    mediaList.add(0,photo);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void getVideo(){

        Query query = databaseReference.child("Feeds").child("videos").orderByChild("timestamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Event event = ds.getValue(Event.class);
                    mediaList.add(event);
                    adapter.notifyDataSetChanged();
                }
//                Collections.sort(mediaList, new Comparator<Object>() {
//                    @Override
//                    public int compare(Object o1, Object o2) {
//                        Log.d("TAG","Comparing...");
//                        if (o1.getClass() == Video.class && o2.getClass() == Video.class) {
//                            Video p1 = (Video) o1;
//                            Video p2 = (Video) o2;
//                            return p2.getDate_added().compareTo(p1.getDate_added());
//                        }
//                        return 0;
//                    }
//                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getEvent(){

        Query query = databaseReference.child("Feeds").child("textfeed").orderByChild("timestamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Event event = ds.getValue(Event.class);
                    mediaList.add(1,event);
                    adapter.notifyDataSetChanged();
                }
//                Collections.sort(mediaList, new Comparator<Object>() {
//                    @Override
//                    public int compare(Object o1, Object o2) {
//                        Log.d("TAG","Comparing...");
//                        if (o1.getClass() == Video.class && o2.getClass() == Video.class) {
//                            Video p1 = (Video) o1;
//                            Video p2 = (Video) o2;
//                            return p2.getDate_added().compareTo(p1.getDate_added());
//                        }
//                        return 0;
//                    }
//                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

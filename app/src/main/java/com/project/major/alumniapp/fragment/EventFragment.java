package com.project.major.alumniapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.CommentsActivity;
import com.project.major.alumniapp.models.Event;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.activities.AddEvent;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class EventFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button addEvent;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    //    private HeartAnimation heartAnimation;
    private boolean mLikedByCurrentUser = false;
    private String likeId;
    private String mLikesString;
    //    private FirebaseMethods firebaseMethods;
    private StringBuilder mStringBuilder;
//    private Photo photo;
//    private Video video;

    public EventFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        mAuth = FirebaseAuth.getInstance();
        addEvent = view.findViewById(R.id.addEvent);
        recyclerView = view.findViewById(R.id.event_recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
//        final EventAdapter eventAdapter = new EventAdapter(list);
//        recyclerView.setAdapter(eventAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("Events");
        databaseReference.keepSynced(true);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    Event listdata = dataSnapshot1.getValue(Event.class);
//                    list.add(listdata);
//
//                }
//                eventAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        addEvent.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddEvent.class));
        });

        return view;
    }


    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("alumni_app").child("Events");

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();

        adapter = new FirebaseRecyclerAdapter<Event, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_item, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Event event) {
                String post_key = getRef(position).getKey();
                holder.title.setText(event.getEv_name());
                holder.desc.setText(event.getDesc());
                holder.name.setText(event.getEmail());
                setLikeListeners(holder.heartOutline, holder.heartRed, post_key, holder.likedBy);
                setUserLikes(holder.heartOutline,holder.heartRed,post_key,holder.likedBy);
                launchComment(post_key, holder);
                if (event.geturl() != null) {
//            Glide.with(context)
//                    .load(data.geturl())
//                    .apply(new RequestOptions()
//                    .placeholder(R.drawable.postimg))
//                    .into(myHolder.postimg);
                    Picasso.get()
                            .load(event.geturl())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .resize(300, 300)
                            .placeholder(R.drawable.postimg)
                            .error(R.drawable.postimg)
                            .into(holder.postimg);
                    holder.postimg.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.GONE);
                } else {
                    holder.postimg.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);

    }

    private void launchComment(String mediaId,ViewHolder view) {

        ImageView comment = view.comment;
        TextView viewComments = view.viewComments;
        final Intent mediaIntent = new Intent(getContext(), CommentsActivity.class);
        mediaIntent.putExtra("mediaID", mediaId);
        mediaIntent.putExtra("profile_photo", profileImgUrl);

        final View.OnClickListener onClickListener = v -> getContext().startActivity(mediaIntent);
        viewComments.setOnClickListener(onClickListener);

        comment.setOnClickListener(onClickListener);
    }

    private void setLikeListeners(final ImageView heartOutline, final ImageView heartRed, String key, final TextView likedBy) {

        final View.OnClickListener onClickListener = v -> toggleLike(heartOutline, heartRed, key, likedBy);
        heartOutline.setOnClickListener(onClickListener);
        heartRed.setOnClickListener(onClickListener);
    }

    private void toggleLike(final ImageView heartOutline, final ImageView heartRed, String key, final TextView likedBy) {

        mLikedByCurrentUser = false;
        Query query = FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Events").child(key).child("likes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    heartRed.setVisibility(View.GONE);
                    heartOutline.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (Objects.requireNonNull(ds.child("user_id").getValue()).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            mLikedByCurrentUser = true;
                            likeId = ds.getKey();
                            Log.d("TAG", "LikeID = " + likedBy);
                            heartOutline.setVisibility(View.GONE);
                            heartRed.setVisibility(View.VISIBLE);
//                                heartAnimation.toggleLike(heartOutline, heartRed);
                            removeNewLike( key, likeId);
                            setUserLikes(heartOutline, heartRed,  key, likedBy);
                        }
                    }
                }

                if (!mLikedByCurrentUser) {
                    Log.d("TAG", "Datasnapshot doesn't exists");
                    heartOutline.setVisibility(View.VISIBLE);
                    heartRed.setVisibility(View.GONE);
//                    heartAnimation.toggleLike(heartOutline, heartRed);
                    addNewLike(key);
                    setUserLikes(heartOutline, heartRed, key ,likedBy);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void setUserLikes(final ImageView heartOutline, final ImageView heartRed, String mediaId, final TextView likedBy){

        Query query = databaseReference.child(mediaId)
                .child("likes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    heartOutline.setVisibility(View.VISIBLE);
                    heartRed.setVisibility(View.GONE);
                    likedBy.setText("");
                }else {
                    heartRed.setVisibility(View.GONE);
                    heartOutline.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.child("user_id").getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            heartOutline.setVisibility(View.GONE);
                            heartRed.setVisibility(View.VISIBLE);
                        }
                        setLikeText(ds,likedBy);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    private void setLikeText(DataSnapshot dataSnapshot, final TextView likedBy){

        mStringBuilder = new StringBuilder();
        Query query = FirebaseDatabase.getInstance().getReference().child("alumni_app").child("users").orderByChild("uid")
                .equalTo(dataSnapshot.child("user_id").getValue().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mStringBuilder.append(Objects.requireNonNull(singleSnapshot.getValue(User.class)).getUser_name());
                    mStringBuilder.append(",");
                }
                String[] splitUsers = mStringBuilder.toString().split(",");

                int length = splitUsers.length;
                if(length == 1){
                    mLikesString = "Liked by " + splitUsers[0];
                }
                else if(length == 2){
                    mLikesString = "Liked by " + splitUsers[0]
                            + " and " + splitUsers[1];
                }
                else if(length == 3){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + " and " + splitUsers[2];

                }
                else if(length == 4){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + ", " + splitUsers[2]
                            + " and " + splitUsers[3];
                }
                else if(length > 4){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + ", " + splitUsers[2]
                            + " and " + (dataSnapshot.getChildrenCount() - 3) + " others";
                }
                likedBy.setText(mLikesString);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewLike(String mediaId){

        String likesId = firebaseDatabase.getReference().push().getKey();
        databaseReference.child(mediaId).child("likes").child(likesId).child("user_id").setValue(mAuth.getCurrentUser().getUid());
    }


    private void removeNewLike(String mediaId, String likesId){

        databaseReference.child(mediaId).child("likes").child(likesId).removeValue();
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,desc,name;
        TextView likedBy;
        ImageView postimg;
        ImageView heartOutline;
        ImageView heartRed;
        CircleImageView circleImageView;
        ImageView comment;
        TextView viewComments;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_name);
            desc = itemView.findViewById(R.id.caption_text);
            name = itemView.findViewById(R.id.user_name);
            likedBy = itemView.findViewById(R.id.like_number);
            postimg = itemView.findViewById(R.id.media_post);
            heartOutline = itemView.findViewById(R.id.heart_outline);
            heartRed = itemView.findViewById(R.id.heart_red);
            circleImageView = itemView.findViewById(R.id.profile_photo);
            comment = itemView.findViewById(R.id.comment);
            viewComments = itemView.findViewById(R.id.view_comments);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}


package com.project.major.alumniapp.adapter;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.Event;
import com.project.major.alumniapp.models.Like;
import com.project.major.alumniapp.models.Photo;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.models.Video;
import com.project.major.alumniapp.utils.ImageUtils;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private int mResource;
    private Activity mContext;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private boolean mLikedByCurrentUser = false;
    private String likeId;
    private String mLikesString;
    private StringBuilder mStringBuilder;
    private Photo photo;
    private Video video;
    private List<Object> list;

    public EventAdapter(@NonNull Activity context, int resource, @NonNull List<Object> list) {
        mResource = resource;
        mContext = context;
        this.list = list;
        reference = FirebaseDatabase.getInstance().getReference("alumni_app");
        mAuth = FirebaseAuth.getInstance();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView post;
        ProgressBar progressBar;
        TextView likedBy;
        CircleImageView profileImage;
        TextView userName;
        ImageView vidInd;
        RelativeLayout media_layout;
        LikeButton like;
        LikeButton unlike;
        ImageView comments;
        TextView caption;
        TextView date_added;
        TextView time;
        TextView date;
        TextView location;
        TextView eventName;
        TextView tags;
        TextView view_comments;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            post = itemView.findViewById(R.id.media_post);
            progressBar = itemView.findViewById(R.id.progressBar);
            likedBy = itemView.findViewById(R.id.like_number);
            profileImage = itemView.findViewById(R.id.profile_photo);
            userName = itemView.findViewById(R.id.user_name);
            vidInd = itemView.findViewById(R.id.video_indicater);
            like = itemView.findViewById(R.id.thumb_button);
            unlike = itemView.findViewById(R.id.thumb_button2);
            caption = itemView.findViewById(R.id.caption_text);
            date_added = itemView.findViewById(R.id.date_added);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);
            eventName = itemView.findViewById(R.id.event_name);
            tags = itemView.findViewById(R.id.tags);
            comments = itemView.findViewById(R.id.comment);
            view_comments = itemView.findViewById(R.id.view_comments);
            media_layout = itemView.findViewById(R.id.media_views);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Object object = list.get(position);
        Event event = (Event) object;
            setProfile(event.getUser_id(), holder.profileImage);
            holder.userName.setText(event.getUser_name());
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(event.getDate_added()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.date_added.setText(timeAgo);
            if (event.getImageurl() == null) {
                holder.media_layout.setVisibility(View.GONE);
                setLike(holder.like, "textevents", event.getId());
                setDislike(holder.unlike, "textevents", event.getId());
                setUserLike("textevents", event.getId(), holder.likedBy);
                likeordislike(holder.like, holder.unlike, "textevents", event.getId(), holder.likedBy);
            } else {
                ImageUtils.loadImageWithTransition(mContext,event.getImageurl(),holder.post,holder.progressBar);
                setLike(holder.like, "photos", event.getId());
                setDislike(holder.unlike, "photos", event.getId());
                setUserLike("photos", event.getId(), holder.likedBy);
                likeordislike(holder.like, holder.unlike, "photos", event.getId(), holder.likedBy);
            }
            holder.caption.setText(event.getEvent_description());
            holder.time.setText(event.getEvent_time());
            holder.date.setText(event.getEvent_date());
            holder.location.setText(event.getEvent_location());
            holder.eventName.setText(event.getEvent_name());
            holder.tags.setText(event.getTags());
    }

    private void setProfile(String userId, CircleImageView imageView) {
        reference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileImgUrl = dataSnapshot.getValue(User.class).getUser_image();
                ImageUtils.loadImageWithOutTransition(mContext, profileImgUrl, imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLike(LikeButton likeButton, String node, String id ) {
        reference.child("Events").child(node).child(id).child("yes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(ds.child("userId").getValue()).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            likeId = ds.getKey();
                            likeButton.setLiked(true);
                        } else {
                            likeButton.setLiked(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDislike(LikeButton likeButton, String node, String id ) {
        reference.child("Events").child(node).child(id).child("no").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(ds.child("userId").getValue()).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            likeId = ds.getKey();
                            likeButton.setLiked(true);
                        } else {
                            likeButton.setLiked(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likeordislike(LikeButton like, LikeButton dislike, String node, String id, TextView likedBy) {
        like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addnewlike(node, id);
                setUserLike(node, id, likedBy);
                if (dislike.isLiked()) {
                    removedislike(node, id);
                    dislike.setLiked(false);
                } else {
                    removedislike(node, id);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                removeLike(node, id);
                setUserLike(node, id, likedBy);
            }
        });

        dislike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                adddislike(node, id);
                if (like.isLiked()){
                    removeLike(node, id);
                    setUserLike(node, id, likedBy);
                    like.setLiked(false);
                } else {
                    removeLike(node, id);
                    setUserLike(node, id, likedBy);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                removedislike(node, id);
            }
        });
    }

    private void addnewlike(String node, String id) {
        Like like = new Like(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName());
        reference.child("Events").child(node).child(id).child("yes").push().setValue(like);
    }

    private void removeLike(String node, String id) {
        reference.child("Events").child(node).child(id).child("yes").child(likeId).removeValue();
    }

    private void adddislike(String node, String id) {
        Like like = new Like(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName());
        reference.child("Events").child(node).child(id).child("no").push().setValue(like);
    }

    private void removedislike(String node, String id) {
        reference.child("Events").child(node).child(id).child("no").child(likeId).removeValue();
    }

    private void setUserLike(String node, String id, TextView likedBy) {
        reference.child("Feeds").child(node).child(id).child("yes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                likedBy.setText(count + " are intrested in this event.");
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

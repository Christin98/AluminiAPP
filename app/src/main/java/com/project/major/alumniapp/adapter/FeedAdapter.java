
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.Feeds;
import com.project.major.alumniapp.models.Like;
import com.project.major.alumniapp.models.Photo;
import com.project.major.alumniapp.models.Video;
import com.project.major.alumniapp.utils.ImageUtils;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private int mResource;
    private Activity mContext;
    private List<Object> list;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Photo photo;
    private Video video;
    private Feeds feeds;
    private String likeId;

    public FeedAdapter(@NonNull Activity context, int resource, @NonNull List<Object> list) {
        mResource = resource;
        mContext = context;
        this.list = list;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView name;
        TextView timestamp;
        TextView caption;
        TextView tags;
        RelativeLayout medialayout;
        ProgressBar progressBar;
        ImageView vid_ind;
        ImageView feedImage;
        LikeButton heartButton;
        ImageView comment;
        TextView viewComments;
        TextView likedBy;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            timestamp = itemView.findViewById(R.id.timestamp);
            caption = itemView.findViewById(R.id.txtStatusMsg);
            tags = itemView.findViewById(R.id.tags);
            medialayout = itemView.findViewById(R.id.media_layout);
            progressBar = itemView.findViewById(R.id.progressBar);
            vid_ind = itemView.findViewById(R.id.video_indicater);
            feedImage = itemView.findViewById(R.id.feedImage1);
            likedBy = itemView.findViewById(R.id.like_number);
            heartButton = itemView.findViewById(R.id.heart_button);
            comment = itemView.findViewById(R.id.comment);
            viewComments = itemView.findViewById(R.id.view_comments);
        }
    }

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, int position) {
        Object object = list.get(position);
        if (object.getClass() == Photo.class){
            photo = (Photo)object;
            setLike(holder.heartButton, "photos", photo.getId());
            like(holder.heartButton, "photos", photo.getId(), holder.likedBy);
            setUserLike("photos", photo.getId(), holder.likedBy);
            ImageUtils.loadImageWithOutTransition(mContext, photo.getProfile_img(), holder.profilePic);
            holder.vid_ind.setVisibility(View.GONE);
            holder.name.setText(photo.getName());
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(photo.getTimestamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.timestamp.setText(timeAgo);
            ImageUtils.loadImageWithTransition(mContext,photo.getImage_url(),holder.feedImage,holder.progressBar);
            holder.caption.setText(photo.getCaption_text());
            holder.tags.setText(photo.getTags());
        } else if (object.getClass() == Video.class){
            video = (Video)object;
            setLike(holder.heartButton, "videos", video.getId());
            like(holder.heartButton, "videos", video.getId(), holder.likedBy);
            setUserLike("videos", video.getId(), holder.likedBy);
            ImageUtils.loadImageWithOutTransition(mContext, video.getProfile_img(), holder.profilePic);
            holder.vid_ind.setVisibility(View.VISIBLE);
            holder.name.setText(video.getName());
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(video.getTimestamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.timestamp.setText(timeAgo);
            ImageUtils.loadImageWithTransition(mContext,video.getVideo_url(),holder.feedImage,holder.progressBar);
            holder.caption.setText(video.getCaption_text());
            holder.tags.setText(video.getTags());
        } else if (object.getClass() == Feeds.class) {
            feeds = (Feeds)object;
            setLike(holder.heartButton, "textfeeds", feeds.getId());
            like(holder.heartButton, "textfeeds", feeds.getId(), holder.likedBy);
            setUserLike("textfeeds", video.getId(), holder.likedBy);
            ImageUtils.loadImageWithOutTransition(mContext, feeds.getProfile_img(), holder.profilePic);
            holder.medialayout.setVisibility(View.GONE);
            holder.name.setText(feeds.getName());
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(feeds.getTimestamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.timestamp.setText(timeAgo);
            holder.caption.setText(feeds.getCaption_text());
            holder.tags.setText(feeds.getTags());
        }
    }

    private void setLike(LikeButton likeButton, String node, String id ) {
        database.getReference("alumni_app").child("Feeds").child(node).child(id).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if(Objects.requireNonNull(ds.child("userId").getValue()).equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
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

    private void like(LikeButton like, String node, String id, TextView likedBy) {
        like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addnewlike(node, id);
                setUserLike(node, id, likedBy);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                removeLike(node, id);
                setUserLike(node, id, likedBy);
            }
        });
    }

    private void addnewlike(String node, String id) {
        Like like = new Like(auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName());
        database.getReference("alumni_app").child("Feeds").child(node).child(id).child("likes").push().setValue(like);
    }

    private void removeLike(String node, String id) {
        database.getReference("alumni_app").child("Feeds").child(node).child(id).child("likes").child(likeId).removeValue();
    }

    private void setUserLike(String node, String id, TextView likedBy) {
        database.getReference("alumni_app").child("Feeds").child(node).child(id).child("likes").orderByChild("userId").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder stringBuilder = new StringBuilder();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Like like = ds.getValue(Like.class);
                    if (like.getUserId().equals(auth.getCurrentUser().getUid())) {
                        stringBuilder.append("You");
                        stringBuilder.append(",");
                    } else {
                        stringBuilder.append(like.getName());
                        stringBuilder.append(",");
                    }
                }

                String[] likes = stringBuilder.toString().split(",");
                String likeString = null;
                int length = likes.length;

                if (length == 0) {
                    likedBy.setText("No Likes For This Post");
                } if (length == 1) {
                    likeString = "Liked by " + likes[0];
                } else if (length == 2) {
                    likeString = "Liked by " + likes[0] + " and " + likes[1];
                } else if (length == 3) {
                    likeString = "Liked by " + likes[0] + ", " + likes[1] + " and " + likes[2];
                } else if (length == 4) {
                    likeString = "Liked by " + likes[0] + ", " + likes[1] + ", " + likes[2] + " and " + likes[3];
                } else if (length > 4) {
                    likeString = "Liked by " + likes[0] + ", " + likes[1] + ", " + likes[2] + " and " + (dataSnapshot.getChildrenCount() - 3)+" others";
                }
                likedBy.setText(likeString);

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

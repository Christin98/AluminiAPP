
package com.project.major.alumniapp.adapter;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.CommentsActivity;
import com.project.major.alumniapp.activities.EditEventsActivity;
import com.project.major.alumniapp.models.Event;
import com.project.major.alumniapp.models.Like;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.utils.ImageUtils;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private int mResource;
    private AppCompatActivity mContext;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private String likeId;
    private List<Object> list;
    private String type;

    public EventAdapter(@NonNull FragmentActivity context, int resource, @NonNull List<Object> list) {
        mResource = resource;
        mContext = (AppCompatActivity) context;
        this.list = list;
        reference = FirebaseDatabase.getInstance().getReference("alumni_app");
        mAuth = FirebaseAuth.getInstance();
        userType();
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
//        LikeButton unlike;
        ImageView comments;
        ImageView options;
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
//            unlike = itemView.findViewById(R.id.thumb_button2);
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
            options = itemView.findViewById(R.id.optionsevent);
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
        holder.options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.options);
            popupMenu.inflate(R.menu.feed_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit:
                        Event event1 = (Event) object;
                        if (event1.getImageurl() == null) {
                            if (event1.getUser_id().equals(mAuth.getCurrentUser().getUid())) {
                                Intent intent = new Intent(mContext, EditEventsActivity.class);
                                intent.putExtra("node", "textevents");
                                intent.putExtra("id", event1.getId());
                                intent.putExtra("eventname", event1.getEvent_name());
                                intent.putExtra("details", event1.getEvent_description());
                                intent.putExtra("location", event1.getEvent_location());
                                intent.putExtra("time", event1.getEvent_time());
                                intent.putExtra("date", event1.getEvent_date());
                                mContext.startActivity(intent);
                                list.remove(position);
                                notifyItemChanged(position);
                                notifyDataSetChanged();
                            } else {
                                TastyToast.makeText(mContext, "You Cannot Edit Other's Event.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                            }
                        } else {
                            if (event1.getUser_id().equals(mAuth.getCurrentUser().getUid())) {
                                Intent intent = new Intent(mContext, EditEventsActivity.class);
                                intent.putExtra("node", "photos");
                                intent.putExtra("id", event1.getId());
                                intent.putExtra("eventname", event1.getEvent_name());
                                intent.putExtra("details", event1.getEvent_description());
                                intent.putExtra("location", event1.getEvent_location());
                                intent.putExtra("time", event1.getEvent_time());
                                intent.putExtra("date", event1.getEvent_date());
                                mContext.startActivity(intent);
                                list.remove(position);
                                notifyItemChanged(position);
                                notifyDataSetChanged();
                            } else {
                                TastyToast.makeText(mContext, "You Cannot Edit Other's Event.", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                            }
                        }
                        break;
                    case R.id.delete:
                        Event event2 = (Event) object;
                        if (event2.getImageurl() == null) {
                            if ((event2.getUser_id().equals(mAuth.getCurrentUser().getUid())) || (type.equals("admin")) || (type.equals("super_admin"))) {
                                FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Events").child("textevents").child(event2.getId()).removeValue();
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                TastyToast.makeText(mContext , "Post Deleted.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                            } else {
                                TastyToast.makeText(mContext, "You Cannot Delete Other's Event.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                            }
                        } else {
                            if ((event2.getUser_id().equals(mAuth.getCurrentUser().getUid())) || (type.equals("admin")) || (type.equals("super_admin"))) {
                                FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Events").child("photos").child(event2.getId()).removeValue();
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                TastyToast.makeText(mContext , "Post Deleted.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                            } else {
                                TastyToast.makeText(mContext , "You Cannot Delete Other's Event.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                            }
                        }
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
        Event event = (Event) object;
//            setProfile(event.getUser_id(), holder.profileImage);
            holder.userName.setText(event.getUser_name());
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(event.getDate_added()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.date_added.setText(timeAgo);
            if (event.getImageurl() == null) {
                holder.media_layout.setVisibility(View.GONE);
                setLike(holder.like, "textevents", event.getId());
//                setDislike(holder.unlike, "textevents", event.getId());
                setUserLike("textevents", event.getId(), holder.likedBy, position);
                likeordislike(holder.like,  "textevents", event.getId(), holder.likedBy, position);
                holder.comments.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("mediaID",event.getId());
                    intent.putExtra("profile_photo",profileImgUrl);
                    intent.putExtra("node","Events");
                    intent.putExtra("node2","textevents");
                    mContext.startActivity(intent);
                });
                holder.view_comments.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("mediaID",event.getId());
                    intent.putExtra("profile_photo",profileImgUrl);
                    intent.putExtra("node","Events");
                    intent.putExtra("node2","textevents");
                    mContext.startActivity(intent);
                });
            } else {
                ImageUtils.loadImageWithTransition(mContext,event.getImageurl(),holder.post,holder.progressBar);
                setLike(holder.like, "photos", event.getId());
//                setDislike(holder.unlike, "photos", event.getId());
                setUserLike("photos", event.getId(), holder.likedBy, position);
                Zoomy.Builder builder = new Zoomy.Builder(mContext).target(holder.post);
                builder.register();
                likeordislike(holder.like,  "photos", event.getId(), holder.likedBy, position);
                holder.comments.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("mediaID",event.getId());
                    intent.putExtra("profile_photo",profileImgUrl);
                    intent.putExtra("node","Events");
                    intent.putExtra("node2","photos");
                    mContext.startActivity(intent);
                });
                holder.view_comments.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("mediaID",event.getId());
                    intent.putExtra("profile_photo",profileImgUrl);
                    intent.putExtra("node","Events");
                    intent.putExtra("node2","photos");
                    mContext.startActivity(intent);
                });
            }
            holder.caption.setText(event.getEvent_description());
            holder.time.setText(event.getEvent_time());
            holder.date.setText(event.getEvent_date());
            holder.location.setText(event.getEvent_location());
            holder.eventName.setText(event.getEvent_name());
            holder.tags.setText(event.getTags());
    }

//    private void setProfile(String userId, CircleImageView imageView) {
//        reference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                profileImgUrl = dataSnapshot.getValue(User.class).getUser_image();
//                ImageUtils.loadImageWithOutTransition(mContext, profileImgUrl, imageView);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void setLike(LikeButton likeButton, String node, String id ) {
        reference.child("Events").child(node).child(id).child("yes").addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void userType() {
        FirebaseDatabase.getInstance().getReference().child("alumni_app").child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                type = user.getUser_type();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void setDislike(LikeButton likeButton, String node, String id ) {
//        reference.child("Events").child(node).child(id).child("no").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        if (Objects.requireNonNull(ds.child("userId").getValue()).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
//                            likeId = ds.getKey();
//                            likeButton.setLiked(true);
//                        } else {
//                            likeButton.setLiked(false);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void likeordislike(LikeButton like, String node, String id, TextView likedBy, int position) {
        like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addnewlike(node, id, likedBy, position);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                removeLike(node, id, likedBy, position);
            }
        });

//        dislike.setOnLikeListener(new OnLikeListener() {
//            @Override
//            public void liked(LikeButton likeButton) {
//                adddislike(node, id, likedBy);
//                if (like.isLiked()){
//                    removeLike(node, id, likedBy);
//                    like.setLiked(false);
//                }
//            }
//
//            @Override
//            public void unLiked(LikeButton likeButton) {
//                removedislike(node, id, likedBy);
//            }
//        });
    }

    private void addnewlike(String node, String id, TextView likedBy, int position) {
        Like like = new Like(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName());
        reference.child("Events").child(node).child(id).child("yes").push().setValue(like);
        setUserLike(node, id, likedBy, position);
        list.remove(position);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    private void removeLike(String node, String id, TextView likedBy, int position) {
        reference.child("Events").child(node).child(id).child("yes").child(likeId).removeValue();
        setUserLike(node, id, likedBy, position);
        list.remove(position);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

//    private void adddislike(String node, String id, TextView likedBy) {
//        Like like = new Like(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName());
//        reference.child("Events").child(node).child(id).child("no").push().setValue(like);
//        setUserLike(node, id, likedBy);
//    }
//
//    private void removedislike(String node, String id, TextView likedBy) {
//        reference.child("Events").child(node).child(id).child("no").child(likeId).removeValue();
//        setUserLike(node, id, likedBy);
//    }

    private void setUserLike(String node, String id, TextView likedBy, int position) {
        Timber.e("USERLIK" + node + id);
        reference.child("Events").child(node).child(id).child("yes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot singlesnapshot: dataSnapshot.getChildren()) {
                    Timber.e("USERLIKES"+singlesnapshot.getChildrenCount());
                    count += 1;
                    notifyItemChanged(position);
                }
                likedBy.setText(count + "is intrested in this event");
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

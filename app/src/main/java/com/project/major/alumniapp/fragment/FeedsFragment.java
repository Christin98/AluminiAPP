package com.project.major.alumniapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.project.major.alumniapp.AApplication;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.AddFeeds;
import com.project.major.alumniapp.activities.CommentsActivity;
import com.project.major.alumniapp.activities.EditFeedsActivity;
import com.project.major.alumniapp.models.Feeds;
import com.project.major.alumniapp.models.Like;
import com.project.major.alumniapp.models.Photo;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.models.Video;
import com.project.major.alumniapp.utils.GlideApp;
import com.project.major.alumniapp.utils.ImageUtils;
import com.project.major.alumniapp.utils.SimpleItemDecorater;
import com.sdsmdg.tastytoast.TastyToast;
import com.touchlane.exovideo.ExoVideoController;
import com.touchlane.exovideo.ExoVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;


public class FeedsFragment extends Fragment implements CacheListener{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button addEvent;
    private RecyclerView recyclerView;
    private FeedAdapter adapter;
    private FirebaseAuth mAuth;
    private ArrayList<Object> mediaList;
    private TextView textView;
    private ProgressBar pb;
    private Context context;
    private ExoVideoController mExoVideoController;

    public FeedsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

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
        mExoVideoController = new ExoVideoController(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecorater(getContext()));
        mediaList = new ArrayList<>();

        getPhoto();
        getVideo();
        getFeeds();

        adapter = new FeedAdapter(mediaList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() -1);

        mediaList.clear();

        addEvent.setOnClickListener(v -> startActivity(new Intent(getContext(), AddFeeds.class)));

    }


    private void getPhoto(){

        Query query = databaseReference.child("Feeds").child("photos").orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Photo photo = ds.getValue(Photo.class);
                    mediaList.add(0,photo);
                    adapter.notifyDataSetChanged();
                }
                Collections.sort(mediaList, (o1, o2) -> {
                    Log.d("TAG","Comparing...");
                    if (o1.getClass() == Photo.class && o2.getClass() == Photo.class) {
                        Photo p1 = (Photo) o1;
                        Photo p2 = (Photo) o2;
                        return p2.getTimestamp().compareTo(p1.getTimestamp());
                    }
                    return 0;
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void getVideo(){

        Query query = databaseReference.child("Feeds").child("videos").orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Video video = ds.getValue(Video.class);
                    mediaList.add(video);
                    adapter.notifyDataSetChanged();
                }
                Collections.sort(mediaList, (o1, o2) -> {
                    Log.d("TAG","Comparing...");
                    if (o1.getClass() == Video.class && o2.getClass() == Video.class) {
                        Video p1 = (Video) o1;
                        Video p2 = (Video) o2;
                        return p2.getTimestamp().compareTo(p1.getTimestamp());
                    }
                    return 0;
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getFeeds(){

        Query query = databaseReference.child("Feeds").child("textfeeds").orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Feeds feeds = ds.getValue(Feeds.class);
                    mediaList.add(feeds);
                    adapter.notifyDataSetChanged();
                }
                Collections.sort(mediaList, (o1, o2) -> {
                    Log.d("TAG","Comparing...");
                    if (o1.getClass() == Feeds.class && o2.getClass() == Feeds.class) {
                        Feeds p1 = (Feeds) o1;
                        Feeds p2 = (Feeds) o2;
                        return p2.getTimestamp().compareTo(p1.getTimestamp());
                    }
                    return 0;
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mExoVideoController.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        adapter.exoPlayer.release();
        mExoVideoController.release();
        AApplication.getProxy(getContext()).unregisterCacheListener(this);
    }



    private static class ViewHolder extends RecyclerView.ViewHolder {
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
        ImageView options;
        TextView viewComments;
        TextView likedBy;
        ExoVideoView playerView;
        AApplication application;

        ViewHolder(@NonNull View itemView, ExoVideoController exoVideoController, ExoVideoView.ThumbnailProvider thumbnailProvider) {
            super(itemView);
            application = (AApplication) itemView.getContext().getApplicationContext();
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
            playerView = itemView.findViewById(R.id.video_post);
            options = itemView.findViewById(R.id.optionsfeed);

            playerView.setExoVideoController(exoVideoController);
            playerView.setThumbnailProvider(thumbnailProvider);

            playerView.setOnClickListener(v -> {
                if (playerView.isPlaying()) {
                    setPlayButtonVisible(true);
                    playerView.pause();
                } else {
                    setPlayButtonVisible(false);
                    playerView.play();
                }
            });

            playerView.setVideoEndListener(new ExoVideoView.VideoEndListener() {
                @Override
                public void onVideoEnded() {
                    setPlayButtonVisible(true);
                }

                @Override
                public void onPlayerDisconnected() {
                    setPlayButtonVisible(true);
                }
            });

        }

        void setPlayButtonVisible(boolean visible) {
            vid_ind.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }

        void setVideoSource(Uri uri) {
            playerView.setSource(uri);
        }

    }

    private class FeedAdapter extends RecyclerView.Adapter<ViewHolder>  {


        private String likeId;
        private List<Object> list;
        private String type;


        FeedAdapter(List<Object> list) {
            this.list = list;
            userType();
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
            return new ViewHolder(view, mExoVideoController, mThumbnailProvider);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Object object = list.get(position);
            holder.heartButton.setVisibility(View.VISIBLE);
            holder.likedBy.setText("Loading...");
            holder.options.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(getContext(), holder.options);
                popupMenu.inflate(R.menu.feed_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            if (object.getClass() == Photo.class) {
                                Photo photo1 = (Photo) object;
                                if (photo1.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                                    Intent intent = new Intent(getActivity(), EditFeedsActivity.class);
                                    intent.putExtra("node", "photos");
                                    intent.putExtra("id", photo1.getId());
                                    intent.putExtra("caption", photo1.getCaption_text());
                                    startActivity(intent);
                                    list.remove(position);
                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                } else {
                                    TastyToast.makeText(getContext(), "You Cannot Edit Other's Post.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                }
                            } else if (object.getClass() == Video.class){
                                Video video1 = (Video) object;
                                if (video1.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                                    Intent intent = new Intent(getActivity(), EditFeedsActivity.class);
                                    intent.putExtra("node", "photos");
                                    intent.putExtra("id", video1.getId());
                                    intent.putExtra("caption", video1.getCaption_text());
                                    startActivity(intent);
                                    list.remove(position);
                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                } else {
                                    TastyToast.makeText(getContext(), "You Cannot Edit Other's Post.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                }
                            } else if (object.getClass() == Feeds.class) {
                                Feeds feeds1 = (Feeds) object;
                                if (feeds1.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                                    Intent intent = new Intent(getActivity(), EditFeedsActivity.class);
                                    intent.putExtra("node", "photos");
                                    intent.putExtra("id", feeds1.getId());
                                    intent.putExtra("caption", feeds1.getCaption_text());
                                    startActivity(intent);
                                    list.remove(position);
                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                } else {
                                    TastyToast.makeText(getContext(), "You Cannot Edit Other's Post.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                }
                            }
                            break;
                        case R.id.delete:
                            if (object.getClass() == Photo.class) {
                                Photo photo2 = (Photo) object;
                                if ((photo2.getUserId().equals(mAuth.getCurrentUser().getUid())) || (type.equals("admin")) || (type.equals("super_admin"))) {
                                    FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Feeds").child("photos").child(photo2.getId()).removeValue();
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                    TastyToast.makeText(getContext(), "Post Deleted.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                } else {
                                    TastyToast.makeText(getContext(), "You Cannot Delete Other's Post.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                }
                            } else if (object.getClass() == Video.class){
                                Video video2 = (Video) object;
                                if ((video2.getUserId().equals(mAuth.getCurrentUser().getUid())) || (type.equals("admin")) || (type.equals("super_admin"))) {
                                    FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Feeds").child("videos").child(video2.getId()).removeValue();
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                    TastyToast.makeText(getContext(), "Post Deleted.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                } else {
                                    TastyToast.makeText(getContext(), "You Cannot Delete Other's Post.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                }
                            } else if (object.getClass() == Feeds.class) {
                                Feeds feeds2 = (Feeds) object;
                                if ((feeds2.getUserId().equals(mAuth.getCurrentUser().getUid())) || (type.equals("admin")) || (type.equals("super_admin"))) {
                                    FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Feeds").child("textfeeds").child(feeds2.getId()).removeValue();
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                    TastyToast.makeText(getContext(), "Post Deleted.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                } else {
                                    TastyToast.makeText(getContext(), "You Cannot Delete Other's Post.",  TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                                }
                            }
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });

            if (object.getClass() == Photo.class){
                Photo photo = (Photo) object;
                holder.feedImage.setVisibility(View.VISIBLE);
                holder.playerView.setVisibility(View.GONE);
                Zoomy.Builder builder = new Zoomy.Builder(getActivity()).target(holder.feedImage)
                        .animateZooming(true)
                        .enableImmersiveMode(true);
                builder.register();
                holder.comment.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("mediaID",photo.getId());
                    intent.putExtra("profile_photo",photo.getProfile_img());
                    intent.putExtra("node","Feeds");
                    intent.putExtra("node2","photos");
                    startActivity(intent);
                });
                holder.viewComments.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("mediaID",photo.getId());
                    intent.putExtra("profile_photo",photo.getProfile_img());
                    intent.putExtra("node","Feeds");
                    intent.putExtra("node2","photos");
                    startActivity(intent);
                });
                setLike(holder.heartButton,"photos", photo.getId());
                like(holder.heartButton, "photos", photo.getId(), holder.likedBy, position);
                setUserLike(holder.heartButton, "photos", photo.getId(), holder.likedBy, position);
                ImageUtils.loadImageWithOutTransition(getContext(), photo.getProfile_img(), holder.profilePic);
                holder.vid_ind.setVisibility(View.GONE);
                holder.name.setText(photo.getName());
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(photo.getTimestamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                holder.timestamp.setText(timeAgo);
                ImageUtils.loadImageWithTransition(getContext(), photo.getImage_url(),holder.feedImage,holder.progressBar);
                holder.caption.setText(photo.getCaption_text());
                holder.tags.setText(photo.getTags());
            } else if (object.getClass() == Video.class){
                Video video = (Video) object;
                Timber.e("video", video);
                holder.feedImage.setVisibility(View.GONE);
                holder.playerView.setVisibility(View.VISIBLE);
                setLike(holder.heartButton, "videos", video.getId());
                like(holder.heartButton, "videos", video.getId(), holder.likedBy, position);
                setUserLike(holder.heartButton, "videos", video.getId(), holder.likedBy, position);
                holder.comment.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("mediaID", video.getId());
                    intent.putExtra("profile_photo",video.getProfile_img());
                    intent.putExtra("node","Feeds");
                    intent.putExtra("node2","videos");
                    startActivity(intent);
                });
                holder.viewComments.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("mediaID", video.getId());
                    intent.putExtra("profile_photo",video.getProfile_img());
                    intent.putExtra("node","Feeds");
                    intent.putExtra("node2","videos");
                    startActivity(intent);
                });
                ImageUtils.loadImageWithOutTransition(getContext(), video.getProfile_img(), holder.profilePic);
                holder.vid_ind.setVisibility(View.VISIBLE);
                HttpProxyCacheServer proxy = AApplication.getProxy(getContext());
                proxy.registerCacheListener(FeedsFragment.this, video.getVideo_url());
                String proxyUrl = proxy.getProxyUrl(video.getVideo_url());
                holder.setVideoSource(Uri.parse(proxyUrl));
                Timber.e("Video Cache Use proxy url " + proxyUrl + " instead of original url " + video.getVideo_url());
                holder.name.setText(video.getName());
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(video.getTimestamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                holder.timestamp.setText(timeAgo);
                holder.caption.setText(video.getCaption_text());
                holder.tags.setText(video.getTags());
            } else if (object.getClass() == Feeds.class) {
                Feeds feeds = (Feeds) object;
                setLike(holder.heartButton, "textfeeds", feeds.getId());
                like(holder.heartButton, "textfeeds", feeds.getId(), holder.likedBy, position);
                setUserLike(holder.heartButton, "textfeeds", feeds.getId(), holder.likedBy, position);
                holder.comment.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("mediaID",feeds.getId());
                    intent.putExtra("profile_photo",feeds.getProfile_img());
                    intent.putExtra("node","Feeds");
                    intent.putExtra("node2","textfeeds");
                    startActivity(intent);
                });
                holder.viewComments.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("mediaID",feeds.getId());
                    intent.putExtra("profile_photo",feeds.getProfile_img());
                    intent.putExtra("node","Feeds");
                    intent.putExtra("node2","textfeeds");
                    startActivity(intent);
                });
                ImageUtils.loadImageWithOutTransition(getContext(), feeds.getProfile_img(), holder.profilePic);
                holder.medialayout.setVisibility(View.GONE);
                holder.name.setText(feeds.getName());
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(feeds.getTimestamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                holder.timestamp.setText(timeAgo);
                holder.caption.setText(feeds.getCaption_text());
                holder.tags.setText(feeds.getTags());
            }
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


        private void setLike(LikeButton likeButton, String node, String id ) {
            firebaseDatabase.getReference("alumni_app").child("Feeds").child(node).child(id).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            if(Objects.requireNonNull(ds.child("userId").getValue()).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())){
                                likeId = ds.getKey();
                                likeButton.setVisibility(View.VISIBLE);
                                likeButton.setLiked(true);
                            } else {
                                likeButton.setVisibility(View.VISIBLE);
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

        private void like(LikeButton like, String node, String id, TextView likedBy, int position) {
            like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    addnewlike(like, node, id, likedBy, position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Timber.e("LikeID "+ likeId);
                    removeLike(like, node, id, likedBy, position);
                    if (like.isLiked()) {
                        like.setLiked(false);
                    }
                }
            });
        }

        private void addnewlike(LikeButton likeButton,String node, String id, TextView likedBy, int position) {
            Like like = new Like(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName());
            setUserLike(likeButton,node, id, likedBy, position);
            firebaseDatabase.getReference("alumni_app").child("Feeds").child(node).child(id).child("likes").push().setValue(like);
            list.remove(position);
            notifyItemChanged(position);
            notifyDataSetChanged();
        }


        private void removeLike(LikeButton likeButton,String node, String id, TextView likedBy, int position) {
            Query query = FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Feeds").child(node).child(id).child("likes").orderByChild("name").equalTo(mAuth.getCurrentUser().getDisplayName());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        setUserLike(likeButton, node, id, likedBy, position);
                        list.remove(position);
                        notifyItemChanged(position);
                        notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void setUserLike(LikeButton likeButton, String node, String id, TextView likedBy, int position) {
            FirebaseDatabase.getInstance().getReference("alumni_app").child("Feeds").child(node).child(id).child("likes").orderByChild("userId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        Like like = ds.getValue(Like.class);
                        if (like.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                            stringBuilder.append("You");
                            stringBuilder.append(",");
                            likeButton.setLiked(true);
                            likeButton.setVisibility(View.VISIBLE);
////                            list.remove(position);
//                            notifyItemChanged(position);
////                            notifyDataSetChanged();

                        } else {
                            likeButton.setVisibility(View.VISIBLE);
                            stringBuilder.append(like.getName());
                            stringBuilder.append(",");
////                            list.remove(position);
//                            notifyItemChanged(position);
////                            notifyDataSetChanged();
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
//                    list.remove(position);
//                    notifyItemChanged(position);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
//            list.remove(position);
//            notifyItemChanged(position);
//            notifyDataSetChanged();
        }


        private ExoVideoView.ThumbnailProvider mThumbnailProvider =
                (imageView, uri) -> {
                    // see MyApplication for Picasso configuration
//                    Picasso.get().load(uri).into(imageView);

                    GlideApp.with(getContext()).load(uri).apply(new RequestOptions().frame(10000)).into(imageView);
        };

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}

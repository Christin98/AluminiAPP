package com.project.major.alumniapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.project.major.alumniapp.activities.AddFeeds;
import com.project.major.alumniapp.models.Feeds;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedsFragment extends Fragment {

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


    public FeedsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        mAuth = FirebaseAuth.getInstance();
        addEvent = view.findViewById(R.id.addFeed);
        recyclerView = view.findViewById(R.id.feed_recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("Feeds");
        databaseReference.keepSynced(true);

        addEvent.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddFeeds.class));
        });

        return view;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("alumni_app").child("Feeds");

        FirebaseRecyclerOptions<Feeds> options = new FirebaseRecyclerOptions.Builder<Feeds>().setQuery(query, Feeds.class).build();

        adapter = new FirebaseRecyclerAdapter<Feeds, FeedsFragment.ViewHolder>(options) {

            @NonNull
            @Override
            public FeedsFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_item, parent, false);

                return new FeedsFragment.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FeedsFragment.ViewHolder holder, int position, @NonNull Feeds feeds) {
                String post_key = getRef(position).getKey();
                holder.name.setText(feeds.getName());

                Log.d("FEEDS ", feeds.getCaption_text());

                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(feeds.getTimestamp()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                holder.timestamp.setText(timeAgo);

                if (!TextUtils.isEmpty(feeds.getCaption_text())){
                    holder.caption.setText(feeds.getCaption_text());
                    holder.caption.setVisibility(View.VISIBLE);
                } else {
                    holder.caption.setVisibility(View.GONE);
                }

                if (feeds.getText_url() != null){
                    holder.textUrl.setText(Html.fromHtml("<a href=\"" + feeds.getText_url() + "\">" + feeds.getText_url() + "</a>"));
                    holder.textUrl.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.textUrl.setVisibility(View.VISIBLE);
                } else {
                    holder.textUrl.setVisibility(View.GONE);
                }

                if (feeds.getFeed_image_url() != null) {
                    Log.d("FEEDS ", feeds.getFeed_image_url());
//                    Picasso.get()
//                            .load(feeds.getFeed_image_url())
//                            .networkPolicy(NetworkPolicy.OFFLINE)
//                            .resize(300, 300)
//                            .placeholder(R.drawable.postimg)
//                            .error(R.drawable.error_img)
//                            .into(holder.feedImage);
                    Glide.with(getContext())
                    .load(feeds.getFeed_image_url())
                    .apply(new RequestOptions().override(300, 300).centerCrop()
                    .placeholder(R.drawable.postimg).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .error(R.drawable.error_img))
                    .into(holder.feedImage);
                    holder.feedImage.setVisibility(View.VISIBLE);
                } else {
                    holder.feedImage.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView name;
        TextView timestamp;
        TextView caption;
        TextView textUrl;
        ImageView feedImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            timestamp = itemView.findViewById(R.id.timestamp);
            caption = itemView.findViewById(R.id.txtStatusMsg);
            textUrl = itemView.findViewById(R.id.txtUrl);
            feedImage = itemView.findViewById(R.id.feedImage1);
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

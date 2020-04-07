package com.project.major.alumniapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.NotificationModel;
import com.project.major.alumniapp.utils.FcmNotification;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.sdsmdg.tastytoast.TastyToast;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotificationFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
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
    LoadingDialog loadingDialog;
    TextView textView;
    FirebaseUser firebaseUser;
//    private Photo photo;
//    private Video video;

    public NotificationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.notification_recyclerView);
        textView = view.findViewById(R.id.textView4);
        loadingDialog = new LoadingDialog(getActivity());
        firebaseUser = mAuth.getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        loadingDialog.showLoading();
        fetch();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("notification");
        databaseReference.keepSynced(true);
        return view;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(firebaseUser.getUid()).orderByChild("date");

        FirebaseRecyclerOptions<NotificationModel> options = new FirebaseRecyclerOptions.Builder<NotificationModel>().setQuery(query, NotificationModel.class).build();

        adapter = new FirebaseRecyclerAdapter<NotificationModel, NotificationFragment.ViewHolder>(options) {
            @NonNull
            @Override
            public NotificationFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
                return new NotificationFragment.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NotificationFragment.ViewHolder holder, int position, @NonNull NotificationModel notificationModel) {
                loadingDialog.hideLoading();
                textView.setVisibility(View.GONE);
                String type = notificationModel.getType();
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(notificationModel.getDate()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                holder.type.setText(notificationModel.getType());
                holder.time.setText(timeAgo);
                switch (type){
                    case "post":
                        holder.text.setText(String.format("%s added a post.", notificationModel.getName()));
                        break;
                    case "comment":
                        holder.text.setText(String.format("%s commented on your post.", notificationModel.getName()));
                        break;
                    case "like":
                        holder.text.setText(String.format("%s liked your post.", notificationModel.getName()));
                        break;
                    case "request":
                        if (!notificationModel.getName().equals("You")) {
                            holder.text.setText(String.format("%s requested to be admin of your city.", notificationModel.getName()));
                            holder.layout.setVisibility(View.VISIBLE);
                            holder.accp.setOnClickListener(v ->{
                                new FcmNotification(getContext()).sendadmin(notificationModel.getId(), firebaseUser, "accepted");
                                FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(notificationModel.getId()).child("user_type").setValue("admin");
                                TastyToast.makeText(getContext(), "Request Accepted", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                                holder.layout.setVisibility(View.GONE);
                            });
                            holder.accp.setOnClickListener(v ->{
                                new FcmNotification(getContext()).sendadmin(notificationModel.getId(), firebaseUser, "rejected");
                                TastyToast.makeText(getContext(), "Request Canceled", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                holder.layout.setVisibility(View.GONE);
                            });
                        } else {
                            FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(notificationModel.getId()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String name = dataSnapshot.child("user_name").getValue(String.class);
                                    holder.text.setText(String.format("%s requested %s to be admin.", notificationModel.getName(), name));
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        break;
                    case "accepted":
                        holder.text.setText(String.format("%s accepted to be admin.", notificationModel.getName()));
                        break;
                    case "rejected":
                        holder.text.setText(String.format("%s rejected to be admin.", notificationModel.getName()));
                        break;
                }

            }
        };
        if(adapter.getItemCount() != 0){
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            loadingDialog.hideLoading();
        }
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        recyclerView.setAdapter(adapter);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        TextView time;
        TextView text;
        TextView accp;
        TextView canc;
        LinearLayout layout;
        CircleImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            time = itemView.findViewById(R.id.time);
            text = itemView.findViewById(R.id.text);
            accp = itemView.findViewById(R.id.accp);
            canc = itemView.findViewById(R.id.can);
            layout = itemView.findViewById(R.id.acc_layout);
            imageView = itemView.findViewById(R.id.icon_not);
        }
    }
}

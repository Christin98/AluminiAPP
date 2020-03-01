/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 29                                                                         *
 ******************************************************************************/

package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.adapter.CommentListAdapter;
import com.project.major.alumniapp.models.Comment;
import com.project.major.alumniapp.models.UtilityInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity implements UtilityInterface {

    Context mContext = CommentsActivity.this;
    private final String TAG = "CommentsActivity";
    private String mediaId;
    private String mediaNode;
    private String profileImage;
    private ListView commentList;
    private ArrayList<Comment> list;
    private CommentListAdapter listAdapter;
    private boolean isCommentAdded = false;
    private long startLimit =-1;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Intent mediaIntent = getIntent();
        mediaId = mediaIntent.getStringExtra("mediaID");
        profileImage = mediaIntent.getStringExtra("profile_photo");

//        setCommentProfileImage(profileImage);
        addComment(mediaNode,mediaId);

        commentList = findViewById(R.id.comment_list);
        list = new ArrayList<>();
        listAdapter = new CommentListAdapter(CommentsActivity.this,R.layout.layout_comment,list);
        retrieveAllComments(mediaId,20);
        commentList.setAdapter(listAdapter);

        goBack();
    }

    private void retrieveAllComments( String mediaId, final long endLimit){

        Query query = myRef.child("alumni_app").child("Events").child(mediaId).child("comment").orderByChild("date_added");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long  commentsLength = 0;
                Log.d("startLimit",""+ startLimit);
                Log.d("endLimit",""+endLimit);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(isCommentAdded&&commentsLength==dataSnapshot.getChildrenCount()-1){

                        Comment comment = snapshot.getValue(Comment.class);
                        list.add(0,new Comment(Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getComment_likes()));
                        commentList.smoothScrollToPosition(0);
                        isCommentAdded = false;
                    }
                    else if (!isCommentAdded&&commentsLength <= endLimit && commentsLength > startLimit) {
                        Comment comment = snapshot.getValue(Comment.class);
                        list.add(new Comment(Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getComment_likes()));
                    }
                    commentsLength++;
                }
                Log.d(TAG,"No of Comments : "+commentsLength);
                Log.d(TAG,"No of Comments_methodBased : "+dataSnapshot.getChildren());
                startLimit = endLimit;
                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addComment(final String mediaNode, final String mediaId){

        TextView postComment = findViewById(R.id.post_comment);
        final EditText commentText = findViewById(R.id.comment);
        postComment.setOnClickListener(v -> {
            if(commentText.getText().toString().length()>0) {
                isCommentAdded = true;
                addNewComment(mediaNode,mediaId, commentText.getText().toString());
            }
        });
    }

//    private void setCommentProfileImage(String image){
//
//        CircularImageView profileImage = findViewById(R.id.comment_profile_image);
//        GlideImageLoader.loadImageWithOutTransition(mContext,image,profileImage);
//    }

    public void addNewComment(final String node, final String mediaId, final String comment){

        final String commentId = myRef.push().getKey();
        final String dateAdded = new SimpleDateFormat("dd-MM-yyyy ", Locale.getDefault()).format(Calendar.getInstance().getTime());
        Query query = myRef.child("alumni_app").child("users").child(mAuth.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userName = Objects.requireNonNull(dataSnapshot.child("user_name").getValue()).toString();
                String profileImage = Objects.requireNonNull(dataSnapshot.child("user_image").getValue()).toString();

                Comment comment_model = new Comment(comment, dateAdded, userName, 0);
                myRef.child("alumni_app").child("Events").child(mediaId).child("comment")
                        .child(Objects.requireNonNull(commentId)).setValue(comment_model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void goBack(){
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
    }


    @Override
    public void loadMore(long limit) {

    }
}

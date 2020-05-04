package com.project.major.alumniapp.adapter;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.Comment;
import com.project.major.alumniapp.models.UtilityInterface;
import com.project.major.alumniapp.utils.GlideApp;
import com.project.major.alumniapp.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Objects;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private AppCompatActivity mContext;
    private int layoutResource;
    private long limit=20;
    private UtilityInterface utilityInterface;

    public CommentListAdapter(@NonNull AppCompatActivity context, int resource, ArrayList<Comment> comments) {
        super(context,resource,comments);
        mContext = context;
        layoutResource = resource;
        utilityInterface = (UtilityInterface)mContext;
    }

    private static class ViewHolder{

        ImageView profileImage;
        TextView comment;
        //TextView commentLike;
        TextView dateAdded;
        ImageView addLike;
        TextView commentReply;
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.profileImage = convertView.findViewById(R.id.comment_profile);
            holder.comment = convertView.findViewById(R.id.comment);
            //holder.commentLike = convertView.findViewById(R.id.commentLike);
            holder.dateAdded = convertView.findViewById(R.id.date_added);
            holder.addLike = convertView.findViewById(R.id.comment_heart);
            holder.commentReply = convertView.findViewById(R.id.commentReply);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        Comment commentData = getItem(position);


        //Setting profile image
//        GlideImageLoader.loadImageWithOutTransition(mContext, Objects.requireNonNull(commentData).getProfile_image(),holder.profileImage);
        //Setting userName and comment
        String userName = Objects.requireNonNull(commentData).getUser_name();
        SpannableStringBuilder str = new SpannableStringBuilder(userName+" "+commentData.getComment());
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.comment.setText(str);
        //Setting date
        ImageUtils.loadImageCenterCrop(GlideApp.with(getContext()), commentData.getProfile_image(), holder.profileImage, 50, 50);
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.parseLong(commentData.getDate_added()), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        holder.dateAdded.setText(timeAgo);

        holder.addLike.setOnClickListener(v -> Toast.makeText(mContext, "This feature is not added yet!!", Toast.LENGTH_SHORT).show());

        holder.commentReply.setOnClickListener(v -> Toast.makeText(mContext, "This feature is not added yet!!", Toast.LENGTH_SHORT).show());


        if(position >= limit-1) {
            limit+=20;
            utilityInterface.loadMore(limit);
        }

        return  convertView;
    }

}

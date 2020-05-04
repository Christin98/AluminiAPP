
package com.project.major.alumniapp.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.EditJobActivity;
import com.project.major.alumniapp.activities.JobActivity;
import com.project.major.alumniapp.models.Jobs;
import com.project.major.alumniapp.models.User;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder> {


    private int mResource;
    private AppCompatActivity mContext;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private ArrayList<Jobs> list;
    private String type;

    public JobsAdapter(@NonNull AppCompatActivity context, int resource, @NonNull ArrayList<Jobs> list) {
        mResource = resource;
        mContext = context;
        this.list = list;
        reference = FirebaseDatabase.getInstance().getReference("alumni_app");
        mAuth = FirebaseAuth.getInstance();
        userType();
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView companyImage;
        TextView companyName;
        TextView jobProfile;
        TextView lastDate;
        TextView experience;
        TextView location;
        LinearLayout layout;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyImage = itemView.findViewById(R.id.companyImage);
            companyName = itemView.findViewById(R.id.companyName);
            jobProfile = itemView.findViewById(R.id.job_profile);
            lastDate = itemView.findViewById(R.id.last_date);
            experience = itemView.findViewById(R.id.exprience);
            location = itemView.findViewById(R.id.location);
            layout = itemView.findViewById(R.id.joblayout);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnLongClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View promptView = layoutInflater.inflate(R.layout.job_menu, null);
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(promptView);
            if(dialog.getWindow()!=null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.setCancelable(true);
            dialog.show();
            Button edit = promptView.findViewById(R.id.editjob);
            Button delete = promptView.findViewById(R.id.deletejob);

            edit.setOnClickListener(v1 -> {
                if (list.get(position).getUserId().equals(mAuth.getCurrentUser().getUid())) {
                    Intent intent = new Intent(mContext, EditJobActivity.class);
                    intent.putExtra("id", list.get(position).getId());
                    intent.putExtra("cn", list.get(position).getCompanyName());
                    intent.putExtra("jp", list.get(position).getJobProfile());
                    intent.putExtra("jd", list.get(position).getJobDescription());
                    intent.putExtra("ld", list.get(position).getLastDate());
                    intent.putExtra("jl", list.get(position).getLocation());
                    intent.putExtra("ex", list.get(position).getExperience());
                    intent.putExtra("apply", list.get(position).getApplyLink());
                    list.remove(position);
                    notifyItemChanged(position);
                    notifyDataSetChanged();
                    mContext.startActivity(intent);
                    dialog.dismiss();
                } else {
                    TastyToast.makeText(mContext, "You Don't have privilege to edit this Job", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    dialog.dismiss();
                }
            });

            delete.setOnClickListener(v1 -> {
                if ((list.get(position).getUserId().equals(mAuth.getCurrentUser().getUid())) || (type.equals("admin")) || (type.equals("super_admin")) ) {
                    FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Jobs").child(list.get(position).getId()).removeValue();
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    TastyToast.makeText(mContext, "Job Deleted", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    dialog.dismiss();
                } else {
                    TastyToast.makeText(mContext, "You Don't have privilege to delete this Job", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                    dialog.dismiss();
                }
            });
            Toast.makeText(mContext, "Long clicked", Toast.LENGTH_SHORT).show();
            return false;
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, JobActivity.class);
            intent.putExtra("id", list.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });
        if (list.get(position).getCompanyImage() != null) {
            Glide.with(mContext)
                    .load(list.get(position).getCompanyImage())
                    .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.postimg).diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(R.drawable.error_img))
                    .into(holder.companyImage);
            holder.companyImage.setVisibility(View.VISIBLE);
        } else {
            holder.companyImage.setVisibility(View.GONE);
        }
        holder.companyName.setText(list.get(position).getCompanyName());
        holder.jobProfile.setText(list.get(position).getJobProfile());
        holder.lastDate.setText(list.get(position).getLastDate());
        holder.experience.setText(list.get(position).getExperience());
        holder.location.setText(list.get(position).getLocation());

//        holder.layout.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, JobActivity.class);
//            intent.putExtra("id", list.get(position).getId());
//            holder.itemView.getContext().startActivity(intent);
//        });
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

    @Override
    public int getItemCount() {
        return list.size();
    }
}

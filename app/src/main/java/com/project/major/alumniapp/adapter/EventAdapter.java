package com.project.major.alumniapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.Event;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.myHolder> {

    private List<Event> eventList;


    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new  myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder myHolder, int position) {
        Event data = eventList.get(position);
        myHolder.title.setText(data.geturl());
        myHolder.desc.setText(data.getDesc());
        myHolder.name.setText(data.getEmail());
        if (data.geturl() != null) {
//            Glide.with(context)
//                    .load(data.geturl())
//                    .apply(new RequestOptions()
//                    .placeholder(R.drawable.postimg))
//                    .into(myHolder.postimg);
            Picasso.get()
                    .load(data.geturl())
                    .resize(300, 300)
                    .placeholder(R.drawable.postimg)
                    .error(R.drawable.postimg)
                    .into(myHolder.postimg);
        } else {
            myHolder.postimg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class myHolder extends RecyclerView.ViewHolder

    {
        TextView title,desc,name;
        ImageView postimg;
        CircleImageView circleImageView;

        myHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            name = itemView.findViewById(R.id.name);
            postimg = itemView.findViewById(R.id.postimg);
            circleImageView = itemView.findViewById(R.id.profile);
        }
    }
}

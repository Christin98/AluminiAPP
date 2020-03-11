package com.project.major.alumniapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.activities.MainActivity;
import com.project.major.alumniapp.models.User;

public class ConnectFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private boolean mLikedByCurrentUser = false;
    private String likeId;
    private String mLikesString;
    private StringBuilder mStringBuilder;
    Context context;

    public ConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.user_recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("users");
        databaseReference.keepSynced(true);
        return view;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference("alumni_app").child("users").orderByChild("").equalTo("");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        adapter = new FirebaseRecyclerAdapter<User, ConnectFragment.ViewHolder>(options) {

            @NonNull
            @Override
            public ConnectFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_item, parent, false);

                return new ConnectFragment.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ConnectFragment.ViewHolder holder, int position, @NonNull User user) {
                String post_key = getRef(position).getKey();

//                Log.d("USER CLASS", user.getemail());
                holder.cardView.setVisibility(View.VISIBLE);
                holder.userlogo.setImageResource(R.drawable.userpic);
                holder.username.setText(user.getUser_name());
                holder.email.setText(user.getEmail());

            }
        };
        recyclerView.setAdapter(adapter);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView userlogo;
        TextView username;
        TextView email;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.userCardView);
            userlogo = itemView.findViewById(R.id.profilePicuser);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((MainActivity) context).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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


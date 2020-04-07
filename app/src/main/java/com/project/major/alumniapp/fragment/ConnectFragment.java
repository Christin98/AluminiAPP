package com.project.major.alumniapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.adapter.UserListAdapter;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.utils.LoadingDialog;

import java.util.ArrayList;

public class ConnectFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private UserListAdapter listAdapter;
    private ArrayList<User> searchList;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private boolean mLikedByCurrentUser = false;
    private String likeId;
    private String mLikesString;
    private StringBuilder mStringBuilder;
    private String[] filt = {"city"};
    private String quer = "jaipur";
    private LoadingDialog loadingDialog;
    TextView textView;

    FloatingActionButton filter_fab;

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
        filter_fab = view.findViewById(R.id.filter_fab);
        textView = view.findViewById(R.id.textView4);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        searchList = new ArrayList<>();
        listAdapter = new UserListAdapter(getContext(), searchList);
        loadingDialog.showLoading();
        recyclerView.setAdapter(listAdapter);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("users");
        databaseReference.keepSynced(true);

        fetch(quer);

        filter_fab.setOnClickListener(v -> showbottomsheet());

        return view;
    }

    private void showbottomsheet() {

        CheckBox prof;
        CheckBox name;
        CheckBox org;
        CheckBox place;
        Button apply;
        StringBuilder stringBuilder;

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(view);
        prof = view.findViewById(R.id.profession_fil);
        name = view.findViewById(R.id.name_fil);
        org = view.findViewById(R.id.organization_fil);
        place = view.findViewById(R.id.place_fil);
        apply = view.findViewById(R.id.apply_fil);
        stringBuilder = new StringBuilder();

        prof.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                stringBuilder.append("profession");
                stringBuilder.append(",");
            }
        });

        name.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                stringBuilder.append("search_name");
                stringBuilder.append(",");
            }
        });

        org.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                stringBuilder.append("organization");
                stringBuilder.append(",");
            }
        });

        place.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                stringBuilder.append("city");
                stringBuilder.append(",");
            }
        });

        apply.setOnClickListener(v ->{
            filt = stringBuilder.toString().split(",");
            dialog.dismiss();
        });

        dialog.show();

        for (String s : filt) {
            Log.d("TAG", s);
        }
    }

    private void fetch(String keyword) {
        textView.setVisibility(View.GONE);
        if (keyword.length() > 0) {
            for (String s : filt) {
                Query query = FirebaseDatabase.getInstance().getReference("alumni_app").child("users").orderByChild(s).startAt(keyword.toUpperCase()).endAt(keyword.toLowerCase() + "\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            Log.d("TAG", "onDataChange: "+dataSnapshot.getValue());
                            searchList.add(singleSnapshot.getValue(User.class));
                            listAdapter.notifyDataSetChanged();
                            loadingDialog.hideLoading();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        searchList.clear();
//                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) item.getActionView();
//        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList.clear();
                listAdapter.notifyDataSetChanged();
                quer = query.trim().toLowerCase();
                loadingDialog.showLoading();
                fetch(quer);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList.clear();
                listAdapter.notifyDataSetChanged();
                quer = newText.trim().toLowerCase();
                fetch(quer);
                return false;
            }
        });
    }


}


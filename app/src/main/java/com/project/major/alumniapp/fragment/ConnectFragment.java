package com.project.major.alumniapp.fragment;

import android.content.Context;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Locale;

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
    private boolean isCity = false;
    private boolean isProfession = false;
    private boolean isOrganization = false;
    private boolean isName = false;
    private String quer = null;
//    private LoadingDialog loadingDialog;
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
        textView = view.findViewById(R.id.textView5);
        linearLayoutManager = new LinearLayoutManager(getActivity());
//        loadingDialog = new LoadingDialog((AppCompatActivity) getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        searchList = new ArrayList<>();
        listAdapter = new UserListAdapter(getContext(), searchList);
        recyclerView.setAdapter(listAdapter);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("alumni_app").child("users");
        databaseReference.keepSynced(true);

        filter_fab.setOnClickListener(v -> showbottomsheet());

        return view;
    }

    private void showbottomsheet() {

        CheckBox prof;
        CheckBox name;
        CheckBox org;
        CheckBox place;
        Button apply;

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(view);
        prof = view.findViewById(R.id.profession_fil);
        name = view.findViewById(R.id.name_fil);
        org = view.findViewById(R.id.organization_fil);
        place = view.findViewById(R.id.place_fil);
        apply = view.findViewById(R.id.apply_fil);

        prof.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isProfession = true;
            }
        });

        name.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
               isName = true;
            }
        });

        org.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isOrganization = true;
            }
        });

        place.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isCity = true;
            }
        });

        apply.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    private void fetch(String keyword) {
        textView.setVisibility(View.GONE);
        if (keyword.length() > 0) {
            searchList.clear();
            FirebaseDatabase.getInstance().getReference().child("alumni_app").child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        User user = dataSnapshot1.getValue(User.class);
                        if (isCity && isName && isProfession && isOrganization) {
                            if (user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword) || user.getOrganization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isCity && isName && isOrganization) {
                            if (user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_organization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isCity && isName && isProfession) {
                            if (user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isName && isProfession && isOrganization) {
                            if (user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword) || user.getOrganization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isCity && isProfession && isOrganization) {
                            if (user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword) || user.getOrganization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isName && isCity) {
                            if (user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isName && isProfession) {
                            if (user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isName && isOrganization) {
                            if (user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_organization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isCity && isProfession) {
                            if (user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isCity && isOrganization) {
                            if (user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_organization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isProfession && isOrganization) {
                            if (user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword) || user.getSearch_organization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isCity) {
                            if (user.getSearch_city().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isName) {
                            if (user.getSearch_name().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isProfession) {
                            if (user.getSearch_profession().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        } else if (isOrganization) {
                            if (user.getSearch_organization().toLowerCase(Locale.getDefault()).contains(keyword)) {
                                searchList.add(user);
                            }
                        }
                        else {
                            textView.setText("No Users Found");
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                    //                    else if () {
//
//                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItemCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList.clear();
                listAdapter.notifyDataSetChanged();
                quer = query.trim().toLowerCase();
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


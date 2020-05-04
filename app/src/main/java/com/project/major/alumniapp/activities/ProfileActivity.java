
package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.utils.FcmNotification;
import com.sdsmdg.tastytoast.TastyToast;

public class ProfileActivity extends AppCompatActivity {

    ImageView prPic;
    ImageButton prEdit;
    TextView prName;
    TextView prEmail;
    TextView prPhone;
    TextView prCountry;
    TextView prState;
    TextView prCity;
    TextView prNavReg;
    TextView prNavName;
    TextView prProfession;
    TextView prOrganization;
    LinearLayout adminlayout;
    CheckBox adminbox;
    Button send_btn;
    Button rmbtn;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prPic = findViewById(R.id.imageview_account_profile);
        prEdit = findViewById(R.id.edit_img_btn);
        prName = findViewById(R.id.profile_name);
        prEmail = findViewById(R.id.profile_email);
        prPhone = findViewById(R.id.profile_phone);
        prCountry = findViewById(R.id.profile_country);
        prState = findViewById(R.id.profile_state);
        prCity = findViewById(R.id.profile_city);
        prNavReg = findViewById(R.id.profile_navodya_region);
        prNavName = findViewById(R.id.profile_navodya_name);
        prProfession = findViewById(R.id.profile_profession);
        prOrganization = findViewById(R.id.profile_organization);
        adminlayout = findViewById(R.id.checkbox_admin_layout);
        adminbox = findViewById(R.id.admin_check);
        send_btn = findViewById(R.id.sndreqbtn);
        rmbtn = findViewById(R.id.rmadbtn);

        id = getIntent().getStringExtra("uid");

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("alumni_app").child("users");

        if (!id.equals(firebaseUser.getUid())) {
            prEdit.setVisibility(View.GONE);
        }

        if (id.equals(firebaseUser.getUid())) {
            adminlayout.setVisibility(View.GONE);
        } else {
            FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user_type = dataSnapshot.child("user_type").getValue(String.class);
                    if (user_type.equals("super_admin") || user_type.equals("admin")) {
                        checkadmin();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        rmbtn.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(id).child("user_type").setValue("user");
        });

        send_btn.setOnClickListener(v -> {
            new FcmNotification(ProfileActivity.this).sendadmin(id, firebaseUser, "request");
            TastyToast.makeText(ProfileActivity.this, "Request Send", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
        });


        prEdit.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));

        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(ProfileActivity.this)
                            .load(user.getUser_image())
                            .apply(new RequestOptions().override(150, 150).placeholder(R.drawable.profle_user).error(R.drawable.profle_user))
                            .into(prPic);
                    prName.setText(user.getUser_name());
                    prEmail.setText(user.getEmail());
                    prPhone.setText(user.getPhone());
                    prCountry.setText(user.getCountry());
                    prState.setText(user.getState());
                    prCity.setText(user.getCity());
                    prNavReg.setText(user.getNav_state());
                    prNavName.setText(user.getNavodhya());
                    prProfession.setText(user.getProfession());
                    prOrganization.setText(user.getOrganization());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                TastyToast.makeText(ProfileActivity.this, "DataBase ERROR Please Try Again.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        });

    }

    private void checkadmin() {

        FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_type = dataSnapshot.child("user_type").getValue(String.class);
                assert user_type != null;
                if (user_type.equals("admin")) {
                    rmbtn.setVisibility(View.VISIBLE);
                } else if (user_type.equals("user")) {
                    send_btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

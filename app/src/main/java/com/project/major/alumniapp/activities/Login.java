package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.AlertDialogManager;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.project.major.alumniapp.utils.SessionManager;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    ImageView top_curve;
    EditText email,password;
    TextView login_title;
    TextView logo;
    LinearLayout new_user_layout;
    CardView login_card;
    AlertDialogManager alertDialogManager = new AlertDialogManager();
    SessionManager sessionManager;
    AwesomeValidation validation;
    LoadingDialog loadingDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
//        sessionManager.checkLogin();
        validation = new AwesomeValidation(ValidationStyle.UNDERLABEL);
        validation.setContext(this);
        loadingDialog = new LoadingDialog(this);
        auth = FirebaseAuth.getInstance();

        top_curve = findViewById(R.id.top_curve);
        email = findViewById(R.id.editText_login_email);
        password = findViewById(R.id.editText_login_password);
        logo = findViewById(R.id.logo);
        login_title = findViewById(R.id.login_text);
        new_user_layout = findViewById(R.id.new_user_text);
        login_card = findViewById(R.id.login_card);

        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_down);
        top_curve.startAnimation(top_curve_anim);

        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.edittext_anim);
        email.startAnimation(editText_anim);
        password.startAnimation(editText_anim);

        Animation field_name_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.field_name_anim);
        logo.startAnimation(field_name_anim);
        login_title.startAnimation(field_name_anim);

        Animation center_reveal_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.center_reveal_anim);
        login_card.startAnimation(center_reveal_anim);

        Animation new_user_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.down_top);
        new_user_layout.startAnimation(new_user_anim);

        validation.addValidation(this, R.id.editText_login_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        validation.addValidation(this, R.id.editText_login_password, "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*]).{8,}", R.string.passwerror);
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUp.class));
    }

    public void loginButton(View view) {
        if (validation.validate()) {
            login(email.getText().toString().trim(),password.getText().toString());
            loadingDialog.showLoading();
        } else {
            TastyToast.makeText(this, "Validation Error", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
        }
    }

    private void login(String email , String passw){
        auth.signInWithEmailAndPassword(email,passw).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                loadingDialog.hideLoading();
                FirebaseUser user = auth.getCurrentUser();
                Map<String, String> users= new HashMap<>();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("alumni-app").getRef().child(user.getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       for (DataSnapshot ds:dataSnapshot.getChildren()){
                           users.put("name",ds.child("name").getValue(String.class));
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (user.isEmailVerified()){
                    TastyToast.makeText(this, "Login Succesfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    sessionManager.createLoginSession(users.get("name"),email);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }else {
                    signOut();
                    TastyToast.makeText(this, "Login Error.Please Verify your Email First.", TastyToast.LENGTH_LONG, TastyToast.INFO).show();
                }
            }else {
                loadingDialog.hideLoading();
                TastyToast.makeText(this,"Login Failed. Please check details.",TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
            }
        });
    }
    private void signOut(){
        auth.signOut();
    }
}

package com.project.major.alumniapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.project.major.alumniapp.utils.SessionManager;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {

    ImageView top_curve;
    EditText email,password;
    TextView login_title;
    ImageView logo;
    TextView copyrightTV;
    TextView forgotpassword;
    LinearLayout new_user_layout;
    CardView login_card;
    SessionManager sessionManager;
    AwesomeValidation validation;
    LoadingDialog loadingDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    User userList;
    boolean phoneVer = false;

    DatabaseReference user_DB;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
        validation = new AwesomeValidation(ValidationStyle.UNDERLABEL);
        validation.setContext(this);
        loadingDialog = new LoadingDialog(this);
        forgotpassword = findViewById(R.id.textView_forgetpasswd);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        user_DB = FirebaseDatabase.getInstance().getReference("alumni_app").child("users");

        top_curve = findViewById(R.id.top_curve);
        email = findViewById(R.id.editText_login_email);
        password = findViewById(R.id.editText_login_password);
        copyrightTV = findViewById(R.id.copyrightTV);
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

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        copyrightTV.setText("AlumniAPP © " + year);

        forgotpassword.setOnClickListener(v -> startActivity(new Intent(Login.this, ResetPasswordActivity.class)));

        validation.addValidation(this, R.id.editText_login_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        validation.addValidation(this, R.id.editText_login_password, "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*]).{8,}", R.string.passwerror);
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUp.class));
    }

    public void loginButton(View view) {
        if (validation.validate()) {
            loadingDialog.showLoading();
            login(email.getText().toString().trim(),password.getText().toString());
        } else {
            TastyToast.makeText(this, "Validation Error", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
        }
    }

    private void login(String email , String passw){
        auth.signInWithEmailAndPassword(email,passw).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("Login", "task.success");
                Map<String, String> users = new HashMap<>();
                String user_ID = auth.getCurrentUser().getUid();
                user = auth.getCurrentUser();
                boolean isVerified = false;
                if (user != null) {
                    isVerified = user.isEmailVerified();
                }
                if (isVerified) {
                    loadingDialog.showLoading();
                    sessionManager.createLoginSession(users.get("name"), users.get("email"), user_ID);
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }  else {
                    loadingDialog.hideLoading();
                    TastyToast.makeText(Login.this, "Email is not verified. Please verify first", TastyToast.LENGTH_LONG, TastyToast.INFO).show();
                    signOut();
                }
            } else{
                    TastyToast.makeText(this, "Your email and password may be incorrect. Please check & try again.", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }
                loadingDialog.hideLoading();
            }
        );
    }
    private void signOut(){
        auth.signOut();
    }
}

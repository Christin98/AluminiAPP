package com.project.major.alumniapp.activities;

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
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.AlertDialogManager;
import com.project.major.alumniapp.utils.SessionManager;
import com.sdsmdg.tastytoast.TastyToast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
        validation = new AwesomeValidation(ValidationStyle.UNDERLABEL);
        validation.setContext(this);

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

    public void register(View view) {
        startActivity(new Intent(this, SignUp.class));
    }

    public void loginButton(View view) {
        if (validation.validate()) {
//            Toast.makeText(this, "Validation Succesful", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(this, "Validation Succesful", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            TastyToast.makeText(this, "Validation Error", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
        }
    }
}

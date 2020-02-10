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

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.project.major.alumniapp.R;
import com.sdsmdg.tastytoast.TastyToast;

public class SignUp extends AppCompatActivity {

    ImageView top_curve;
    EditText name, email, password, confPassword;
    TextView login_title;
    TextView logo;
    LinearLayout already_have_account_layout;
    CardView register_card;
    AwesomeValidation validation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        validation = new AwesomeValidation(ValidationStyle.UNDERLABEL);
        validation.setContext(this);

        top_curve = findViewById(R.id.top_curve);
        name = findViewById(R.id.editText_signup_name);
        email = findViewById(R.id.editText_signup_email);
        password = findViewById(R.id.editText_signup_password);
        confPassword = findViewById(R.id.editText_signup_passwordConfirm);
        logo = findViewById(R.id.logo);
        login_title = findViewById(R.id.registration_title);
        already_have_account_layout = findViewById(R.id.already_have_account_text);
        register_card = findViewById(R.id.register_card);

        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_down);
        top_curve.startAnimation(top_curve_anim);

        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.edittext_anim);
        name.startAnimation(editText_anim);
        email.startAnimation(editText_anim);
        password.startAnimation(editText_anim);
        confPassword.startAnimation(editText_anim);

        Animation field_name_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.field_name_anim);
        logo.startAnimation(field_name_anim);
        login_title.startAnimation(field_name_anim);

        Animation center_reveal_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.center_reveal_anim);
        register_card.startAnimation(center_reveal_anim);

        Animation new_user_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.down_top);
        already_have_account_layout.startAnimation(new_user_anim);

        validation.addValidation(this, R.id.editText_signup_name, "(^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}).{2,}", R.string.nameerror);
        validation.addValidation(this, R.id.editText_signup_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        validation.addValidation(this, R.id.editText_signup_password, "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*]).{8,}", R.string.passwerror);
        validation.addValidation(this, R.id.editText_signup_passwordConfirm, R.id.editText_signup_password, R.string.confpasswerror);

    }

    public void login(View view) {
        startActivity(new Intent(this, Login.class));
    }
    public void registerButton(View view) {
        if (validation.validate()) {
            TastyToast.makeText(this,"Register Clicked",TastyToast.LENGTH_SHORT, TastyToast.INFO).show();

        }
    }
}

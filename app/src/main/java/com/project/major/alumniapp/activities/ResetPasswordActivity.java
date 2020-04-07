package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.sdsmdg.tastytoast.TastyToast;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText editText;
    Button sendBtn;
    AwesomeValidation validation;
    LoadingDialog loadingDialog;
    String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        editText = findViewById(R.id.email_edtext);
        sendBtn = findViewById(R.id.reset_button);
        validation = new AwesomeValidation(ValidationStyle.UNDERLABEL);
        validation.setContext(this);
        loadingDialog = new LoadingDialog(this);

        validation.addValidation(this, R.id.email_edtext, Patterns.EMAIL_ADDRESS, R.string.emailerror);

        sendBtn.setOnClickListener(v -> {
            if (validation.validate()) {
                loadingDialog.showLoading();
                email = editText.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    TastyToast.makeText(ResetPasswordActivity.this, "Password Reset Email sent. Please reset password form email", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                    loadingDialog.hideLoading();
                    Intent intent = new Intent(ResetPasswordActivity.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }
        });
    }
}

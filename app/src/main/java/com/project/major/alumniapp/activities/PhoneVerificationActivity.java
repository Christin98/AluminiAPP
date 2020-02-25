package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.SessionManager;
import com.sdsmdg.tastytoast.TastyToast;
import com.shuhart.stepview.StepView;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private int currentStep = 0;
    LinearLayout layout1,layout2,layout3;
    StepView stepView;

    private static String uniqueIdentifier = null;
    private static final String UNIQUE_ID = "UNIQUE_ID";
    private static final long ONE_HOUR_MILLI = 60*60*1000;

    private static final String TAG = "FirebasePhoneNumAuth";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth;

    private String phoneNumber;
    private Button sendCodeButton;
    private Button verifyCodeButton;
    private Button button3;

    private EditText phoneNum;
    private PinView verifyCodeET;
    private TextView phonenumberText;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;

    private SessionManager sessionManager;

    DatabaseReference user_DB;

    String email, passw, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        sessionManager = new SessionManager(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        sendCodeButton = findViewById(R.id.submit1);
        verifyCodeButton = findViewById(R.id.submit2);
        button3 = findViewById(R.id.submit3);
        firebaseAuth = FirebaseAuth.getInstance();
        phoneNum = findViewById(R.id.phonenumber);
        verifyCodeET = findViewById(R.id.pinView);
        phonenumberText = findViewById(R.id.phonenumberText);

        stepView = findViewById(R.id.step_view);
        stepView.setStepsNumber(3);
        stepView.go(0, true);
        layout1.setVisibility(View.VISIBLE);

        email = getIntent().getStringExtra("email");
        passw = getIntent().getStringExtra("password");
        name = getIntent().getStringExtra("name");

        sendCodeButton.setOnClickListener(v -> {
            phoneNumber = phoneNum.getText().toString();
            phonenumberText.setText(phoneNumber);

            if (TextUtils.isEmpty(phoneNumber)) {
                phoneNum.setError("Enter a Phone Number");
                phoneNum.requestFocus();
            } else if (phoneNumber.length() < 10) {
                phoneNum.setError("Please enter a valid phone number");
                phoneNum.requestFocus();
            } else {
                if (currentStep < stepView.getStepCount() - 1){
                    currentStep++;
                    stepView.go(currentStep, true);
                } else {
                    stepView.done(true);
                }
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, PhoneVerificationActivity.this, mCallbacks);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                mAuth.getCurrentUser().linkWithCredential(phoneAuthCredential).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        TastyToast.makeText(PhoneVerificationActivity.this, "SUCCESFULLY LINKED", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    } else {
                        TastyToast.makeText(PhoneVerificationActivity.this, task1.getException().getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                        mAuth.signOut();
                        sessionManager.logoutUser();
                    }
                });
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }
        };

        verifyCodeButton.setOnClickListener(v -> {

            String verificationCode = verifyCodeET.getText().toString();
            if (verificationCode.isEmpty()){
                TastyToast.makeText(PhoneVerificationActivity.this, "Enter verification code", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            } else {

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        if (currentStep < stepView.getStepCount() - 1) {
                            currentStep++;
                            stepView.go(currentStep, true);
                        } else {
                            stepView.done(true);
                        }


                        layout1.setVisibility(View.GONE);
                        layout2.setVisibility(View.GONE);
                        layout3.setVisibility(View.VISIBLE);
                        TastyToast.makeText(PhoneVerificationActivity.this, "SUCCESFULLY LINKED", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    } else {
                        TastyToast.makeText(PhoneVerificationActivity.this, task1.getException().getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                    }
                });
            }
        });

        button3.setOnClickListener(v -> {
            if (currentStep < stepView.getStepCount() - 1) {
                currentStep++;
                stepView.go(currentStep, true);
            } else {
                stepView.done(true);
            }
            String user_ID = mAuth.getCurrentUser().getUid();
            user_DB = FirebaseDatabase.getInstance().getReference("alumni_app").getRef().child("users").child(user_ID);
            user_DB.child(user_ID).child("verified").setValue("true");
            user_DB.child(user_ID).child("phone_verified").setValue("true");
            user_DB.child(user_ID).child("phone").setValue(phoneNum);
            user_DB.child(user_ID).child("verification_ID").setValue(mVerificationId);

            sessionManager.createLoginSession(name, email, user_ID);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(PhoneVerificationActivity.this, MainActivity.class));
                finish();
            },3000);
        });
    }

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        if (currentStep < stepView.getStepCount() - 1) {
//                            currentStep++;
//                            stepView.go(currentStep, true);
//                        } else {
//                            stepView.done(true);
//                        }
//
//
//                        layout1.setVisibility(View.GONE);
//                        layout2.setVisibility(View.GONE);
//                        layout3.setVisibility(View.VISIBLE);
//                        // ...
//                    } else {
//                        TastyToast.makeText(PhoneVerificationActivity.this,"Something wrong",TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
//                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//
//                        }
//                    }
//                });
//    }
}

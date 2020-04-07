package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.SessionManager;
import com.sdsmdg.tastytoast.TastyToast;
//import com.shuhart.stepview.StepView;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private int currentStep = 0;
    LinearLayout layout1,layout2,layout3;
//    StepView stepView;

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
    private TextView resend;
    private TextView timer, tap;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private SessionManager sessionManager;
int counter;
    DatabaseReference user_DB;

    String email, passw, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        sessionManager = new SessionManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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
        resend = findViewById(R.id.resend);
        timer = findViewById(R.id.timer);
        tap = findViewById(R.id.tap);
        layout1.setVisibility(View.VISIBLE);

        email = getIntent().getStringExtra("email");
        passw = getIntent().getStringExtra("password");
        name = getIntent().getStringExtra("name");

        sendCodeButton.setOnClickListener(v -> {
            phoneNumber = phoneNum.getText().toString();
            phonenumberText.setText(phoneNumber);
            resend.setVisibility(View.GONE);
            if (TextUtils.isEmpty(phoneNumber)) {
                phoneNum.setError("Enter a Phone Number");
                phoneNum.requestFocus();
            } else if (phoneNumber.length() < 10) {
                phoneNum.setError("Please enter a valid phone number");
                phoneNum.requestFocus();
            } else {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                verifyCodeET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (count >= 6){
                            verifyCodeButton.setEnabled(true);
                            tap.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, PhoneVerificationActivity.this, mCallbacks);
                new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timer.setText(String.valueOf(millisUntilFinished/1000));
                        counter++;
                    }
                    @Override
                    public void onFinish() {
                        resend.setVisibility(View.VISIBLE);
                        timer.setVisibility(View.GONE);
                    }
                }.start();
            }
        });

        resend.setOnClickListener(v -> {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, PhoneVerificationActivity.this, mCallbacks, mResendToken);
            timer.setVisibility(View.VISIBLE);
            new CountDownTimer(60000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timer.setText(String.valueOf(millisUntilFinished/1000));
                    counter++;
                }
                @Override
                public void onFinish() {
                    resend.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.GONE);
                }
            }.start();
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                user.updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(task -> {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                    TastyToast.makeText(PhoneVerificationActivity.this, "SUCCESFULLY LINKED", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                });
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

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
                user.updatePhoneNumber(credential).addOnCompleteListener(task -> {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                    TastyToast.makeText(PhoneVerificationActivity.this, "SUCCESFULLY LINKED", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                });
            }
        });

        button3.setOnClickListener(v -> {
            String user_ID = user.getUid();
            user_DB = FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(user_ID);
            user_DB.child("verified").setValue("true");
            user_DB.child("phone_verified").setValue("true");
            user_DB.child("phone").setValue(user.getPhoneNumber());
            user_DB.child("verification_ID").setValue(mVerificationId);
            Intent intent = new Intent(PhoneVerificationActivity.this, EditProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            sessionManager.createLoginSession(name, email, user_ID);
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = task.getResult().getUser();
                    } else {
                        TastyToast.makeText(PhoneVerificationActivity.this,"Something wrong",TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            TastyToast.makeText(PhoneVerificationActivity.this,"Something wrong",TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                        }
                    }
                });
    }
}

package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.major.alumniapp.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

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
                mAuth.getCurrentUser().linkWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                        } else {

                        }
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
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (currentStep < stepView.getStepCount() - 1) {
                                currentStep++;
                                stepView.go(currentStep, true);
                            } else {
                                stepView.done(true);
                            }
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.GONE);
                            layout3.setVisibility(View.VISIBLE);
                            // ...
                        } else {
                            TastyToast.makeText(PhoneVerificationActivity.this,"Something wrong",TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }
}

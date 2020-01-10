package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneRegisterActivity extends AppCompatActivity {

    private EditText phoneNumberEt, verificationCodeEt;
    private Button sendCodeBtn, verifyCodeBtn;
    private ProgressDialog progressDialog;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks myCallback;
    private PhoneAuthProvider.ForceResendingToken myResendToken;
    private String myVerificationId;


    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        phoneNumberEt = findViewById(R.id.phone_number_et_id);
        verificationCodeEt = findViewById(R.id.verification_code_et_id);
        sendCodeBtn = findViewById(R.id.send_verification_code_btn_id);
        verifyCodeBtn = findViewById(R.id.verify_btn_id);
        progressDialog = new ProgressDialog(this);

        myAuth = FirebaseAuth.getInstance();


        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEt.getText().toString();

                if (!TextUtils.isEmpty(phoneNumber)) {
                    progressDialog.setTitle("Number Verifying");
                    progressDialog.setMessage("Please wait.....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneRegisterActivity.this,               // Activity (for callback binding)
                            myCallback);        // OnVerificationStateChangedCallbacks


                } else {
                    Toast.makeText(PhoneRegisterActivity.this, "Give your Number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberEt.setVisibility(View.INVISIBLE);
                sendCodeBtn.setVisibility(View.INVISIBLE);

                String verificationCode = verificationCodeEt.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(PhoneRegisterActivity.this, "Give The verification code.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Code Verifying");
                    progressDialog.setMessage("Please wait....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(myVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        myCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneRegisterActivity.this, "Give a valid number", Toast.LENGTH_SHORT).show();
                phoneNumberEt.setVisibility(View.VISIBLE);
                sendCodeBtn.setVisibility(View.VISIBLE);
                verificationCodeEt.setVisibility(View.INVISIBLE);
                verifyCodeBtn.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                myVerificationId = verificationId;
                myResendToken = token;

                phoneNumberEt.setVisibility(View.INVISIBLE);
                sendCodeBtn.setVisibility(View.INVISIBLE);
                verificationCodeEt.setVisibility(View.VISIBLE);
                verifyCodeBtn.setVisibility(View.VISIBLE);

                progressDialog.dismiss();
            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        myAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //FirebaseUser user = task.getResult().getUser();
                            progressDialog.dismiss();
                            Toast.makeText(PhoneRegisterActivity.this, "Verification Completed.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneRegisterActivity.this, MainActivity.class));

                        } else {
                            String errorMsg = task.getException().toString();
                            Toast.makeText(PhoneRegisterActivity.this, "Error : "+ errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

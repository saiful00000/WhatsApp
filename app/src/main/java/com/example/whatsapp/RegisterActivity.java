 package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

 public class RegisterActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private TextView alreadyRegisteredTv;
    private Button registerBtn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        initFields();

        alreadyRegisteredTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }

     private void createNewAccount() {
         String userEmail = emailEt.getText().toString();
         String userPassword = passwordEt.getText().toString();

         if (TextUtils.isEmpty(userEmail)) {
             Toast.makeText(this, "Enter your email...", Toast.LENGTH_SHORT).show();
             emailEt.requestFocus();
         } else if (TextUtils.isEmpty(userPassword)) {
             Toast.makeText(this, "Enter your password...", Toast.LENGTH_SHORT).show();
             passwordEt.requestFocus();
         } else {

             showProgressDialog();

             firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                     .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()) {
                                 progressDialog.dismiss();
                                 startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                                 Toast.makeText(RegisterActivity.this, "Account created succesfully.", Toast.LENGTH_SHORT).show();
                             }else {
                                 progressDialog.dismiss();
                                 String errorMsg = task.getException().toString();
                                 Toast.makeText(RegisterActivity.this, "Erros : " + errorMsg, Toast.LENGTH_LONG).show();
                             }
                         }
                     });
         }
     }

     private void showProgressDialog() {
         progressDialog.setTitle("Account creating...");
         progressDialog.setMessage("Wait until finished...");
         progressDialog.setCanceledOnTouchOutside(true);
         progressDialog.show();
     }


     private void initFields() {

        emailEt = findViewById(R.id.registerr_email_id);
        passwordEt = findViewById(R.id.register_password_id);
        registerBtn = findViewById(R.id.register_button_id);
        alreadyRegisteredTv = findViewById(R.id.already_registered_tv_id);

        progressDialog = new ProgressDialog(this);
     }
 }

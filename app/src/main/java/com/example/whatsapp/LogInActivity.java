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
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private EditText loginEmailEt, loginPassEt;
    private Button loginBtn;
    private TextView createNewAcTv;


    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        firebaseAuth = FirebaseAuth.getInstance();

        findById();

        createNewAcTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogin();
            }
        });
    }

    private void allowUserToLogin() {

        String userEmail = loginEmailEt.getText().toString();
        String userPassword = loginPassEt.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter your email...", Toast.LENGTH_SHORT).show();
            loginEmailEt.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter your password...", Toast.LENGTH_SHORT).show();
            loginPassEt.requestFocus();
        } else {
            showProgressDialog();

            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                sentUserToMainActivity();
                            } else {
                                String errorMsg = Objects.requireNonNull(task.getException()).toString();
                                progressDialog.dismiss();
                                Toast.makeText(LogInActivity.this, "Error : "+ errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    private void showProgressDialog() {
        progressDialog.setTitle("Login in process....");
        progressDialog.setMessage("Wait until finished...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
    }


    private void sentUserToMainActivity() {

        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


    private void findById() {

        loginEmailEt = findViewById(R.id.login_email_id);
        loginPassEt = findViewById(R.id.login_password_id);
        loginBtn = findViewById(R.id.login_button_id);
        createNewAcTv = findViewById(R.id.creaete_ac_tv_id);

        progressDialog = new ProgressDialog(this);

    }
}

package com.example.whatsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhoneRegisterActivity extends AppCompatActivity {

    private EditText phoneNumberEt, verificationCodeEt;
    private Button sendCodeBtn, verifyCodeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        phoneNumberEt = findViewById(R.id.phone_number_et_id);
        verificationCodeEt = findViewById(R.id.verification_code_et_id);
        sendCodeBtn = findViewById(R.id.send_verification_code_btn_id);
        verifyCodeBtn = findViewById(R.id.verify_btn_id);

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberEt.setVisibility(View.INVISIBLE);
                sendCodeBtn.setVisibility(View.INVISIBLE);
                verificationCodeEt.setVisibility(View.VISIBLE);
                verifyCodeBtn.setVisibility(View.VISIBLE);
            }
        });

    }
}

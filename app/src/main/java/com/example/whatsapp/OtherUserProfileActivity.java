package com.example.whatsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class OtherUserProfileActivity extends AppCompatActivity {

    private String receivedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);


        receivedUserId = getIntent().getExtras().get("visitUserId").toString();



    }
}

package com.example.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText usernameEt, userStatusEt;
    private CircleImageView userPhoto;
    private Button updateButton;

    private String currentUserId;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        rootReference = FirebaseDatabase.getInstance().getReference();

        initFields();

        getUserInformation();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

    }

    private void getUserInformation() {

        rootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")) {
                        usernameEt.setText(dataSnapshot.child("name").getValue().toString());
                        userStatusEt.setText(dataSnapshot.child("status").getValue().toString());
                    } else if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")) {
                        usernameEt.setText(dataSnapshot.child("name").getValue().toString());
                        userStatusEt.setText(dataSnapshot.child("status").getValue().toString());
                    }
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Set profile info first.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateProfile() {

        String userName = usernameEt.getText().toString();
        String userStatus = userStatusEt.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Please Enter user name.", Toast.LENGTH_SHORT).show();
            usernameEt.requestFocus();
        } else if (TextUtils.isEmpty(userStatus)) {
            Toast.makeText(this, "Please enter yur status.", Toast.LENGTH_SHORT).show();
            userStatusEt.requestFocus();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", userName);
            profileMap.put("status", userStatus);

            rootReference.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateProfileActivity.this, "Profile updated succesfully..", Toast.LENGTH_SHORT).show();
                        sentUserToMainActivity();
                    } else {
                        String errorMsg = task.getException().toString();
                        Toast.makeText(UpdateProfileActivity.this, "Error : "+ errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    private void sentUserToMainActivity() {

        Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


    private void initFields() {
        usernameEt = findViewById(R.id.username_et_id);
        userStatusEt = findViewById(R.id.userstatus_et_id);
        userPhoto = findViewById(R.id.user_pic_imageview_id);
        updateButton = findViewById(R.id.update_profile_button_id);


    }
}

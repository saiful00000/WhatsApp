package com.example.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference;
    private String currentUserId;

    private LinearLayout profileLinearLayout;
    private TextView userNameTv, userStatusTv;

    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        rootReference = FirebaseDatabase.getInstance().getReference();


        profileLinearLayout = findViewById(R.id.linearlayut_profile_id);
        userNameTv = findViewById(R.id.user_name_tv_id);
        userStatusTv = findViewById(R.id.user_status_tv_id);
        profileImage = findViewById(R.id.profile_imageview_id);

        profileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(new Intent(getApplicationContext(), UpdateProfileActivity.class)));
            }
        });

        // retrive user information and set to fields
        getUserInformation();

    }

    private void getUserInformation() {

        rootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")
                            && dataSnapshot.hasChild("profile_image")) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        String userImage = dataSnapshot.child("profile_image").getValue().toString();

                        userNameTv.setText(userName);
                        userStatusTv.setText(userStatus);
                        Picasso.get().load(userImage).into(profileImage);

                    } else if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")) {
                        userNameTv.setText(dataSnapshot.child("name").getValue().toString());
                        userStatusTv.setText(dataSnapshot.child("status").getValue().toString());
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Set profile info first.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

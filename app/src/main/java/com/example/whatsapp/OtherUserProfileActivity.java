package com.example.whatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends AppCompatActivity {

    private CircleImageView userImage;
    private TextView userNameTv, userStatusTv;
    private Button sendMessageRequestBtn, cancelMessageRequestBtn;

    private String receiverUserId;
    private String currentState;
    private String senderUserId;

    private DatabaseReference userrReference, chatRequestReference, contactsReference;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);


        userImage = findViewById(R.id.user_profile_imageview_id);
        userNameTv = findViewById(R.id.username_tv_id);
        userStatusTv = findViewById(R.id.userstatus_tv_id);
        sendMessageRequestBtn = findViewById(R.id.send_msg_button_id);
        cancelMessageRequestBtn = findViewById(R.id.cancel_msg_button_id);

        currentState = "new";

        myAuth = FirebaseAuth.getInstance();
        senderUserId = myAuth.getCurrentUser().getUid().toString();

        // receive intent data
        receiverUserId = getIntent().getExtras().get("visitUserId").toString();

        // initialize the references
        userrReference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts");



        retriveUserInformation();

    }

    private void retriveUserInformation() {
        userrReference.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("profile_image")) {
                    String image = dataSnapshot.child("profile_image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getKey().toLowerCase();

                    Picasso.get().load(image).into(userImage);
                    userNameTv.setText(name);
                    userStatusTv.setText(status);

                    manageChatRequest();
                } else {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getKey().toLowerCase();

                    userNameTv.setText(name);
                    userStatusTv.setText(status);

                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest() {
        chatRequestReference.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserId)) {
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")) {
                                currentState = "request_sent";
                                sendMessageRequestBtn.setText("Cancel Chat Request");
                            } else if (request_type.equals("received")) {
                                currentState = "request_received";
                                sendMessageRequestBtn.setText("Accept Chat Request");

                                cancelMessageRequestBtn.setVisibility(View.VISIBLE);
                                cancelMessageRequestBtn.setEnabled(true);
                                cancelMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        currentState = "new";
                                        cancelChatRequest();
                                    }
                                });
                            }
                        } else {
                            contactsReference.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiverUserId)) {
                                        currentState = "friends";
                                        sendMessageRequestBtn.setText("Remove this contacs");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (!senderUserId.equals(receiverUserId)) {
            sendMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestBtn.setEnabled(false);
                    if (currentState.equals("new")) {
                        sendChatRequest();
                    }

                    if (currentState.equals("request_sent")) {
                        cancelChatRequest();
                    }

                    if (currentState.equals("request_received")) {
                        acceptChatRequest();
                    }
                }
            });
        } else {
            sendMessageRequestBtn.setVisibility(View.INVISIBLE);
        }
    }


    private void acceptChatRequest() {
        contactsReference.child(senderUserId).child(receiverUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            contactsReference.child(receiverUserId).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                chatRequestReference.child(senderUserId).child(receiverUserId)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isComplete()) {
                                                            chatRequestReference.child(receiverUserId).child(senderUserId)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendMessageRequestBtn.setEnabled(true);
                                                                    currentState = "friends";
                                                                    sendMessageRequestBtn.setText("Remove this Contact");
                                                                    cancelMessageRequestBtn.setVisibility(View.INVISIBLE);
                                                                    cancelMessageRequestBtn.setEnabled(false);
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                            }

                                        }
                                    });
                        }

                    }
                });
    }


    private void cancelChatRequest() {
        chatRequestReference.child(senderUserId).child(receiverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatRequestReference.child(receiverUserId).child(senderUserId)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendMessageRequestBtn.setEnabled(true);
                                currentState = "new";
                                sendMessageRequestBtn.setText("Send Chat Request");

                                cancelMessageRequestBtn.setVisibility(View.INVISIBLE);
                                cancelMessageRequestBtn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendChatRequest() {
        chatRequestReference.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        chatRequestReference.child(receiverUserId).child(senderUserId)
                                .child("request_type").setValue("received")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sendMessageRequestBtn.setEnabled(true);
                                            currentState = "request_sent";
                                            sendMessageRequestBtn.setText("Cancel Request");
                                        }
                                    }
                                });
                    }
                });
    }
}













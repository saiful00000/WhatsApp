package com.example.whatsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView msgRecyclerView;
    private EditText inputMsgtEt;
    private ImageButton sendMsgBtn;

    private TextView rcvrNameTv, rcvrLastSeenTv;
    private CircleImageView rcvrProfileImageView;

    private FirebaseAuth myAuth;
    private DatabaseReference rootReference;

    private String msgReceiverId, msgReceiverName , msgReceiverImage, msgSenderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myAuth = FirebaseAuth.getInstance();
        msgSenderId = myAuth.getCurrentUser().getUid();
        rootReference = FirebaseDatabase.getInstance().getReference();

        // get intent data
        msgReceiverId = getIntent().getExtras().get("visitUserId").toString();
        msgReceiverName = getIntent().getExtras().get("visitUserName").toString();
        msgReceiverImage = getIntent().getExtras().get("visitUserImage").toString();

        toolbar = findViewById(R.id.chat_activity_toolbar_id);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chatbar, null);
        actionBar.setCustomView(actionBarView);

        rcvrProfileImageView = findViewById(R.id.receiver_profile_image_view_id);
        rcvrNameTv = findViewById(R.id.receiver_profile_name_tv_id);
        rcvrLastSeenTv = findViewById(R.id.receiver_lastseen_tv_id);
        msgRecyclerView = findViewById(R.id.personal_msg_list_rv_id);
        inputMsgtEt = findViewById(R.id.input_personal_massage_et_id);
        sendMsgBtn = findViewById(R.id.send_personal_msg_button_id);

        if (!msgReceiverImage.equals("default_image")) {
            Picasso.get().load(msgReceiverImage).into(rcvrProfileImageView);
        }
        rcvrNameTv.setText(msgReceiverName.toString());
        rcvrLastSeenTv.setText("Last seen: ");

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }


    private void sendMessage() {

        String messageTxt = inputMsgtEt.getText().toString();

        if (!TextUtils.isEmpty(messageTxt)) {
            String msgSenderReference = "Message/" + msgSenderId + "/" + msgReceiverId;
            String msgReceaverReference = "Message/" + msgReceiverId + "/" + msgSenderId;

            DatabaseReference userMsgkeyRef = rootReference.child("Messages")
                    .child(msgSenderId).child(msgReceiverId).push();

            String msgPushId = userMsgkeyRef.getKey();

            Map msgTextBody = new HashMap();
            msgTextBody.put("message", messageTxt);
            msgTextBody.put("type", "text");
            msgTextBody.put("from", msgSenderId);

            Map msgBodyDetails = new HashMap();
            msgBodyDetails.put(msgSenderReference + "/" + msgPushId, msgTextBody);
            msgBodyDetails.put(msgReceaverReference + "/" + msgPushId, msgTextBody);

            rootReference.updateChildren(msgBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        inputMsgtEt.setText("");
                    } else {
                        Toast.makeText(ChatActivity.this, "Error while sending msg.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
package com.example.whatsapp;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView msgRecyclerView;
    private EditText inputMsgtEt;
    private ImageButton sendMsgBtn;

    private TextView rcvrNameTv, rcvrLastSeenTv;
    private CircleImageView rcvrProfileImageView;

    private String msgReceiverId, msgReceiverName = "", msgReceiverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        rcvrNameTv.setText(msgReceiverName.toString());
        rcvrLastSeenTv.setText("Last seen");

        Toast.makeText(this, msgReceiverName +"\n"+ msgReceiverId, Toast.LENGTH_SHORT).show();


    }
}
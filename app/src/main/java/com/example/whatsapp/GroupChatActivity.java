package com.example.whatsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ScrollView mScrollView;
    private TextView displayMassageTv;
    private EditText msgInputEt;
    private ImageButton sendButton;

    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // receive intent extras
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, currentGroupName, Toast.LENGTH_SHORT).show();

        mToolbar = (Toolbar) findViewById(R.id.groupe_chat_bar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);


        mScrollView = findViewById(R.id.group_chat_scroll_view_id);
        displayMassageTv = findViewById(R.id.groupe_chat_text_display_tv_id);
        msgInputEt = findViewById(R.id.input_group_massage_id);
        sendButton = findViewById(R.id.send_group_msg_button_id);




    }
}

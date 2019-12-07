package com.example.whatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ScrollView mScrollView;
    private TextView displayMassageTv;
    private EditText msgInputEt;
    private ImageButton sendButton;

    private String currentGroupName, currentUserId, currentUserName;
    private String currentDate, currentTime;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference, groupNameReference, groupMassageKeyReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // receive intent extras
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, currentGroupName, Toast.LENGTH_SHORT).show();


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid().toString();

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);


        mToolbar = (Toolbar) findViewById(R.id.groupe_chat_bar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);


        mScrollView = findViewById(R.id.group_chat_scroll_view_id);
        displayMassageTv = findViewById(R.id.groupe_chat_text_display_tv_id);
        msgInputEt = findViewById(R.id.input_group_massage_id);
        sendButton = findViewById(R.id.send_group_msg_button_id);

        getUserInfo();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMassageInformationIntoGroup();
                msgInputEt.setText("");
            }
        });

    }


    private void getUserInfo() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child(currentUserId).child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveMassageInformationIntoGroup() {
        String massage = msgInputEt.getText().toString();
        // generate massage key
        String massageKey = groupNameReference.push().getKey();

        if (TextUtils.isEmpty(massage)) {
            Toast.makeText(this, "Msg box is Empty", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calendarForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calendarForDate.getTime());

            Calendar calendarForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentDateFormat.format(calendarForTime.getTime());

            HashMap<String, Object> groupMassageKey = new HashMap<>();
            groupNameReference.updateChildren(groupMassageKey);

            groupMassageKeyReference = groupNameReference.child(massageKey);

            HashMap<String, Object> massageInfoMap = new HashMap<>();
                massageInfoMap.put("name", currentUserName);
                massageInfoMap.put("massaage", massage);
                massageInfoMap.put("date", currentDate);
                massageInfoMap.put("time", currentTime);

            groupMassageKeyReference.updateChildren(massageInfoMap);

        }
    }

}

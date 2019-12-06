package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.adapters.TabsAccessAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessAdapter myTabAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference rootReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //primary #128C7E  dark #075E54

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        rootReference = FirebaseDatabase.getInstance().getReference();

        myToolbar = (Toolbar) findViewById(R.id.main_page_toolbar_id);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Saifuls Chat");


        myViewPager = findViewById(R.id.main_pager_id);
        myTabAccessorAdapter = new TabsAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);


        myTabLayout = findViewById(R.id.main_tab_id);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            senduserToLogInActivity();
        }else{
            verifyUserExistance();
        }
    }

    private void verifyUserExistance() {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();


        rootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())) {
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                } else {
                    sendUserToUpdateProfileActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendUserToUpdateProfileActivity() {

        Intent intent = new Intent(MainActivity.this, UpdateProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_find_friend_id:
                return true;
            case R.id.menu_item_create_group_id:
                requestNewGroup();
                return true;
            case R.id.menu_item_logout_id:
                userLogOut();
                return true;
            case R.id.menu_item_settings_id:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText groupNameFieldEt = new EditText(MainActivity.this);
        groupNameFieldEt.setHint("e.g Group Name");
        builder.setView(groupNameFieldEt);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameFieldEt.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "Enter a group name first.", Toast.LENGTH_SHORT).show();
                } else {
                    createNewGroupe(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroupe(String groupName) {
        rootReference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Groupe Added successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void userLogOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
    }


    private void senduserToLogInActivity() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

package com.example.whatsapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.whatsapp.adapters.TabsAccessAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessAdapter myTabAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

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
        }
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
            case R.id.menu_item_logout_id:
                userLogOut();
                return true;
            case R.id.menu_item_settings_id:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void userLogOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
    }


    private void senduserToLogInActivity() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}

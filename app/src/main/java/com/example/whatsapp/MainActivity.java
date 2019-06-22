package com.example.whatsapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.whatsapp.adapters.TabsAccessAdapter;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessAdapter myTabAccessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myToolbar = (Toolbar) findViewById(R.id.main_page_toolbar_id);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Saifuls Chat");


        myViewPager = findViewById(R.id.main_pager_id);
        myTabAccessorAdapter = new TabsAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);


        myTabLayout = findViewById(R.id.main_tab_id);
        myTabLayout.setupWithViewPager(myViewPager);

    }
}

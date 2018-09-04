package com.example.restaurantdemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.adapter.PagerAdapter;
import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.common.DBInitHelper;

public class LoginActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        FragmentManager manager = getSupportFragmentManager();

        PagerAdapter adapter = new PagerAdapter(manager);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);

        // Set DB name
        SharedPreferences mPref = getSharedPreferences( "Setting", Context.MODE_PRIVATE );
        SharedPreferences.Editor prefEditor = mPref.edit();
        prefEditor.putString( Const.DB_NAME, getApplicationContext().getDatabasePath("myrest.db").getPath());
        prefEditor.apply();

        // Create Database
        DBInitHelper db = new DBInitHelper(mPref.getString(Const.DB_NAME, "myrest.db"));
        db.initialize(getResources().openRawResource(R.raw.item));
    }
}

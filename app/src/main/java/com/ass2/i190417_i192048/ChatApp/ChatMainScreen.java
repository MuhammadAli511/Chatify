package com.ass2.i190417_i192048.ChatApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.ass2.i190417_i192048.Adapters.FragmentsAdapter;
import com.ass2.i190417_i192048.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatMainScreen extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main_screen);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_chat_bubble_outline_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_call_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_add_box_24);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_person_outline_24);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_baseline_settings_24);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("UsersStatus").child(id).setValue("Online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("UsersStatus").child(id).setValue("Online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("UsersStatus").child(id).setValue("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("UsersStatus").child(id).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("UsersStatus").child(id).setValue("Online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
        String dateText = formatter.format(cal.getTime());
        String finalText = "Last Seen: " + dateText;
        db.getReference().child("UsersStatus").child(id).setValue(finalText);
    }
}
package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Adapters.MusicAdapter;
import com.ass2.i190417_i192048.Adapters.PlaylistAdapter;
import com.ass2.i190417_i192048.ChatApp.ChatMainScreen;
import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.Models.Playlists;
import com.ass2.i190417_i192048.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {
    LinearLayout liked, add, search, listenlater;
    RecyclerView playlistRecyclerViewMain;
    FirebaseFirestore db;
    List<Playlists> playlistsList;
    PlaylistAdapter playlistAdapter;

    RecyclerView musicRecyclerViewMain;
    List<Music> musicList;
    MusicAdapter musicAdapter;

    ImageView profileDrawerLoader, profileImage, chatIcon;
    TextView userName;
    LinearLayout logout, brownDrawer;
    DrawerLayout profileDrawer;
    TextView forgotPassword;
    FirebaseUser user;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Variables
        liked = findViewById(R.id.liked);
        add = findViewById(R.id.add);
        search = findViewById(R.id.search);
        listenlater = findViewById(R.id.listenlater);
        logout = findViewById(R.id.logout);
        brownDrawer = findViewById(R.id.brownDrawer);
        profileDrawerLoader = findViewById(R.id.profileDrawerLoader);
        profileDrawer = findViewById(R.id.profileDrawer);
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        forgotPassword = findViewById(R.id.forgotPassword);
        chatIcon = findViewById(R.id.chatIcon);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        mAuth = FirebaseAuth.getInstance();
        db.collection("Users").document(id).update("status", "Offline");

        playlistRecyclerViewMain = findViewById(R.id.playlistRecyclerViewMain);
        playlistsList = new ArrayList<>();

        musicRecyclerViewMain = findViewById(R.id.musicRecyclerViewMain);
        musicList = new ArrayList<>();

        String deviceIDStr = OneSignal.getDeviceState().getUserId();
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).update("deviceID", deviceIDStr);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        listenlater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, ListenLater.class);
                startActivity(intent);
            }
        });

        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Liked.class);
                startActivity(intent);
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, ChatMainScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        brownDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Test", "Working");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, SearchMusic.class);
                startActivity(intent);
            }
        });

        profileDrawerLoader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileDrawer.isDrawerOpen(brownDrawer)) {
                    profileDrawer.closeDrawer(brownDrawer);
                } else {
                    profileDrawer.openDrawer(brownDrawer);
                }
            }
        });

        if (user != null) {
            userName.setText(user.getDisplayName());
            // set profile image using glide
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).into(profileImage);
            }
        }

        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String id1 = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child("UsersStatus").child(id1).setValue("Offline");




        // Layouts
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        playlistRecyclerViewMain.setLayoutManager(layoutManager);
        getData();


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        musicRecyclerViewMain.setLayoutManager(layoutManager2);
        getData2();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainScreen.this, Signin.class);
                startActivity(intent);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, UploadMusic.class));
            }
        });

    }

    public void getData(){
        db.collection("playlists").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Playlists playlists = new Playlists(document.getString("playlistName"), document.getString("imageURL"), document.getString("userID"));
                        playlistsList.add(playlists);
                    }
                    playlistAdapter = new PlaylistAdapter(MainScreen.this, playlistsList);
                    playlistRecyclerViewMain.setAdapter(playlistAdapter);
                    playlistAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainScreen.this, "Error getting Playlists.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void getData2(){

        db.collection("Music").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Music music = new Music(document.getString("title"), document.getString("genre"), document.getString("description"), document.getString("musicURL"), document.getString("imageURL"), document.getString("userID"));
                        musicList.add(music);
                    }
                    musicAdapter = new MusicAdapter(MainScreen.this, musicList);
                    musicRecyclerViewMain.setAdapter(musicAdapter);
                    musicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainScreen.this, "Error getting Music.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicAdapter != null) {
            musicAdapter.notifyDataSetChanged();
        }
    }
}
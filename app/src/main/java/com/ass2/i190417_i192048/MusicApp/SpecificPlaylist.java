package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Adapters.MusicAdapter;
import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SpecificPlaylist extends AppCompatActivity {
    ImageView back;
    TextView playlistName;

    FirebaseFirestore db;
    RecyclerView musicRecyclerViewMain2;
    List<Music> musicList;
    MusicAdapter musicAdapter;
    String playlistNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_playlist);

        Intent oldIntent = getIntent();
        playlistNameString = oldIntent.getStringExtra("playlistName");


        back = findViewById(R.id.back);
        playlistName = findViewById(R.id.playlistName);
        playlistName.setText(playlistNameString);

        db = FirebaseFirestore.getInstance();
        musicRecyclerViewMain2 = findViewById(R.id.musicRecyclerViewMain2);
        musicList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        musicRecyclerViewMain2.setLayoutManager(layoutManager);
        getData();

        back.setOnClickListener(v -> {
            finish();
        });

    }

    public void getData(){
        db.collection("Music").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Music music = new Music(document.getString("title"), document.getString("genre"), document.getString("description"), document.getString("musicURL"), document.getString("imageURL"), document.getString("userID"));
                        String playList2 = document.getString("playlistName");
                        if (playlistNameString.equals(playList2)){
                            musicList.add(music);
                        }
                    }
                    musicAdapter = new MusicAdapter(SpecificPlaylist.this, musicList);
                    musicRecyclerViewMain2.setAdapter(musicAdapter);
                    musicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SpecificPlaylist.this, "Error getting Music.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




}
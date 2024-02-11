package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Adapters.PlaylistAdapter2;
import com.ass2.i190417_i192048.MainActivity;
import com.ass2.i190417_i192048.Models.Playlists;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectPlaylist extends AppCompatActivity {
    ImageView addPlaylistButton;
    RecyclerView selectPlaylistRecyclerView;
    List<Playlists> playlistsList;
    TextView skip;
    PlaylistAdapter2 playlistAdapter;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_playlist);

        addPlaylistButton = findViewById(R.id.addPlaylistButton);
        skip = findViewById(R.id.skip);
        selectPlaylistRecyclerView = findViewById(R.id.selectPlaylistRecyclerView);
        db = FirebaseFirestore.getInstance();
        playlistsList = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectPlaylistRecyclerView.setLayoutManager(layoutManager);
        getData();
        // get data from intent
        Intent intentOld = getIntent();
        String title = intentOld.getStringExtra("title");



        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectPlaylist.this, AddPlaylist.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectPlaylist.this, MainScreen.class);
                startActivity(intent);
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
                    playlistAdapter = new PlaylistAdapter2(SelectPlaylist.this, playlistsList);
                    selectPlaylistRecyclerView.setAdapter(playlistAdapter);
                    playlistAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SelectPlaylist.this, "Error getting Playlists.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
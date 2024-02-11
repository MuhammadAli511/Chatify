package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Adapters.MusicAdapter;
import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Liked extends AppCompatActivity {

    RecyclerView recyclerViewLiked;
    List<Music> musicList;
    MusicAdapter musicAdapter;
    FirebaseFirestore db;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        recyclerViewLiked = findViewById(R.id.recyclerViewLiked);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            finish();
        });

        musicList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewLiked.setLayoutManager(layoutManager);
        getData();
    }

    public void getData(){
        db.collection("LikedMusic").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("LikedMusic").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Music music = new Music(document.getString("title"), document.getString("genre"), document.getString("description"), document.getString("musicURL"), document.getString("imageURL"), document.getString("userID"));
                        musicList.add(music);
                    }
                    musicAdapter = new MusicAdapter(Liked.this,musicList);
                    recyclerViewLiked.setAdapter(musicAdapter);
                    // notify adapter
                    musicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Liked.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
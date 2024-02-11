package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Adapters.MusicAdapter;
import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchMusic extends AppCompatActivity {

    ImageView back;
    EditText searchMusicEditor;
    RecyclerView musicSearchRecyclerView;
    List<Music> musicList;
    MusicAdapter musicAdapter;
    ValueEventListener valueEventListener;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);

        back = findViewById(R.id.back);
        searchMusicEditor = findViewById(R.id.searchMusicEditor);
        musicSearchRecyclerView = findViewById(R.id.musicSearchRecyclerView);
        musicList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicSearchRecyclerView.setLayoutManager(layoutManager);

        back.setOnClickListener(v -> {
            finish();
        });

        searchMusicEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = searchMusicEditor.getText().toString();
                Log.d("searchText", searchText);
                if (searchText.length() > 0) {
                    musicList.clear();
                    searchMusic(searchText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void searchMusic(String searchText) {
        db.collection("Music").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Music music = new Music(document.getString("title"), document.getString("genre"), document.getString("description"), document.getString("musicURL"), document.getString("imageURL"), document.getString("userID"));
                        String title = music.getTitle();
                        if (title.toLowerCase().contains(searchText.toLowerCase())) {
                            musicList.add(music);
                        }
                    }
                    musicAdapter = new MusicAdapter(SearchMusic.this, musicList);
                    musicSearchRecyclerView.setAdapter(musicAdapter);
                    musicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SearchMusic.this, "Error getting Music.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
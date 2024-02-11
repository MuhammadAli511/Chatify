package com.ass2.i190417_i192048.MusicApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ass2.i190417_i192048.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MusicAdded extends AppCompatActivity {

    Button returnButton;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_added);
        returnButton = findViewById(R.id.returnButton);

        Intent oldIntent = getIntent();
        String title = oldIntent.getStringExtra("title");
        String playlistName = oldIntent.getStringExtra("playlistName");
        db = FirebaseFirestore.getInstance();
        db.collection("Music").document(title).update("playlistName", playlistName);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicAdded.this, MainScreen.class);
                startActivity(intent);
            }
        });

    }
}
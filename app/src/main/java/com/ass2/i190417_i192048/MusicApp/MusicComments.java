package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Adapters.CommentAdapter;
import com.ass2.i190417_i192048.Models.Comments;
import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MusicComments extends AppCompatActivity {
    ImageView back, sendComment;
    TextView songTitle;
    RecyclerView commentsRecyclerView;
    EditText editTextComment;
    List<Comments> commentsList;
    CommentAdapter commentAdapter;
    FirebaseFirestore db;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_comments);

        back = findViewById(R.id.back);
        sendComment = findViewById(R.id.sendComment);
        songTitle = findViewById(R.id.songTitle);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        editTextComment = findViewById(R.id.editTextComment);

        db = FirebaseFirestore.getInstance();
        commentsList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(layoutManager);
        Intent intentOld = getIntent();
        title = intentOld.getStringExtra("title");
        List<Music> musicList = (List<Music>) intentOld.getSerializableExtra("musicList");
        getData();
        // get data from intent




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicComments.this, PlayMusic.class);
                intent.putExtra("musicList", (ArrayList) musicList);
                startActivity(intent);
            }
        });


        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editTextComment.getText().toString();
                if (comment.isEmpty()) {
                    Toast.makeText(MusicComments.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                } else {
                    Comments comments = new Comments(comment);
                    comments.setMusicTitle(title);
                    db.collection("Comments").add(comments).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MusicComments.this, "Comment added", Toast.LENGTH_SHORT).show();
                                editTextComment.setText("");
                                commentsList.clear();
                                getData();
                            } else {
                                Toast.makeText(MusicComments.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        songTitle.setText(title);

    }

    public void getData(){
        db.collection("Comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Comments comments2 = new Comments(document.getString("commentBody"));
                        String title2 = document.getString("musicTitle");
                        if (title2.equals(title)) {
                            commentsList.add(comments2);
                        }
                    }
                    commentAdapter = new CommentAdapter(MusicComments.this, commentsList);
                    commentsRecyclerView.setAdapter(commentAdapter);
                } else {
                    Toast.makeText(MusicComments.this, "Error getting Comments.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
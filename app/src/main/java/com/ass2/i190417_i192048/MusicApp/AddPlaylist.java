package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Models.Playlists;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddPlaylist extends AppCompatActivity {
    ImageView clickPhoto;
    EditText playListNameEditor;
    Button createPlaylist;
    FirebaseStorage storage;
    FirebaseFirestore db;
    StorageReference storageRef;
    Uri imageURI;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        clickPhoto = findViewById(R.id.clickPhoto);
        playListNameEditor = findViewById(R.id.playListNameEditor);
        createPlaylist = findViewById(R.id.createPlaylist);
        storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        progressDialog.setMessage("Creating Playlist...");

        Intent oldIntent = getIntent();
        String title = oldIntent.getStringExtra("title");

        clickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playListNameEditor.getText().toString().isEmpty() || imageURI == null) {
                    playListNameEditor.setError("Playlist name is required");
                } else {
                    progressDialog.show();
                    StorageReference imageRef = storageRef.child("images/" + playListNameEditor.getText().toString());
                    UploadTask uploadTask = imageRef.putFile(imageURI);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadURL) {
                                    String imageUrl = downloadURL.toString();
                                    String playlistName = playListNameEditor.getText().toString();
                                    // get logged in user id
                                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    Playlists playlist = new Playlists(playlistName, imageUrl, userID);
                                    db.collection("playlists").document(playlistName).set(playlist);
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(AddPlaylist.this, SelectPlaylist.class);
                                    intent.putExtra("title", title);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    AddPlaylist.this.finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPlaylist.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageURI = data.getData();
            clickPhoto.setImageURI(imageURI);
        }
    }

}



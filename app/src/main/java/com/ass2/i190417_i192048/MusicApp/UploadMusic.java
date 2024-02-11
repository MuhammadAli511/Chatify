package com.ass2.i190417_i192048.MusicApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class UploadMusic extends AppCompatActivity {
    ImageView back, uploadImage;
    Button  uploadMusic;
    EditText title, genre, description;
    Button upload, record;
    FirebaseStorage storage;
    FirebaseFirestore db;
    StorageReference storageRef;
    Uri musicURI;
    Uri imageURI;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_music);

        back = findViewById(R.id.back);
        uploadImage = findViewById(R.id.uploadImage);
        uploadMusic = findViewById(R.id.uploadMusic);
        title = findViewById(R.id.title);
        genre = findViewById(R.id.genre);
        description = findViewById(R.id.description);
        upload = findViewById(R.id.upload);
        record = findViewById(R.id.record);
        storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Music...");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadMusic.this, MainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                UploadMusic.this.finish();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });

        uploadMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Music"), 101);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty() || genre.getText().toString().isEmpty() || description.getText().toString().isEmpty() || imageURI == null || musicURI == null) {
                    Toast.makeText(UploadMusic.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    StorageReference audioRef = storageRef.child("audio/" + title.getText().toString());
                    UploadTask uploadTask = audioRef.putFile(musicURI);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadURL) {
                                    String audioURL = downloadURL.toString();
                                    StorageReference imageRef = storageRef.child("images/" + title.getText().toString());
                                    UploadTask uploadTask = imageRef.putFile(imageURI);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri downloadURL) {
                                                    String imageURL = downloadURL.toString();
                                                    Music music = new Music(title.getText().toString(), genre.getText().toString(), description.getText().toString(), audioURL, imageURL, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    db.collection("Music").document(title.getText().toString()).set(music).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(UploadMusic.this, SelectPlaylist.class);
                                                            intent.putExtra("title", title.getText().toString());
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            UploadMusic.this.finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(UploadMusic.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UploadMusic.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadMusic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("UploadMusic", e.getMessage());
                        }
                    });

                }
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadMusic.this, RecordMusic.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                UploadMusic.this.finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            uploadImage.setImageURI(data.getData());
            imageURI = data.getData();
        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            musicURI = data.getData();
        }
    }


}
package com.ass2.i190417_i192048.MusicApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
// glide
import com.ass2.i190417_i192048.Models.Music;
import com.ass2.i190417_i192048.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayMusic extends AppCompatActivity {

    TextView musicTitle, currentTime;
    SeekBar musicSlider, volumeSlider;
    ImageView musicImage, previousButton, pauseButton, nextButton, comment, likedMusic, listenLaterButton;
    List<Music> musicList;
    Music currentSong;
    MediaPlayer mediaPlayer = MusicMediaPlayer.getInstance();
    AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        Intent oldIntent = getIntent();
        musicList = (List<Music>) oldIntent.getSerializableExtra("musicList");
        Log.d("Music List Size : " , String.valueOf(musicList.size()));


        musicTitle = findViewById(R.id.musicTitle);
        currentTime = findViewById(R.id.currentTime);
        musicSlider = findViewById(R.id.musicSlider);
        volumeSlider = findViewById(R.id.volumeSlider);
        musicImage = findViewById(R.id.musicImage);
        previousButton = findViewById(R.id.previousButton);
        pauseButton = findViewById(R.id.pauseButton);
        nextButton = findViewById(R.id.nextButton);
        comment = findViewById(R.id.comment);
        likedMusic = findViewById(R.id.likedMusic);
        listenLaterButton = findViewById(R.id.listenLaterButton);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSlider.setMax(maxVolume);
        volumeSlider.setProgress(currentVolume);
        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        likedMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String musicTitle = currentSong.getTitle();
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("LikedMusic").document(userID).collection("LikedMusic").document(musicTitle).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // already liked
                            db.collection("LikedMusic").document(userID).collection("LikedMusic").document(musicTitle).delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(PlayMusic.this, "Removed from Liked Music", Toast.LENGTH_SHORT).show();
                                    likedMusic.setImageResource(R.drawable.heart);
                                }
                            });
                            Toast.makeText(PlayMusic.this, "Music Unliked", Toast.LENGTH_SHORT).show();
                        } else {
                            // not liked
                            db.collection("LikedMusic").document(userID).collection("LikedMusic").document(musicTitle).set(currentSong);
                            Toast.makeText(PlayMusic.this, "Liked", Toast.LENGTH_SHORT).show();
                            likedMusic.setImageResource(R.drawable.whiteheart);
                        }
                    }
                });

            }
        });

        listenLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String musicTitle = currentSong.getTitle();
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("ListenLater").document(userID).collection("ListenLater").document(musicTitle).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            db.collection("ListenLater").document(userID).collection("ListenLater").document(musicTitle).delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(PlayMusic.this, "Removed from Listen Later", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(PlayMusic.this, "Music Removed from Listen Later", Toast.LENGTH_SHORT).show();
                        } else {
                            db.collection("ListenLater").document(userID).collection("ListenLater").document(musicTitle).set(currentSong);
                            Toast.makeText(PlayMusic.this, "Added to Listen Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });



        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayMusic.this, MusicComments.class);
                intent.putExtra("title", currentSong.getTitle());
                intent.putExtra("musicList", (Serializable)musicList);
                startActivity(intent);
            }
        });

        try {
            setValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PlayMusic.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                    musicSlider.setProgress(mCurrentPosition);
                    currentTime.setText(convertToMMSS(mCurrentPosition +""));
                    if (mediaPlayer.isPlaying()){
                        pauseButton.setImageResource(R.drawable.pause);
                    } else{
                        pauseButton.setImageResource(R.drawable.playone);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        musicSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void setValues() throws IOException {
        currentSong = musicList.get(MusicMediaPlayer.currentIndex);
        Log.d("currentSong", currentSong.getTitle() + " " + currentSong.getDescription() + " " + currentSong.getImageURL() + " " + currentSong.getGenre() + " " + currentSong.getMusicURL());
        musicTitle.setText(currentSong.getTitle());
        Glide.with(this).load(currentSong.getImageURL()).into(musicImage);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicMediaPlayer.currentIndex == musicList.size() - 1) {
                    Toast.makeText(PlayMusic.this, "No more songs", Toast.LENGTH_SHORT).show();
                    return;
                }
                MusicMediaPlayer.currentIndex += 1;
                mediaPlayer.reset();
                try {
                    setValues();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicMediaPlayer.currentIndex == 0) {
                    return;
                }
                MusicMediaPlayer.currentIndex -= 1;
                mediaPlayer.reset();
                try {
                    setValues();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        playMusic();



    }

    void playMusic() throws IOException {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getMusicURL());
            mediaPlayer.prepare();
            mediaPlayer.start();
            musicSlider.setProgress(0);
            musicSlider.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        String returnValue = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return returnValue;

    }



}
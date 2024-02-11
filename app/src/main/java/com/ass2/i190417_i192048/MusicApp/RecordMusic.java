package com.ass2.i190417_i192048.MusicApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ass2.i190417_i192048.R;

import java.io.File;

public class RecordMusic extends AppCompatActivity {
    ImageView back;
    Button startRecording1;
    MediaRecorder mediaRecorder;
    String pathSave = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_music);
        back = findViewById(R.id.back);
        startRecording1 = findViewById(R.id.startRecording);

        if(isMicrophonePresent())
            getMicrophonePermission();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordMusic.this, MainScreen.class);
                startActivity(intent);
            }
        });

        startRecording1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startRecording1.getText().toString().equalsIgnoreCase("Start Recording")) {
                    startRecording1.setText("Stop Recording");
                    startRecording();
                } else {
                    startRecording1.setText("Start Recording");
                    stopRecording();
                }
            }
        });
    }

    boolean isMicrophonePresent(){
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO},100);
        }
    }

    private String getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory,System.currentTimeMillis() + ".mp3");
        return file.getPath();
    }

    void startRecording() {
        pathSave = getRecordingFilePath();
        Log.d("pathSave", pathSave);
        setupMediaRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(pathSave);
    }


    void stopRecording() {
        mediaRecorder.stop();
        Intent intent = new Intent(RecordMusic.this, RecordMusicDetails.class);
        intent.putExtra("path", pathSave);
        startActivity(intent);
    }




}
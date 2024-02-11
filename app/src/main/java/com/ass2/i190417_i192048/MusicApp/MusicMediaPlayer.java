package com.ass2.i190417_i192048.MusicApp;

import android.media.MediaPlayer;

public class MusicMediaPlayer {
    static MediaPlayer instance;
    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = -1;

}

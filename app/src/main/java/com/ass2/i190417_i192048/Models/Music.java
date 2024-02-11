package com.ass2.i190417_i192048.Models;

import java.io.Serializable;

public class Music implements Serializable {
    String title;
    String genre;
    String description;
    String musicURL;
    String imageURL;
    String userID;
    String playlistName;


    public Music(String title, String genre, String description, String musicURL, String imageURL, String userID) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.musicURL = musicURL;
        this.imageURL = imageURL;
        this.userID = userID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMusicURL() {
        return musicURL;
    }

    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

package com.ass2.i190417_i192048.Models;


public class Playlists {
    String playlistName;
    String imageURL;
    String userID;

    public Playlists(String playlistName, String imageURL, String UserID) {
        this.playlistName = playlistName;
        this.imageURL = imageURL;
        this.userID = UserID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}

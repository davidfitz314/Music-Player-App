package com.example.musicplayerpreview;

public class Song {
    private long songId;
    private String title;
    private String artist;
    private String file_path;

    public Song(String title_in, String artist_in, String location_in){
        title = title_in;
        artist = artist_in;
        file_path = location_in;
    }

    public long getSongId() {
        return songId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getFile_path() {
        return file_path;
    }
}

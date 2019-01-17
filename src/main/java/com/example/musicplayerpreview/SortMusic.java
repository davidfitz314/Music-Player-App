package com.example.musicplayerpreview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.Collections;
import java.util.LinkedList;

public class SortMusic {
    private LinkedList<Song> song_list;
    private final String sAlphabet = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890-_\t\nbsp+=!@#$%^&*?~`";

    SortMusic(LinkedList<Song> list_in){
        this.song_list = list_in;
    }

    public LinkedList<Song> sortSong(){
        try {
            new sortSongsAsync().execute(song_list);
            return song_list;
        } catch (Exception e){
            Log.d("ERROR", e.getMessage());
        }
        return song_list;
    }

    private static class sortSongsAsync extends AsyncTask<LinkedList<Song>, Void, Void>{
        private final String sAlphabet = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890-_\t\nbsp+=!@#$%^&*?~`";


        @Override
        protected Void doInBackground(LinkedList<Song>... songLists) {
            if (songLists[0].size() > 1) {
                for (int i = 0; i < songLists[0].size() - 1; i++) {
                    for (int j = i + 1; j < songLists[0].size(); j++) {
                        String songA = songLists[0].get(i).getTitle();
                        String songB = songLists[0].get(j).getTitle();
                        int compare = compareSongs(songA, songB);
                        if (compare > 0) {
                            //swap the two elements
                            Collections.swap(songLists[0], i, j);
                        }
                    }
                }
            }
            return null;
        }

        private int compareSongs(String songA_in, String songB_in){
            int count = 0;
            while (count < songA_in.length() && count < songB_in.length()){
                char findA = songA_in.charAt(0);
                int aComp = sAlphabet.indexOf(findA);
                char findB = songB_in.charAt(0);
                int bComp = sAlphabet.indexOf(findB);
                if (aComp < bComp){
                    return -1;
                }
                if (aComp > bComp){
                    return 1;
                }
                count ++;
            }
            return 0;
        }
    }
}

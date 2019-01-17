package com.example.musicplayerpreview;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private static final int PERMISSION_REQUEST = 1;
    private LinkedList<Song> mSongs = new LinkedList<>();
    private boolean sortClick = false;
    private FloatingActionButton sortFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check if app has permission to view device files
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        } else {
            //gather music from device memory
            getMusic();
        }

        //initialize the recycler and connect it to the recyclers view
        mRecyclerView = findViewById(R.id.recyclerview);
        //adapter and supply it data
        mAdapter = new SongsAdapter(this, mSongs);
        //connect recycler and the adapter
        mRecyclerView.setAdapter(mAdapter);
        //set recycler views default layout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //floatactionbutton
        sortFab = findViewById(R.id.fab_sort_button);


    }

    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);
                Song temp = new Song(currentTitle, currentArtist, currentLocation);
                mSongs.add(temp);
            } while (songCursor.moveToNext());
        }
    }

    public void sortSongsList(View view) {
        LinkedList<Song> tempList = new SortMusic(this.mSongs).sortSong();
        if (sortClick) {
            mSongs = new LinkedList<Song>();
            getMusic();
            //adapter and supply it data
            mAdapter = new SongsAdapter(this, mSongs);
            //connect recycler and the adapter
            mRecyclerView.setAdapter(mAdapter);
            //set recycler views default layout
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            sortFab.setImageDrawable(getDrawable(R.drawable.ic_sort_by_alpha_black_24dp));
            sortClick = false;

        } else {
            if (tempList != null) {
                //adapter and supply it data
                mAdapter = new SongsAdapter(this, tempList);
                //connect recycler and the adapter
                mRecyclerView.setAdapter(mAdapter);
                //set recycler views default layout
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                sortFab.setImageDrawable(getDrawable(R.drawable.ic_cancel_black_24dp));
                sortClick = true;
            }
        }
    }
}

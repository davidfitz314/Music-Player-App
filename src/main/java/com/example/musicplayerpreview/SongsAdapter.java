package com.example.musicplayerpreview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {
    private LinkedList<Song> mSongList;
    private LayoutInflater mInflater;
    private Context mContext;


    public SongsAdapter(Context context, LinkedList<Song> songList) {
        mInflater = LayoutInflater.from(context);
        this.mSongList = songList;
        mContext = context;
    }

    @NonNull
    @Override
    public SongsAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.songlist_layout, parent, false);
        return new SongViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapter.SongViewHolder songViewHolder, int position) {
        Song mCurrent = mSongList.get(position);
        //plug in artist and title here
        songViewHolder.titleView.setText(mCurrent.getTitle());
        //songViewHolder.artistView.setText(mCurrent.getArtist());
    }

    @Override
    public int getItemCount() {
        if (mSongList.isEmpty())
            return 0;
        return mSongList.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView titleView;
        //public final TextView artistView;
        final SongsAdapter songsAdapter;

        public SongViewHolder(View itemView, SongsAdapter adapter){
            super(itemView);
            titleView = itemView.findViewById(R.id.song_title_display);
            //artistView = itemView.findViewById(R.id.song_artist_display);
            this.songsAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //pass in the extra data to the new activity
            Song currentSong = mSongList.get(getAdapterPosition());
            //Log.d("ONCLICK", titleView.getText().toString());
            Intent songIntent = new Intent(mContext, SongPlayer.class);
            songIntent.putExtra("song_title_text", currentSong.getTitle());
            songIntent.putExtra("song_artist_text", currentSong.getArtist());
            songIntent.putExtra("song_file_path", currentSong.getFile_path());
            mContext.startActivity(songIntent);

        }
    }
}

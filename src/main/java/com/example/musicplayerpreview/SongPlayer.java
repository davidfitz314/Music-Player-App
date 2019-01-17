package com.example.musicplayerpreview;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class SongPlayer extends AppCompatActivity {
    private SeekBar positionBar;
    private TextView titleArtistTextView;
    private TextView currentSongTime;
    private TextView endSongTime;
    private MediaPlayer mediaPlayer;
    private int totalTime;
    private ImageView coverArt;
    private int mycounter = 0;
    //notification id
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    //notification builder
    private NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        positionBar = findViewById(R.id.current_playing_time_seekbar);
        titleArtistTextView = findViewById(R.id.song_details_text);
        currentSongTime = findViewById(R.id.current_playing_time_clock);
        endSongTime = findViewById(R.id.end_music_time);


        //temp title and artist
        String titleArtist = "\"" + getIntent().getStringExtra("song_title_text");// + "\" \n by " + getIntent().getStringExtra("song_artist_text");
        titleArtistTextView.setText(titleArtist);

        coverArt = findViewById(R.id.music_cover_view);

        //media player
        //MediaPlayer.create(this, R.raw.thecarpenterssolitaire);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(getIntent().getStringExtra("song_file_path")));
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
        mediaPlayer.setLooping(false);
        mediaPlayer.seekTo(0);
        totalTime = mediaPlayer.getDuration();

        //time position bar
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //thread to update progress and time? (change to asyncTask later?)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying() && mycounter > 1) {
                            coverArt.setImageResource(R.drawable.ic_music_icon_alt);
                            mycounter = 0;
                        } else {
                            coverArt.setImageResource(R.drawable.ic_music_icon);
                        }
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(500);
                        mycounter++;
                        if (mediaPlayer.isPlaying() && mycounter > 1) {
                            coverArt.setImageResource(R.drawable.ic_music_icon_alt);
                            mycounter = 0;
                        } else {
                            coverArt.setImageResource(R.drawable.ic_music_icon);
                        }
                        Thread.sleep(500);

                        mycounter++;
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();

        //notification creation
        createNotificationChannel();

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int currentPosition = msg.what;
            //update position bar
            positionBar.setProgress(currentPosition);
            //update labels
            String elapsedTime = createTimeLabel(currentPosition);
            currentSongTime.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            endSongTime.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) {
            timeLabel += "0" + sec;
        } else {
            timeLabel += sec;
        }
        return timeLabel;
    }

    public void pausePlayAction(View view) {

        ImageButton mPlayButton = (ImageButton) view.findViewById(R.id.start_stop_button);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mPlayButton.setImageResource(R.drawable.ic_pause_action);
            sendNotification();
        } else {
            mediaPlayer.pause();
            mPlayButton.setImageResource(R.drawable.ic_play_action);
        }
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Playing Now", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Music Player");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }

    }

    private NotificationCompat.Builder getNotificationBuilder(){
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Now Playing")
                .setContentText(getIntent().getStringExtra("song_title_text"))
                .setSmallIcon(R.drawable.ic_pause_action);
        return notifyBuilder;
    }

    public void sendNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    /*
    @Override
    protected void onStop() {
        mNotifyManager.cancelAll();
        super.onStop();
    }
    */

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        //destroy notification
        mNotifyManager.cancel(NOTIFICATION_ID);
        mNotifyManager.cancelAll();
        super.onDestroy();
    }
}

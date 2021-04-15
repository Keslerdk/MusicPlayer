package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ImageButton playBtn, nextBnt, previousBtn, fastForwardBtn, fastRewindBtn;
    TextView songName, start, stop;
    SeekBar seekMusic;
    BarVisualizer visualizer;

    String sname;
    private static final String EXTRA_NAME = "song name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playBtn = findViewById(R.id.playBtn);
        nextBnt = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);
        fastForwardBtn = findViewById(R.id.fastForwardBtn);
        fastRewindBtn = findViewById(R.id.fastRewindBtn);
        songName = findViewById(R.id.songName2);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        seekMusic = findViewById(R.id.seekBar);
        visualizer = findViewById(R.id.blast);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songname = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        songName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        songName.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playBtn.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                    playBtn.setBackgroundResource(R.drawable.ic_pause);
                }
            }
        });

    }
}
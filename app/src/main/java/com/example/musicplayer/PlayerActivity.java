package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    ImageView musicRecord;

    Thread updateSeekBar;

    String sname;
    private static final String EXTRA_NAME = "song name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (visualizer != null) {
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        musicRecord = findViewById(R.id.musicRecord);

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

        updateSeekBar = new Thread() {
            @Override
            public void run() {
               int totalDuration = mediaPlayer.getDuration();
               int currentPossition = 0;

               while (currentPossition < totalDuration) {
                   try {
                       sleep(500);
                       currentPossition = mediaPlayer.getCurrentPosition();
                       seekMusic.setProgress(currentPossition);
                   } catch (InterruptedException | IllegalStateException e) {
                       e.printStackTrace();
                   }
               }
            }
        };
        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        stop.setText(endTime);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                start.setText(currentTime);
                handler.postDelayed(this, 1000);
            }
        }, 1000);


        fastForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        fastRewindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

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

        //next listener
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextBnt.performClick();
            }
        });

        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) {
            visualizer.setAudioSessionId(audioSessionId);
        }

        nextBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position+1)%mySongs.size();
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName();
                songName.setText(sname);
                mediaPlayer.start();
                playBtn.setBackgroundResource(R.drawable.ic_pause);

                startAnimation(musicRecord);

                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1) {
                    visualizer.setAudioSessionId(audioSessionId);
                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position-1)<0? (mySongs.size()-1): (position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName();
                songName.setText(sname);
                mediaPlayer.start();
                playBtn.setBackgroundResource(R.drawable.ic_pause);

                startAnimation(musicRecord);

                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1) {
                    visualizer.setAudioSessionId(audioSessionId);
                }
            }
        });
    }

    public void startAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(musicRecord, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

    public String createTime(int duration) {
        String time ="";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";

        if (sec< 10) {
            time+="0";
        }
        time +=sec;

        return time;
    }
}
package com.csit.smusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class PlayMusic extends AppCompatActivity {
    private TextView textView;
    private ImageView play ,previous,next;
    private SeekBar seekBar;
     ArrayList<File> mySongs;
    int position;
    MediaPlayer mediaPlayer;
    Thread updateSeekbar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeekbar.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        textView =findViewById(R.id.textView);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        seekBar=findViewById(R.id.seekBar);

        textView.setSelected(true);


        Intent intent=getIntent();
        String currsong=intent.getStringExtra("currentSong");
        Bundle bundle=intent.getExtras();
        mySongs=(ArrayList) bundle.getParcelableArrayList("songList");
         position=intent.getIntExtra("position",0);
         textView.setText(currsong);
        Uri uri= Uri.parse(mySongs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        mediaPlayer.start();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }
                else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);

                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position>0){
                    position--;
                }
                else {
                    position=mySongs.size()-1;
                }
                play.setImageResource(R.drawable.pause);
                textView.setText(mySongs.get(position).getName().replace(".mp3","").replace(".mp4",""));
               Uri uri= Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position<mySongs.size()-1){
                    position++;
                }
                else {
                    position=0;
                }
                play.setImageResource(R.drawable.pause);
                textView.setText(mySongs.get(position).getName().replace(".mp3","").replace(".mp4",""));
                Uri uri= Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
            }
        });

        //seekbar
    seekBar.setMax(mediaPlayer.getDuration());
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
    updateSeekbar=new Thread(){
        @Override
        public void run() {
            int currentPosition=mediaPlayer.getCurrentPosition();
            try {
                while (currentPosition < mediaPlayer.getDuration()) {
                    currentPosition=mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    sleep(800);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    updateSeekbar.start();
    }
}
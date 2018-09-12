package com.example.rgc.sounds;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView text_music, text_sound;
    Button btn_music_start, btn_music_stop, btn_sound_start, btn_sound_stop;
    MediaPlayer mediaPlayer;
    SoundPool soundPool;
    int preload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_music = (TextView) findViewById(R.id.tv_music);
        text_sound = (TextView) findViewById(R.id.tv_sound);

        btn_music_start = (Button) findViewById(R.id.btnms);
        btn_music_stop = (Button) findViewById(R.id.btnmc);
        btn_sound_start = (Button) findViewById(R.id.btnss);
        btn_sound_stop = (Button) findViewById(R.id.btnsc);

        btn_music_start.setOnClickListener(this);
        btn_music_stop.setOnClickListener(this);
        btn_sound_start.setOnClickListener(this);
        btn_sound_stop.setOnClickListener(this);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Sound Pool
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        preload = soundPool.load(this, R.raw.just_like_magic,1);


    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.btnms:
                mediaPlayer = MediaPlayer.create(this, R.raw.burbujas);
                mediaPlayer.start();
                text_music.setText(getString(R.string.music_start));
                break;
            case R.id.btnmc:
                mediaPlayer.stop();
                text_music.setText(getString(R.string.music_stop));
                break;
            case R.id.btnss:
                soundPool.play(preload, 1f, 1f, 0, -1, 1);
                text_sound.setText(getString(R.string.sound_start));
                break;
            case R.id.btnsc:
                soundPool.stop(preload);
                text_sound.setText(getString(R.string.sound_stop));
                break;
            default:
                break;
        }


    }
}

package com.surroundsync.gpsnow.Splash;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.surroundsync.gpsnow.R;
import com.surroundsync.gpsnow.startapp.StartActivity;

public class Splash extends Activity {
    private ImageView ivlogo;
    private Animation anima;
    private MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivlogo=(ImageView)findViewById(R.id.activity_splash_iv_logo);
        anima= AnimationUtils.loadAnimation(getBaseContext(),R.anim.aninations);

        ivlogo.startAnimation(anima);
        anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.soundmainlogo);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMediaPlayer.stop();
                finish();
                Intent i = new Intent(getBaseContext(),StartActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}

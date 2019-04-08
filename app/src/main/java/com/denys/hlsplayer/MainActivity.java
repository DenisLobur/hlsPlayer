package com.denys.hlsplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView progressLabel;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    private ImageButton playPauseButton;
    private PlayerState currentState;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressLabel = findViewById(R.id.progressLabel);
        progressBar = findViewById(R.id.progressBar);
        playPauseButton = findViewById(R.id.playPauseButton);

        currentState = PlayerState.UNINITIALIZED;
        changePlayerState(currentState);

        mediaPlayer = MediaPlayer.create(this, R.raw.audio_sample);

        mediaPlayer.setOnCompletionListener(mp -> {
            pStatus = 0;
            currentState = PlayerState.COMPLITED;
            changePlayerState(currentState);
        });

        playPauseButton.setOnClickListener(v -> {
            if (currentState == PlayerState.UNINITIALIZED || currentState == PlayerState.COMPLITED) {
                currentState = PlayerState.FETCHING;
                progressBar.setVisibility(View.VISIBLE);
                fetchAudio().start();
            } else if (currentState == PlayerState.PLAYING) {
                currentState = PlayerState.PAUSED;
            } else {
                currentState = PlayerState.PLAYING;
            }
            playMusic(currentState);
            changePlayerState(currentState);
        });


    }

    private void changePlayerState(PlayerState state) {
        switch (state) {
            case UNINITIALIZED:
                playPauseButton.setImageResource(R.drawable.round_play);
                progressLabel.setText(R.string.current_state_uninitialized);
                break;
            case FETCHING:
                playPauseButton.setVisibility(View.INVISIBLE);
                progressLabel.setText(R.string.current_state_fetching);
                break;
            case PLAYING:
                playPauseButton.setImageResource(R.drawable.round_pause);
                progressLabel.setText(R.string.current_state_playing);
                break;
            case PAUSED:
                playPauseButton.setImageResource(R.drawable.round_play);
                progressLabel.setText(R.string.current_state_paused);
                break;
            case COMPLITED:
                playPauseButton.setImageResource(R.drawable.round_play);
                progressLabel.setText(R.string.current_state_completed);
            default:
                //
                break;
        }
    }

    private void playMusic(PlayerState state) {
        if (state == PlayerState.PLAYING) {
            mediaPlayer.start();
        } else if(state == PlayerState.PAUSED) {
            mediaPlayer.pause();
        }
    }

    private Thread fetchAudio(){
        Thread fetchingThread = new Thread(() -> {
            while (pStatus <= 100) {
                handler.post(() -> {
                    progressBar.setProgress(pStatus);
                    String txt = String.format(getString(R.string.current_state_fetching), pStatus);
                    progressLabel.setText(txt);
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pStatus++;
            }

            handler.post(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                playPauseButton.setVisibility(View.VISIBLE);
                currentState = PlayerState.PLAYING;
                changePlayerState(currentState);
                playMusic(currentState);
            });
        });

        return fetchingThread;
    }
}

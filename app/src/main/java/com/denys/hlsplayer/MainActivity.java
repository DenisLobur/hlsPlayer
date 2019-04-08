package com.denys.hlsplayer;

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
    private Thread fetchingThread;
    private PlayerState currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressLabel = findViewById(R.id.progressLabel);
        progressBar = findViewById(R.id.progressBar);
        playPauseButton = findViewById(R.id.playPauseButton);

        currentState = PlayerState.UNINITIALIZED;
        changePlayerState(currentState);
        playPauseButton.setOnClickListener(v -> {
            if (currentState == PlayerState.UNINITIALIZED) {
                currentState = PlayerState.FETCHING;
                fetchingThread.start();
            } else if (currentState == PlayerState.PLAYING) {
                currentState = PlayerState.PAUSED;
            } else {
                currentState = PlayerState.PLAYING;
            }
            changePlayerState(currentState);
        });

        fetchingThread = new Thread(() -> {
            while (pStatus <= 100) {
                handler.post(() -> {
                    progressBar.setProgress(pStatus);
                    progressLabel.setText(pStatus + " %");
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pStatus++;
            }

            handler.post(() -> {
                currentState = PlayerState.PLAYING;
                changePlayerState(currentState);
            });
        });
    }

    private void changePlayerState(PlayerState state) {
        switch (state) {
            case UNINITIALIZED:
                playPauseButton.setImageResource(R.drawable.round_play);
                progressLabel.setText(R.string.current_state_uninitialized);
                break;
            case FETCHING:
                progressLabel.setText(R.string.current_state_fetching);
                //progressLabel
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
                //playButton
                progressLabel.setText(R.string.current_state_completed);
            default:
                //fetching, completed
                break;
        }
    }
}

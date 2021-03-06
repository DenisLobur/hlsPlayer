package com.denys.hlsplayer.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denys.hlsplayer.R;
import com.denys.hlsplayer.presenter.MainPresenter;
import com.denys.hlsplayer.util.PlayerState;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, MainPresenter.MainView {
  private static final String TAG = "CUSTOM_VIEW";

  private static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

  private TextView progressLabel;
  private ProgressBar progressBar;
  private int pStatus = 0;
  private Handler handler;
  private ImageButton playPauseButton;
  private PlayerState currentState;
  private MediaPlayer mediaPlayer;

  private PlayerView playerView;
  private float xDelta;
  private float yDelta;
  private int windowWidth;
  private int windowHeight;
  private Animation animation;
  private MainPresenter presenter;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int msgValue = msg.what;
        if (msgValue > 92){
          msgValue = 100;
        }
        progressBar.setProgress(msgValue);
        String txt = String.format(getString(R.string.current_state_fetching), msgValue);
        progressLabel.setText(txt);
        if (msgValue > 99){
          currentState = PlayerState.PLAYING;
          changePlayerState(currentState);
        }
      }
    };
    presenter = new MainPresenter(this, handler);

    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    windowWidth = size.x;
    windowHeight = size.y;

    playerView = findViewById(R.id.player_view);

    playerView.setOnTouchListener(this);

    animation = AnimationUtils.loadAnimation(this, R.anim.accel_deaccel_anim);

    verifyStoragePermissions(this);

    progressLabel = findViewById(R.id.progressLabel);
    progressBar = findViewById(R.id.progressBar);
    playPauseButton = findViewById(R.id.playPauseButton);

    currentState = PlayerState.UNINITIALIZED;
    changePlayerState(currentState);

    mediaPlayer = MediaPlayer.create(this, R.raw.audio_sample);

    mediaPlayer.setOnCompletionListener(mp -> {
      currentState = PlayerState.COMPLITED;
      changePlayerState(currentState);
    });

    playPauseButton.setOnClickListener(v -> {
      if (currentState == PlayerState.UNINITIALIZED || currentState == PlayerState.COMPLITED) {
        currentState = PlayerState.FETCHING;
        progressBar.setVisibility(View.VISIBLE);
        presenter.fetchMainPlaylist();
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
        break;
      case PLAYING:
        playPauseButton.setImageResource(R.drawable.round_pause);
        playPauseButton.setVisibility(View.VISIBLE);
        progressLabel.setText(R.string.current_state_playing);
        progressBar.setVisibility(View.INVISIBLE);
        playMusic(currentState);
        break;
      case PAUSED:
        playPauseButton.setImageResource(R.drawable.round_play);
        progressLabel.setText(R.string.current_state_paused);
        break;
      case COMPLITED:
        playPauseButton.setImageResource(R.drawable.round_play);
        progressLabel.setText(R.string.current_state_completed);
        presenter.deleteAllCachedFiles();
      default:
        //
        break;
    }
  }

  private void playMusic(PlayerState state) {
    if (state == PlayerState.PLAYING) {
      mediaPlayer.start();
    } else if (state == PlayerState.PAUSED) {
      mediaPlayer.pause();
    }
  }

  private static void verifyStoragePermissions(Activity activity) {
    // Check if we have write permission
    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      // We don't have permission so prompt the user
      ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    float newX;
    float newY;
    int lastAction;

    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        xDelta = playerView.getX() - event.getRawX();
        yDelta = playerView.getY() - event.getRawY();
        lastAction = MotionEvent.ACTION_DOWN;
        break;
      case MotionEvent.ACTION_UP:
        break;
      case MotionEvent.ACTION_MOVE:
        // anim here
        playerView.setAnimation(animation);

        newX = event.getRawX() + xDelta;
        newY = event.getRawY() + yDelta;

        if ((newX <= 0 || newX >= windowWidth - playerView.getWidth()) || (newY <= 0 || newY >= windowHeight - playerView.getHeight())) {
          lastAction = MotionEvent.ACTION_MOVE;
          break;
        }

        playerView.setX(newX);
        playerView.setY(newY);
        lastAction = MotionEvent.ACTION_MOVE;

        break;

    }

    return true;

  }

  @Override
  public void updateUI() {
    playPauseButton.setVisibility(View.VISIBLE);
    progressLabel.setVisibility(View.INVISIBLE);
  }
}

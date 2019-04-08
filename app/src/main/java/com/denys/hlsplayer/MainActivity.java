package com.denys.hlsplayer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private TextView progressLabel;
  private ProgressBar progressBar;
  private int pStatus = 0;
  private Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressLabel = findViewById(R.id.progressLabel);
    progressBar = findViewById(R.id.progressBar);

    new Thread(new Runnable() {
      @Override
      public void run() {
        while (pStatus <= 100) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              progressBar.setProgress(pStatus);
              progressLabel.setText(pStatus + " %");
            }
          });
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          pStatus++;
        }
      }
    }).start();

  }
}

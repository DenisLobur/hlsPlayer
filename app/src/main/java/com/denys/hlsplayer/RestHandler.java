package com.denys.hlsplayer;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RestHandler {
  private static final String TAG = "HTTP_HANDLER";
  private static final String BASE_URL = "http://pubcache1.arkiva.de/test/";

  public RestHandler() {

    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

//    for (int i = 0; i < 10; i++) {
    Task task = new Task("Task: ");

    threadPoolExecutor.execute(task);
//    }

  }

  private static class Task implements Runnable {
    private String name;

    public Task(String name) {
      this.name = name;
    }

    @Override
    public void run() {
//      try {
//        long duration = (long) (Math.random() * 100);
//        Log.d(TAG, "Executing: " + name + " on thread: " + Thread.currentThread().getName());
//
//        TimeUnit.MILLISECONDS.sleep(duration);
//      } catch (InterruptedException ie) {
//        Log.d(TAG, ie.getMessage());
//      }
      try {
        Log.d(TAG, "Executing: " + name + " on thread: " + Thread.currentThread().getName());
        makeHttpRequest("", "");
      } catch (IOException e) {
        Log.d(TAG, e.getMessage());
      }
    }

    private String makeHttpRequest(String uri, String range) throws IOException {
      BufferedReader reader = null;
      URL url = null;
      HttpURLConnection urlConnection = null;
      try {
        url = new URL(BASE_URL + uri);
      } catch (MalformedURLException e) {
        Log.d(TAG, e.getMessage());
      }
      try {
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        if (range != null) {
          urlConnection.setRequestProperty("Range:", "bytes=" + range);
        }
        urlConnection.connect();
        reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder buf = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          Log.d(TAG, line + "\n");
          buf.append(line + "\n");
        }

        return (buf.toString());
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
      }
    }
  }
}

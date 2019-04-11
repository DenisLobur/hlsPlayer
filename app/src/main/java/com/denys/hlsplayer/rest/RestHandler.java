package com.denys.hlsplayer.rest;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.denys.hlsplayer.parser.PlayListParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class RestHandler {
  private static final String TAG = "HTTP_HANDLER";
  private static final String BASE_URL = "http://pubcache1.arkiva.de/test/";
  private static final String MAIN_PLAYLIST_URI = "hls_index.m3u8";
  private static final int THREADS = 2;

  private PlayListParser playListParser;
  private ThreadPoolExecutor threadPoolExecutor;

  public RestHandler() {
    playListParser = new PlayListParser();
    threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS);
  }

  public Future<StringBuilder> getPlayList(String name) throws Exception {
    FetchPlayListTask task = new FetchPlayListTask(name, MAIN_PLAYLIST_URI);
    return threadPoolExecutor.submit(task);
  }

  public Future<StringBuilder> getAudioFile(String name, String uri) throws Exception {
    FetchPlayListTask task = new FetchPlayListTask(name, uri);
    return threadPoolExecutor.submit(task);
  }

  public void getAudioChunk(String audioFileEndpoint, String range, Handler handler, double progress) {
      Task task = new Task("Task: " + range, audioFileEndpoint, range, handler, progress);
      threadPoolExecutor.execute(task);
      if (progress > 95) {
        threadPoolExecutor.shutdown();
      }
  }

  private class Task implements Runnable {
    private String name;
    private String uri;
    private String range;
    private Handler handler;
    private double progress;

    public Task(String name, String uri, String range, Handler handler, double progress) {
      this.name = name;
      this.uri = uri;
      this.range = range;
      this.handler = handler;
      this.progress = progress;
    }

    @Override
    public void run() {
      try {
        Log.d(TAG, "Executing: " + name + " on thread: " + Thread.currentThread().getName());
        makeAsyncHttpRequest(uri, range);
        Message message = new Message();
        message.what = (int)progress;
        Log.d(TAG, "percent: "+progress);
        handler.sendMessage(message);
      } catch (IOException e) {
        Log.d(TAG, e.getMessage());
      }
    }
  }

  private class FetchPlayListTask implements Callable<StringBuilder> {
    private String name;
    private String uri;
    private String range;

    public FetchPlayListTask(String name, String uri) {
      this.name = name;
      this.uri = uri;
    }

    FetchPlayListTask(String name, String uri, String range) {
      this.name = name;
      this.uri = uri;
      this.range = range;
    }

    @Override
    public StringBuilder call() throws Exception {
      Log.d(TAG, "Executing callable: " + name + " on thread: " + Thread.currentThread().getName());
      StringBuilder sb = range == null ? makeHttpRequest(uri) : makeHttpRequest(uri, range);

      return sb;
    }
  }

  private StringBuilder makeHttpRequest(String uri) throws IOException {
    return makeHttpRequest(uri, null);
  }

  private StringBuilder makeHttpRequest(String uri, String range) throws IOException {
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
      if (!TextUtils.isEmpty(range)) {
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

      return buf;
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
  }

  private synchronized void makeAsyncHttpRequest(String uri, String range) throws IOException {
    String requestRange = "bytes=" + range;
    Log.d(TAG, "Fetching range: " + range);
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
      urlConnection.setRequestProperty("Accept-Encoding", "");
      urlConnection.setRequestProperty("Range", requestRange);
      urlConnection.connect();
      playListParser.writeStreamToFile(urlConnection.getInputStream());
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
  }
}

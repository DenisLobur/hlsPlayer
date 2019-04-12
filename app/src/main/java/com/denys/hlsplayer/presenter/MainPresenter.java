package com.denys.hlsplayer.presenter;

import android.os.Handler;
import android.util.Log;

import com.denys.hlsplayer.model.AudioFileModel;
import com.denys.hlsplayer.model.MainPlaylistModel;
import com.denys.hlsplayer.parser.PlayListParser;
import com.denys.hlsplayer.rest.RestHandler;
import com.denys.hlsplayer.util.CompareAudioQuality;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class MainPresenter {
  private static final String TAG = "PRESENTER";

  private MainView mainView;
  private RestHandler restHandler;
  private PlayListParser playListParser;
  private Future<StringBuilder> mainPlayListFurure;
  private List<MainPlaylistModel> audioList = Collections.emptyList();
  private List<AudioFileModel> rangeList = Collections.emptyList();
  private Handler handler;

  public MainPresenter(MainView mainView, Handler handler) {
    this.mainView = mainView;
    restHandler = new RestHandler();
    playListParser = new PlayListParser();
    this.handler = handler;
  }

  public void fetchMainPlaylist() {
    try {
      mainPlayListFurure = restHandler.getPlayList("Fetch main playlist");
      while (!mainPlayListFurure.isDone()) {
        savePlayListToFile(mainPlayListFurure.get(), "main_playlist.txt");
      }
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }

  private void fetchAudioFile(String audioFileName) {
    try {
      mainPlayListFurure = restHandler.getAudioFile("Fetch audio file", audioFileName);
      while (!mainPlayListFurure.isDone()) {
        saveAudioPlayListToFile(mainPlayListFurure.get(), "audio_to_parse.txt");
      }
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }

  private void savePlayListToFile(StringBuilder sb, String fileName) {
    try {
      playListParser.writeToFile(sb, fileName);
      audioList = playListParser.parseMainPlayList(fileName);
      String audioFileURI = CompareAudioQuality.compareAudioFiles(audioList);
      Log.d(TAG, "audio_uri: " + audioFileURI);
      fetchAudioFile(audioFileURI);
    } catch (IOException e) {
      Log.d(TAG, e.getMessage());
    }
  }

  private void saveAudioPlayListToFile(StringBuilder sb, String fileName){
    try {
      Log.d(TAG, "audio_play_list: " + sb.toString());
      playListParser.writeToFile(sb, fileName);
      rangeList = playListParser.parseAudioPlayList(fileName);
      fetchAudioChunks(playListParser.getAudioFileEndpoint(), rangeList);
    } catch (IOException e) {
      Log.d(TAG, e.getMessage());
    }
  }

  public void fetchAudioChunks(String audioFileEndpoint, List<AudioFileModel> rangeList) {
    Log.d(TAG, "audioFileEndpoint: " + audioFileEndpoint);
    double percent = 100.0/rangeList.size();
    for(int i = 0; i < rangeList.size(); i++) {
      try {
        long start = System.currentTimeMillis();
        restHandler.getAudioChunk(audioFileEndpoint, rangeList.get(i).getRange(), handler, i*percent);
        long end = System.currentTimeMillis();
        Log.d("elapsed", "millis: "+(end - start));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void deleteAllCachedFiles() {
    File f1 = new File("/storage/emulated/0/Android/data/raw_media.txt");
    File f2 = new File("/storage/emulated/0/Android/data/main_playlist.txt");
    File f3 = new File("/storage/emulated/0/Android/data/audio_to_parse.txt");
    if(f1.delete() && f2.delete() && f3.delete()){
      Log.d(TAG, "deleted");
    }
  }

  public interface MainView {
    void updateUI();
  }
}

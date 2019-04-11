package com.denys.hlsplayer.parser;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.denys.hlsplayer.model.AudioFileModel;
import com.denys.hlsplayer.model.MainPlaylistModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayListParser {
  private static final String TAG = "HLS_PARSER";
  private String audioFileEndPoint = "";

  public PlayListParser() {

  }

  @SuppressLint("NewApi")
  public void writeToFile(StringBuilder sb, String fileName) throws IOException {
    Log.d(TAG, "writing to file");
    Log.d(TAG, "dirs: " + Environment.getExternalStorageDirectory().toPath());
    BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("/storage/emulated/0/Android/data/" + fileName)));

    //write contents of StringBuffer to a file
    bwr.write(sb.toString());

    //flush the stream
    bwr.flush();

    //close the stream
    bwr.close();
  }

  public synchronized void writeStreamToFile(InputStream inputStream) throws Exception{
    Log.d(TAG, "here: +" + Thread.currentThread().getName());
    try {
      File file = new File("/storage/emulated/0/Android/data/raw_media.txt");
      OutputStream output = new FileOutputStream(file, true);
      try {
        byte[] buffer = new byte[4 * 1024]; // or other buffer size
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
          output.write(buffer, 0, read);
        }

        output.flush();
      } finally {
        output.close();
      }
    } finally {
      inputStream.close();
    }
  }

  @SuppressLint("NewApi")
  public List<String> readFromFile(String fileName) {
    List<String> lines = Collections.emptyList();
    try {
      lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lines;
  }

  public List<MainPlaylistModel> parseMainPlayList(String fileName) {
    List<String> lines = readFromFile("/storage/emulated/0/Android/data/" + fileName);
    List<MainPlaylistModel> audioFiles = new ArrayList<>();

    for (String line : lines) {
      if (line.contains(Tags.TYPE)) {
        Log.d(TAG, "line: " + line);
        String[] rawData = parseMainListLine(line);
        audioFiles.add(new MainPlaylistModel(rawData[0], rawData[1]));
      }
    }

    return audioFiles;
  }

  public List<AudioFileModel> parseAudioPlayList(String fileName) {
    List<String> lines = readFromFile("/storage/emulated/0/Android/data/" + fileName);
    List<AudioFileModel> audioFileRanges = new ArrayList<>();

    for (String line : lines) {
      if (line.contains(Tags.BYTERANGE)) {
        Log.d(TAG, "line: " + line);
        String range = parseAudioRangeLine(line);
        audioFileRanges.add(new AudioFileModel(range));
      }
      if(line.contains(".ts")){
        audioFileEndPoint = line;
      }
    }

    return audioFileRanges;
  }

  public String getAudioFileEndpoint(){
    return audioFileEndPoint;
  }

  private String[] parseMainListLine(String line) {
    String[] parsedRes = new String[2];
    String chunks[] = line.split(",");
    for (String chunk : chunks) {
      if (chunk.startsWith(Tags.GROUP_ID)) {
        parsedRes[0] = parseStringChunk(chunk);
      }
      if (chunk.startsWith(Tags.URI)) {
        parsedRes[1] = parseStringChunk(chunk);
      }
    }

    return parsedRes;
  }

  private String parseAudioRangeLine(String line) {
    String range = "";
    String chunks[] = line.split(":");
    for(String chunk: chunks){
      if(chunk.contains("@")){
        range = chunk.replace("@", "-");
      }
    }

    return range;
  }

  private String parseStringChunk(String chunk) {
    Log.d(TAG, "chunk before: " + chunk);
    chunk = chunk.substring(chunk.indexOf("\"") + 1);
    chunk = chunk.substring(0, chunk.indexOf("\""));
    Log.d(TAG, "chunk after: " + chunk);
    return chunk;
  }
}

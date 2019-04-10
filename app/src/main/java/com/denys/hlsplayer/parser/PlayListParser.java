package com.denys.hlsplayer.parser;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class PlayListParser {
  private static final String TAG = "HLS_PARSER";

  public PlayListParser(){

  }

  public void writeToFile(StringBuilder sb) throws IOException {
    Log.d(TAG, "writing to file");
    Log.d(TAG, "dirs: " + Environment.getExternalStorageDirectory().toPath());
    BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("/storage/emulated/0/Android/data/main_playlist.txt")));

    //write contents of StringBuffer to a file
    bwr.write(sb.toString());

    //flush the stream
    bwr.flush();

    //close the stream
    bwr.close();
  }

  public List<String> readFromFile(String fileName){
    List<String> lines = Collections.emptyList();
    try
    {
      lines =
              Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
    }

    catch (IOException e)
    {

      // do something
      e.printStackTrace();
    }
    return lines;
  }
}

package com.denys.hlsplayer.util;

import com.denys.hlsplayer.model.MainPlaylistModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompareAudioQuality {
  public static String compareAudioFiles(List<MainPlaylistModel> audios) {
    Collections.sort(audios, new QualityComparator());
    return audios.get(audios.size() - 1).getUri();
  }

  private static class QualityComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
      MainPlaylistModel audio1 = (MainPlaylistModel) o1;
      MainPlaylistModel audio2 = (MainPlaylistModel) o2;
      int q1 = Integer.parseInt(audio1.getUri().replaceAll("\\D+", ""));
      int q2 = Integer.parseInt(audio2.getUri().replaceAll("\\D+", ""));

      if (q1 == q2) {
        return 0;
      } else if (q1 > q2) {
        return 1;
      } else {
        return -1;
      }
    }
  }
}

package com.denys.hlsplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.denys.hlsplayer.R;

public class PlayerView extends RelativeLayout {

  public PlayerView(Context context) {
    super(context);
    init(context);
  }

  public PlayerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.view_player, this);
  }


}

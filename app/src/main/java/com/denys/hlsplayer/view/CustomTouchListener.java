package com.denys.hlsplayer.view;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

//TODO: works with Fling gesture
public class CustomTouchListener implements View.OnTouchListener {
  private static final String TAG = "SWIPE";
  private static final int MIN_DISTANCE_MOVED = 50;
  private static final float MIN_TRANSLATION = 0;
  private static final float FRICTION = 1.1f;


  private final GestureDetector gestureDetector;
  private View view;

  public CustomTouchListener(Context ctx, View view) {
    gestureDetector = new GestureDetector(ctx, new MyGestureListener());
    this.view = view;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    return gestureDetector.onTouchEvent(event);
  }

  private final class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 10;

    //    mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//      @Override
//      public void onGlobalLayout() {
//        maxTranslationX = mainLayout.getWidth() - playerView.getWidth();
//        maxTranslationY = mainLayout.getHeight() - playerView.getHeight();
//        //As only wanted the first call back, so now remove the listener
//        mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//      }
//    });

    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
      Log.d(TAG, "Fling!");
      //downEvent : when user puts his finger down on the view
      //moveEvent : when user lifts his finger at the end of the movement
      float distanceInX = Math.abs(moveEvent.getRawX() - downEvent.getRawX());
      float distanceInY = Math.abs(moveEvent.getRawY() - downEvent.getRawY());

      if (distanceInX > MIN_DISTANCE_MOVED) {
        //Fling Right/Left
        FlingAnimation flingX = new FlingAnimation(view, DynamicAnimation.TRANSLATION_X);
        flingX.setStartVelocity(velocityX).setMinValue(MIN_TRANSLATION) // minimum translationX property
                //.setMaxValue(maxTranslationX)  // maximum translationX property
                .setFriction(FRICTION).start();
      } else if (distanceInY > MIN_DISTANCE_MOVED) {
        //Fling Down/Up
        FlingAnimation flingY = new FlingAnimation(view, DynamicAnimation.TRANSLATION_Y);
        flingY.setStartVelocity(velocityY).setMinValue(MIN_TRANSLATION)  // minimum translationY property
                //.setMaxValue(maxTranslationY) // maximum translationY property
                .setFriction(FRICTION).start();
      }

      return true;
    }

    public void onSwipeRight(View view, float x) {
      //Log.d(TAG, "swipe right");
      view.setX(x);
    }

    public void onSwipeLeft(View view, float x) {
      //Log.d(TAG, "swipe left");
      view.setX(x);
    }

    public void onSwipeTop(View view, float y) {
      //Log.d(TAG, "swipe top");
      view.setY(y);
    }

    public void onSwipeBottom(View view, float y) {
      //Log.d(TAG, "swipe bottom");
      view.setY(y);
    }
  }
}

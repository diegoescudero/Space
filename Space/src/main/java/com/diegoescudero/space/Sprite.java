package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Sprite {
    private Bitmap currentFrame;
    private Context context;

    public Sprite(Context context) {
        this.context = context;
        currentFrame = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship);
    }

    public Bitmap getBitmap() {
        return currentFrame;
    }
}

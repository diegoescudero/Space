package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public abstract class Sprite {
    public Bitmap currentFrame;
    private Context context;
    private boolean hasDeathAnimation;
    private Rect location;

    public Sprite(Context context, boolean deathAnimation) {
        this.context = context;
        hasDeathAnimation = deathAnimation;
    }

    public boolean hasDeathAnimation() {
        return hasDeathAnimation;
    }

    public Bitmap getBitmap() {
        return currentFrame;
    }

    public void setLocation(Rect loc) {
        location = loc;
    }

    public Rect getLocation() {
        return location;
    }
}

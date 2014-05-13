package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.HashMap;

public class Sprite {
    private Context context;
    private Bitmap bitmap;
    private SpriteType type;
    private int frameWidth;
    private int frameHeight;

    private HashMap<Animation, Integer> bitmapRow = new HashMap<Animation, Integer>();

    private Bitmap currentFrame;
    private Animation currentAnimation;
    private int currentFramePosition;

    public Sprite(Context context, SpriteType type) {
        this.context = context;
        this.type = type;
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), type.getResource());
        this.frameWidth = bitmap.getWidth() / type.getWCount();
        this.frameHeight = bitmap.getHeight() / type.getHCount();

        showAnimationFrame(Animation.TILT_LEFT, 0);
    }

    public void showAnimationFrame(Animation animation, int frame) {
        int x = frame * frameWidth;
        int y = 0;

        currentFrame = Bitmap.createBitmap(bitmap, x, y, frameWidth, frameHeight);
    }

    public void startAnimation(Animation animation) {

    }

    public void advance() {
        if (currentFramePosition < type.getWCount()) {

        }
    }

    public Bitmap getBitmap() {
        return currentFrame;
    }

}

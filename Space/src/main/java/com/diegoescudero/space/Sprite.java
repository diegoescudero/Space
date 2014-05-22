package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;

public class Sprite {
    private Context context;
    private Bitmap bitmap;
    private SpriteType type;
    private int frameWidth;
    private int frameHeight;
    private int spriteWidth;
    private int spriteHeight;

    private HashMap<Animation, Integer> animationRow = new HashMap<Animation, Integer>();

    private Rect currentFrame;
    private Animation currentAnimation;
    private int currentFramePosition;

    public Sprite(Context context, SpriteType type, int spriteWidth, int spriteHeight) {
        this.context = context;
        this.type = type;

        //Decode image and set to right size
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), type.getResource());
        this.bitmap = Bitmap.createScaledBitmap(temp, spriteWidth * type.getWCount(), spriteHeight * type.getHCount(), true);
        temp.recycle();

        //Calculate sizes
        this.frameWidth = bitmap.getWidth() / type.getWCount();
        this.frameHeight = bitmap.getHeight() / type.getHCount();
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;

        //Create list of available animations in sprite sheet order
        ArrayList<Animation> allAnimations = new ArrayList<Animation>(Animation.getAllAnimations());
        int i = 0;
        for (Animation a : allAnimations) {
            if (type.containsAnimation(a)) {
                animationRow.put(a, i);
                i++;
            }
        }

        //Set default frame
        currentFrame = new Rect(0, 0, frameWidth, frameHeight);
    }

    public void showAnimationFrame(Animation animation, int frame) {
        int x = frame * frameWidth;
        int y = animationRow.get(animation) * frameHeight;

        currentFrame.left = x;
        currentFrame.top = y;
        currentFrame.right = x + frameWidth;
        currentFrame.bottom = y + frameHeight;
    }

    public boolean pixelFilled(int x, int y) {
        if (bitmap.getPixel(x + currentFrame.left, y + currentFrame.top) != Color.TRANSPARENT) {
            return true;
        }

        return false;
    }

    public void startAnimation(Animation animation) {

    }

    public void advance() {
        if (currentFramePosition < type.getWCount()) {

        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getFrameRect() {
        return currentFrame;
    }

    public SpriteType getSpriteType() {
        return type;
    }

}

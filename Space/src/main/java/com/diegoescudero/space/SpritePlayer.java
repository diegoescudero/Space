package com.diegoescudero.space;

import android.content.Context;
import android.graphics.BitmapFactory;

public class SpritePlayer extends Sprite {

    public SpritePlayer(Context context) {
        super(context, true);
        currentFrame = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceship);
    }
}

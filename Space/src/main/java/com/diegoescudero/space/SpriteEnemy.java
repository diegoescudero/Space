package com.diegoescudero.space;

import android.content.Context;
import android.graphics.BitmapFactory;

public class SpriteEnemy extends Sprite {

    public SpriteEnemy(Context context) {
        super(context, true);
        currentFrame = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
    }
}

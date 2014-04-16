package com.diegoescudero.space;

import android.content.Context;
import android.graphics.BitmapFactory;

public class SpriteProjectile extends Sprite {

    public SpriteProjectile(Context context) {
        super(context, false);
        currentFrame = BitmapFactory.decodeResource(context.getResources(), R.drawable.energy);
    }
}

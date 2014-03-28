package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class GameModel {
    Context context;
    Sprite ship;

    public GameModel(Context context) {
        this.context = context;

        ship = new Sprite(context);
    }

    public void update() {
        //do nothing for now
    }

    public void drawToCanvas(Canvas c) {
        if (c != null) {
            int width = c.getWidth()/2;
            int height = c.getHeight()/2;

            Bitmap b = ship.getBitmap();
            c.drawBitmap(b, null, new Rect(0, 0, width, height), null);
        }
    }
}

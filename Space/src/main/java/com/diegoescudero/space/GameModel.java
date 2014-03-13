package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

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

    public Canvas drawToCanvas(Canvas c) {
        if (c != null) {
            int width = c.getWidth();
            int height = c.getHeight();

            c.drawBitmap(ship.getBitmap(), new Rect(0, 0, 10, 10), new Rect(0, 0, 10, 10), null);
        }

        return c;
    }
}

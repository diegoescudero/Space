package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameView extends SurfaceView {
    GameThread thread;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (canvas != null) {
//            int red = (new Random()).nextInt(255);
//            int green = (new Random()).nextInt(255);
//            int blue = (new Random()).nextInt(255);
//
//            canvas.drawRGB(red, green, blue);
//        }
//    }
}

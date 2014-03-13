package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    GameThread thread;

    public GameView(Context context) {
        super(context);
        setup();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        getHolder().addCallback(this);
        thread = new GameThread(getHolder());
        setFocusable(true);
    }

    public void surfaceCreated(SurfaceHolder holder) {
//        Canvas canvas = holder.lockCanvas();
//        draw(canvas);
//        holder.unlockCanvasAndPost(canvas);

        thread.setRunning(true);
        thread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //nothing
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e) {
                //try again
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRGB(255, 255, 0);
//        canvas.drawBitmap();
    }

}

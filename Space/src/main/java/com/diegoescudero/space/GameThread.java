package com.diegoescudero.space;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private boolean running;
    private final int FPS = 30;

    private SurfaceHolder surfaceHolder;

    public GameThread(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {
        Canvas canvas = null;

        while (running) {
            //Update
            //Draw

        }
    }

    public void setRunning(boolean r) {
        running = r;
    }
}

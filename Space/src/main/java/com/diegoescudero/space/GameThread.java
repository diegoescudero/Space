package com.diegoescudero.space;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Random;

public class GameThread extends Thread {
    private boolean running;
    private final long FPS = 30;
    private final long FRAME_TIME = 1000 / FPS;

    private GameModel gameModel;
    private GameView gameView;

    public GameThread(GameModel model, GameView view) {
        gameModel = model;
        gameView = view;
    }

    @Override
    public void run() {
        long startTime;
        long sleepTime;

        while (running) {
//            Log.d("thread", "in loop");
            Canvas canvas = null;
            startTime = System.currentTimeMillis();

            try {
                canvas = gameView.getHolder().lockCanvas();

                //Update
                gameModel.update();
                
                //Draw
                synchronized (gameView.getHolder()) {
                    gameModel.drawToCanvas(canvas);
                }
            } finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }

            //Sleep
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            sleepTime = FRAME_TIME - timeTaken;
//            Log.d("TIME", Long.toString(sleepTime));
            try {
                if (sleepTime > 0) {
                    sleep(sleepTime);
                }
                else {
                    sleep(10);
                }
            }
            catch (InterruptedException e) {
            }
        }
    }

    public void setRunning(boolean r) {
        running = r;
    }

    public boolean getRunning() {
        return running;
    }
}

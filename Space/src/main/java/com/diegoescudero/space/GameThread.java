package com.diegoescudero.space;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Random;

public class GameThread extends Thread {
    private boolean running = false;
    private final int FPS = 60;
    private final int FRAME_TIME = 1000 / FPS;
    private final int FRAME_SKIPS = 5;

    private GameModel gameModel;
    private GameView gameView;
    private GameController gameController;

    public GameThread(GameModel model, GameView view, GameController controller) {
        gameModel = model;
        gameView = view;
        gameController = controller;
    }

    @Override
    public void run() {
        Canvas canvas;
        long startTime;
        long timeTaken;
        long sleepTime;
        int framesSkipped;

        while (running) {
            canvas = null;

            //Update and Draw
            try {
                framesSkipped = 0;
                startTime = System.currentTimeMillis();
                canvas = gameView.getHolder().lockCanvas();

                //Update
                gameModel.update(gameController.getPlayerShots(), gameController.getCurrentTilt());

                //Game Over?
                if (gameModel.isGameOver()) {
                    running = false;
                }

                //Draw
//                synchronized (gameView.getHolder()) {
                    gameModel.drawToCanvas(canvas);
//                }
            } finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }

            //Sleep
            timeTaken = System.currentTimeMillis() - startTime;
            sleepTime = FRAME_TIME - timeTaken;
//            Log.d("SleepTime", Long.toString(sleepTime));
            try {
                if (sleepTime > 0) {
                    sleep(sleepTime);
                }
//                while (framesSkipped < FRAME_SKIPS && sleepTime < 0) {
//                    gameModel.update(playerShots, currentTilt);
//                    sleepTime += FRAME_TIME;
//                    framesSkipped++;
//                }
            }
            catch (InterruptedException e) {
            }
        }

        if (gameModel.isGameOver()) {
            gameController.launchContinueMenu();
        }
    }

    public void setRunning(boolean r) {
        running = r;
    }

    public boolean getRunning() {
        return running;
    }
}

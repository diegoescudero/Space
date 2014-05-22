package com.diegoescudero.space;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Random;

public class GameThread extends Thread {
    private boolean running = false;
    private final int FPS = 60;
    private final int FRAME_TIME = 1000000000 / FPS;
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

        while (running) {
            canvas = null;

            //Update and Draw
            try {
                startTime = System.nanoTime();
                canvas = gameView.getHolder().lockCanvas();

                //Update
                gameModel.update(gameController.getPlayerShots(), gameController.getCurrentTilt());

                //Game Over?
                if (gameModel.isGameOver()) { running = false; }

                //Draw
                synchronized (gameView.getHolder()) { gameModel.drawToCanvas(canvas); }
            }
            finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }

            //Sleep if needed
            timeTaken = System.nanoTime() - startTime;
            sleepTime = FRAME_TIME - timeTaken;
            try {
                if (sleepTime > 0) {
                    sleep(Math.abs((int)sleepTime/1000000), Math.abs((int)sleepTime % 1000000));
                }

            }
            catch (InterruptedException e) {}
        }

        //Load menu after game over
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

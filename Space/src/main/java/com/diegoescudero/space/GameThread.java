package com.diegoescudero.space;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.SystemClock;

public class GameThread extends Thread {
    private boolean running = false;

    private long lastTimeMS = 0;
    private double smoothedDeltaTimeMS = 16.0d;
    private double avgDeltaTimeMS = smoothedDeltaTimeMS;

    private final double avgPeriod = 40.0d;
    private final double smoothFactor = 0.1d;

    private GameModel gameModel;
    private GameView gameView;
    private GameController gameController;

    public GameThread(GameModel model, GameView view, GameController controller) {
        gameModel = model;
        gameView = view;
        gameController = controller;
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        Canvas canvas;

        while (running) {
            canvas = null;

            //Update and Draw
            try {
                canvas = gameView.getHolder().lockCanvas();

                //Calc and smooth time
                long currentTimeMS = SystemClock.uptimeMillis();
                double currentDeltaTimeMS;
                if (lastTimeMS > 0) {
                    currentDeltaTimeMS = (currentTimeMS - lastTimeMS);
                }
                else {
                    currentDeltaTimeMS = smoothedDeltaTimeMS; // just the first time
                }
                avgDeltaTimeMS = (currentDeltaTimeMS + avgDeltaTimeMS * (avgPeriod - 1)) / avgPeriod;

                // Calc a better aproximation for smooth stepTime
                smoothedDeltaTimeMS = smoothedDeltaTimeMS + (avgDeltaTimeMS - smoothedDeltaTimeMS) * smoothFactor;

                lastTimeMS = currentTimeMS;

                //Update
                gameModel.update(smoothedDeltaTimeMS / 1000.0d, gameController.getCurrentTilt(), gameController.getGesture());

                //Game Over?
                if (gameModel.isGameOver()) { running = false; }

                //Draw
//                   gameModel.drawToCanvas(canvas);
                    gameView.onDraw(canvas);

                //Set FPS
//                final long frameTime = Math.round(Math.abs(avgDeltaTimeMS));
//                gameController.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        gameController.setHealth(Math.round(1.0d / (frameTime / 1000.0d)));
//                    }
//                });

            }
            //Release canvas
            finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
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

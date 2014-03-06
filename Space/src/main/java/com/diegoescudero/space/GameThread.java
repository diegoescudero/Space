package com.diegoescudero.space;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private boolean isRunning;
    private final int FPS = 30;

    private GameModel gameModel;
    private GameView gameView;
    private SurfaceHolder surfaceHolder;

    public GameThread(GameModel gameModel, GameView gameView) {
        this.gameModel = gameModel;
        this.gameView = gameView;
        this.surfaceHolder = this.gameView.getHolder();
    }

    public void start() {
        isRunning = true;

        while (true) {
            Canvas c = gameModel.drawToCanvas(surfaceHolder.lockCanvas());
            surfaceHolder.unlockCanvasAndPost(c);
        }

    }

    public boolean isRunning() {
        return isRunning;
    }
}

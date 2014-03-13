package com.diegoescudero.space;

import android.graphics.Canvas;
import android.util.Log;

public class GameThread extends Thread {
    private boolean running;
    private final int FPS = 30;

    private GameModel gameModel;
    private GameView gameView;

    public GameThread(GameModel gameModel, GameView gameView) {
        this.gameModel = gameModel;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        Canvas canvas = null;

        while (running) {
            try {
                canvas = gameView.getHolder().lockCanvas();
                if (canvas != null) {
                    Log.d("SUCCESS", "Should Draw");
                    canvas = gameModel.drawToCanvas(canvas);
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
                Log.d("FAIL", "Not read");
            }
        }
    }

    public void setRunning(boolean r) {
        running = r;
    }
}

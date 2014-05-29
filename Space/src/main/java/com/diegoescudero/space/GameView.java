package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameView extends SurfaceView {
    GameModel gameModel;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null && gameModel != null) {
            //Initialize if need
            if (!gameModel.isInitialized()) {
                gameModel.initialize(canvas);
            }

            //Clear Canvas
            canvas.drawColor(Color.BLACK);

            //Draw Stars
            Sprite star = gameModel.getStarSprite();
            HashMap<Rect, Integer> stars = gameModel.getStars();
            for (Rect r : stars.keySet()) {
                canvas.drawBitmap(star.getBitmap(), star.getFrameRect(), r, null);
            }

            //Draw Player Ship
            Sprite player = gameModel.getPlayerSprite();
            Rect playerLocation = gameModel.getPlayerLocation();
            if (player != null) {
                canvas.drawBitmap(player.getBitmap(), player.getFrameRect(), playerLocation, null);
            }

            //Draw Asteroids
            ArrayList<Quadrant> visibleQuads = gameModel.getVisibleQuads();
            for (Quadrant q : visibleQuads) {
                for (Map.Entry<Rect, Sprite> e : q.getAsteroids().entrySet()) {
                    canvas.drawBitmap(e.getValue().getBitmap(), e.getValue().getFrameRect(), e.getKey(), null);
                }
            }
        }
    }
}

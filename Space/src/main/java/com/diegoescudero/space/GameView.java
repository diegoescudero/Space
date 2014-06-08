package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;

public class GameView extends SurfaceView {
    GameModel gameModel;

    public GameView(Context context) {
        super(context);
        setKeepScreenOn(true);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setKeepScreenOn(true);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setKeepScreenOn(true);
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
            HashMap<SpritePosition, Integer> stars = gameModel.getStars();
            for (SpritePosition p : stars.keySet()) {
                canvas.drawBitmap(star.getBitmap(), star.getFrameRect(), p.rect(), null);
            }

            //Draw Player Ship
            Sprite player = gameModel.getPlayerSprite();
            SpritePosition playerPosition = gameModel.getPlayerPosition();
            if (player != null) {
                canvas.drawBitmap(player.getBitmap(), player.getFrameRect(), playerPosition.rect(), null);
            }

            //Draw Asteroids
            Sprite asteroid = gameModel.getAsteroidSprite();
            ArrayList<Quadrant> visibleQuads = gameModel.getVisibleQuads();
            for (Quadrant q : visibleQuads) {
                for (SpritePosition p : q.getAsteroids()) {
                    canvas.drawBitmap(asteroid.getBitmap(), asteroid.getFrameRect(), p.rect(), null);
                }
            }

            //Draw Flare
            Sprite flare = gameModel.getFlareSprite();
            SpritePosition flarePosition = gameModel.getFlarePosition();
            canvas.drawBitmap(flare.getBitmap(), flare.getFrameRect(), flarePosition.rect(), null);

            //Draw Flare
            Sprite missile = gameModel.getMissileSprite();
            SpritePosition missilePosition = gameModel.getMissilePosition();
            canvas.drawBitmap(missile.getBitmap(), missile.getFrameRect(), missilePosition.rect(), null);
        }
    }
}

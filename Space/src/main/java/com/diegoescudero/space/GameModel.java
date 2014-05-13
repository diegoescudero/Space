package com.diegoescudero.space;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class GameModel {
    //General
    private Context context;
    private boolean isGameOver = false;
    private int playerHealth = 100;

    //Tilt
    private final float TILT_CENTER_LIMIT = 0.5f;
    private final float TILT_MAX = 5.0f;
    private final float TILT_NOISE = 0.25f;

    //Game Screen
    private boolean initialized = false;
    private int canvasWidth = 0;
    private int canvasHeight = 0;

    //Player
    private final float PLAYER_VELOCITY_MAX_PERC = .015f; //1.5% of screen
    private final float PLAYER_ACCELERATION_PERC = .005f; //.5% of screen
    private float PLAYER_VELOCITY_MAX = 0;
    private float PLAYER_ACCELERATION = 0;
    private float playerVelocity = 0;
    private int playerWidth = 0;
    private int playerHeight = 0;
    private Sprite player = null;
    private Rect playerLocation = null;

    //Asteroids
    private final float ASTEROID_VELOCITY = 30;
    private HashMap<Sprite, Rect> asteroids = new HashMap<Sprite, Rect>();


    public GameModel(Context context) {
        this.context = context;
    }

    private void initialize(Canvas c) {
        canvasWidth = c.getWidth();
        canvasHeight = c.getHeight();
        playerWidth = canvasWidth / 8;
        playerHeight = playerWidth;

        player = new Sprite(context, SpriteType.PLAYER);
        playerLocation = new Rect(
                canvasWidth/2 - playerWidth/2,
                canvasHeight/2,
                canvasWidth/2 + playerWidth/2,
                canvasHeight/2 + playerHeight
        );

        PLAYER_VELOCITY_MAX = PLAYER_VELOCITY_MAX_PERC * canvasWidth;
        PLAYER_ACCELERATION = PLAYER_ACCELERATION_PERC * canvasWidth;

        initialized = true;
        createAsteroids();
    }

    private void createAsteroids() {
        for (int i = 0; i < 10; i++) {
            Sprite e = new Sprite(context, SpriteType.ASTEROID);
            Random rand = new Random();
            int left = rand.nextInt(canvasWidth-playerWidth*2);
            int bot = -2*i * playerHeight*2;
            Rect r = new Rect(
                    left,
                    bot - playerHeight*2,
                    left + playerWidth*2,
                    bot
            );
            asteroids.put(e, r);
        }
    }

    public void update(int playerShots, float tilt) {
        if (!isGameOver) {
            updateVelocityFromTilt(tilt);
            updatePlayerTilt(tilt);
            updateAsteroidPositions();
            checkForCollisions();
            updateIsGameOver();
        }
    }

    private void updateVelocityFromTilt(float tilt) {
        float percent = tilt / TILT_MAX;
        float targetVelocity = percent * PLAYER_VELOCITY_MAX;

        //Center Ship
        if (Math.abs(tilt) <= TILT_CENTER_LIMIT) {
            targetVelocity = 0;
        }

        //Positive Velocity
        if (playerVelocity < targetVelocity) {
            if (playerVelocity + PLAYER_ACCELERATION > targetVelocity) {
                playerVelocity = targetVelocity;
            }
            else {
                playerVelocity += PLAYER_ACCELERATION;
            }
        }
        //Negative Velocity
        else if (playerVelocity > targetVelocity) {
            if (playerVelocity - PLAYER_ACCELERATION < targetVelocity) {
                playerVelocity = targetVelocity;
            }
            else {
                playerVelocity -= PLAYER_ACCELERATION;
            }
        }

        //Constrain velocity to max
        if (playerVelocity > PLAYER_VELOCITY_MAX) {
            playerVelocity = PLAYER_VELOCITY_MAX;
        }
        else if (playerVelocity < -PLAYER_VELOCITY_MAX) {
            playerVelocity = -PLAYER_VELOCITY_MAX;
        }

//        Log.d("Tilt", Float.toString(playerVelocity));

    }

    private void updatePlayerTilt(float tilt) {
        if(player != null) {
//            player.showAnimationFrame(Animation.TILT_LEFT, 7);
        }
    }

    private void updateAsteroidPositions() {
        ArrayList<Sprite> remove = new ArrayList<Sprite>();

        for (Map.Entry<Sprite, Rect> e : asteroids.entrySet()) {
            Rect temp = e.getValue();
            temp.top += PLAYER_VELOCITY_MAX;
            temp.bottom += PLAYER_VELOCITY_MAX;
            temp.left -= playerVelocity;
            temp.right -= playerVelocity;

            //Mark to remove
            if (temp.top > canvasHeight) {
                remove.add(e.getKey());
            }
        }

        //Projectiles to remove from list (off screen)
        for (Sprite p : remove) {
            asteroids.remove(p);
        }
    }

    private void checkForCollisions() {
        if (playerLocation != null) {
            int pRadius = playerWidth / 2;
            Point pCenter = new Point(playerLocation.left + pRadius, playerLocation.top + pRadius);

            for (Map.Entry<Sprite, Rect> e : asteroids.entrySet()) {
                int aRadius = (e.getValue().right - e.getValue().left) / 2;
                Point aCenter = new Point(e.getValue().left + aRadius, e.getValue().top + aRadius);

                int xSquared = (pCenter.x-aCenter.x) * (pCenter.x-aCenter.x);
                int ySquared = (pCenter.y-aCenter.y) * (pCenter.y-aCenter.y);
                double distance = Math.sqrt(xSquared + ySquared);

                //Collision
                if (distance < pRadius + aRadius) {
                    playerHealth = 0;
                    break;
                }
            }
        }
    }

    private void updateIsGameOver() {
        if (playerHealth == 0) {
            isGameOver = true;
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void drawToCanvas(Canvas c) {
        if (!initialized) {
            initialize(c);
        }

        if (c != null) {
            //Clear Canvas
            c.drawColor(Color.BLACK);

            //Draw Player Ship
            if (player != null) {
                c.drawBitmap(player.getBitmap(), null, playerLocation, null);
            }

            //Draw Asteroids
            for (Map.Entry<Sprite, Rect> e : asteroids.entrySet()) {
                c.drawBitmap(e.getKey().getBitmap(), null, e.getValue(), null);
            }
        }
    }
}

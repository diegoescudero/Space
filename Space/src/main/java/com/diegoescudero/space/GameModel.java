package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class GameModel {
    private Context context;
    private Sprite ship;

    private final int shipSpeed = 25;
    private Rect shipLocation = null;
    private int tiltDirection = 0;

    private int canvasWidth = 0;
    private int canvasHeight = 0;

    public GameModel(Context context) {
        this.context = context;
        ship = new Sprite(context);
    }

    public void setTiltDirection(float tilt) {
        if (tilt >= 2) {
            tiltDirection = 1;
        }
        else if (tilt <= -2) {
            tiltDirection = -1;
        }
        else {
            tiltDirection = 0;
        }
    }

    public void update() {
        //calculate new ship location
        if (shipLocation != null) {
            if (tiltDirection > 0) {
                if (shipLocation.right + shipSpeed < canvasWidth) {
                    shipLocation.left += shipSpeed;
                    shipLocation.right += shipSpeed;
                }
                else {
                    int distLeft = canvasWidth - shipLocation.right;

                    shipLocation.right += distLeft;
                    shipLocation.left += distLeft;
                }
            }
            else if (tiltDirection < 0) {
                if (shipLocation.left - shipSpeed > 0) {
                    shipLocation.left -= shipSpeed;
                    shipLocation.right -= shipSpeed;
                }
                else {
                    int distLeft = shipLocation.left;

                    shipLocation.left -= distLeft;
                    shipLocation.right -= distLeft;
                }
            }
        }
    }

    public void drawToCanvas(Canvas c) {
        if (shipLocation == null) {
            canvasWidth = c.getWidth();
            canvasHeight = c.getHeight();
            int width = c.getWidth()/6;
            int height = c.getHeight()/6;

            shipLocation = new Rect(
                    c.getWidth()/2 - width/2,
                    c.getHeight() - height,
                    c.getWidth()/2 + width/2,
                    c.getHeight());
        }

        if (c != null) {
            int width = c.getWidth()/4;
            int height = c.getHeight()/4;

            //Clear Canvas
            c.drawColor(Color.BLACK);

            //Draw Player Ship
            c.drawBitmap(ship.getBitmap(), null, shipLocation, null);
        }
    }
}

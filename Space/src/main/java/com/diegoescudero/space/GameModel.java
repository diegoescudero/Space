package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class GameModel {
    private Context context;

    private Sprite ship;
    private Rect shipLocation = null;
    private int shipVelocity = 0;

    private final int TILT_FACTOR = 3;
    private final int SHIP_ACCELERATION = 2;
    private final int MAX_SHIP_VELOCITY = 10;

    private int canvasWidth = 0;
    private int canvasHeight = 0;

    public GameModel(Context context) {
        this.context = context;
        ship = new Sprite(context);
    }

    public void updateShipVelocity(float tilt) {
        int tiltInt = (int)tilt;

        //Increase/decrease ship velocity
        if (tiltInt > 1 || tiltInt < -1) {
            int targetVelocity = tiltInt * TILT_FACTOR;

            if (Math.abs(shipVelocity - targetVelocity) <= SHIP_ACCELERATION) {
                shipVelocity = targetVelocity;
            }
            else if (shipVelocity < targetVelocity) {
                shipVelocity += SHIP_ACCELERATION;
            }
            else if (shipVelocity > targetVelocity) {
                shipVelocity -= SHIP_ACCELERATION;
            }
        }
        //Balance ship to 0 velocity
        else {
            if (shipVelocity > 0) {
                if (shipVelocity - SHIP_ACCELERATION < 0) {
                    shipVelocity = 0;
                }
                else {
                    shipVelocity -= SHIP_ACCELERATION;
                }
            }
            else if (shipVelocity < 0) {
                if (shipVelocity + SHIP_ACCELERATION > 0) {
                    shipVelocity = 0;
                }
                else {
                    shipVelocity += SHIP_ACCELERATION;
                }
            }
        }

        //Cap ship velocity
        if (shipVelocity > MAX_SHIP_VELOCITY) {
            shipVelocity = MAX_SHIP_VELOCITY;
        }
        else if (shipVelocity < -MAX_SHIP_VELOCITY) {
            shipVelocity = -MAX_SHIP_VELOCITY;
        }
    }

    public void update() {
        updateShipLocation();
        //updateEnemyLocations();
        //updateProjectileLocations();
        //checkForCollisions();
    }

    private void updateShipLocation() {
        if (shipLocation != null) {
            if (shipVelocity < 0) {
                if (shipLocation.left - shipVelocity > 0) {
                    shipLocation.left += shipVelocity;
                    shipLocation.right += shipVelocity;
                }
                else {
                    int distLeft = shipLocation.left - shipVelocity;

                    shipLocation.left -= distLeft;
                    shipLocation.right -= distLeft;
                }
            }
            else if (shipVelocity > 0) {
                if (shipLocation.right + shipVelocity < canvasWidth) {
                    shipLocation.left += shipVelocity;
                    shipLocation.right += shipVelocity;
                }
                else {
                    int distLeft = canvasWidth - shipLocation.right;

                    shipLocation.right += distLeft;
                    shipLocation.left += distLeft;
                }
            }

//
//            shipLocation.left += shipVelocity;
//            shipLocation.right += shipVelocity;
//            if (tiltDirection > 0) {
//                if (shipLocation.right + shipVelocity < canvasWidth) {
//                    shipLocation.left += shipVelocity;
//                    shipLocation.right += shipVelocity;
//                }
//                else {
//                    int distLeft = canvasWidth - shipLocation.right;
//
//                    shipLocation.right += distLeft;
//                    shipLocation.left += distLeft;
//                }
//            }
//            else if (tiltDirection < 0) {
//                if (shipLocation.left - shipVelocity > 0) {
//                    shipLocation.left -= shipVelocity;
//                    shipLocation.right -= shipVelocity;
//                }
//                else {
//                    int distLeft = shipLocation.left;
//
//                    shipLocation.left -= distLeft;
//                    shipLocation.right -= distLeft;
//                }
//            }
        }
    }

    public void drawToCanvas(Canvas c) {
        if (shipLocation == null) {
            canvasWidth = c.getWidth();
            canvasHeight = c.getHeight();
            int width = c.getWidth()/8;
            int height = c.getHeight()/8;

            shipLocation = new Rect(
                    c.getWidth()/2 - width/2,
                    c.getHeight() - height,
                    c.getWidth()/2 + width/2,
                    c.getHeight());
        }

        if (c != null) {
            //Clear Canvas
            c.drawColor(Color.BLACK);

            //Draw Player Ship
            c.drawBitmap(ship.getBitmap(), null, shipLocation, null);
        }
    }
}

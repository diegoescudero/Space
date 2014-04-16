package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashSet;

public class GameModel {
    private Context context;

    //Player Ship
    private Sprite shipPlayer;
    private Rect shipLocation = null;
    private int shipVelocity = 0;

    //Player Projectiles
    private HashSet<SpriteProjectile> playerProjectiles;

    //Enemy Ships
    //    private ArrayList<Rect>

    //Enemy Projectiles

    private boolean isAutoTilt = false;
    private final int TILT_FACTOR = 3;
    private final int SHIP_ACCELERATION = 4;
    private final int MAX_SHIP_VELOCITY = 20;

    private int canvasWidth = 0;
    private int canvasHeight = 0;

    public GameModel(Context context) {
        this.context = context;
        shipPlayer = new SpritePlayer(context);
        playerProjectiles = new HashSet<SpriteProjectile>();
    }

    public void playerFire() {
        SpriteProjectile p = new SpriteProjectile(context);
        p.setLocation(new Rect(shipLocation));

        playerProjectiles.add(p);
    }

    public void setAutoTilt(boolean enabled) {
        isAutoTilt = enabled;
    }

    public void updateShipVelocity(float tilt) {
        int tiltInt = (int)tilt;

        if (isAutoTilt) {

        }
        else {
            //Increase/decrease ship velocity
            if (tiltInt > 0.5 || tiltInt < -0.5) {
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
    }

    public void update(int playerShots, float tilt) {
        if (playerShots >= 1) {
            playerFire();
        }
        updateShipVelocity(tilt);

        updateShipLocation();
        //updateEnemyLocations();
        updateProjectileLocations();
        //checkForCollisions();
    }

    private void updateShipLocation() {
        if (!isAutoTilt) {
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
            }
        }
    }

    private void updateProjectileLocations() {
        ArrayList<SpriteProjectile> remove = new ArrayList<SpriteProjectile>();

        for (SpriteProjectile p : playerProjectiles) {
            Rect temp = p.getLocation();
            temp.top -= MAX_SHIP_VELOCITY;
            temp.bottom -= MAX_SHIP_VELOCITY;

            p.setLocation(temp);

            //Mark to remove
            if (temp.top < 0 && temp.bottom < 0) {
                remove.add(p);
            }
        }

        //Projectiles to remove from list (off screen)
        for (SpriteProjectile p : remove) {
            playerProjectiles.remove(p);
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
            c.drawBitmap(shipPlayer.getBitmap(), null, shipLocation, null);

            //Draw Projectiles
            for (SpriteProjectile p : playerProjectiles) {
                c.drawBitmap(p.getBitmap(), null, p.getLocation(), null);
            }

            //Draw Enemies
        }
    }
}

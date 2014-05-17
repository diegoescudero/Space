package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class GameModel {
    //General
    private Context context;
    private boolean isGameOver = false;

    //Quadrants 6x6 Grid
    private final int QUAD_GRID_SIZE = 6;
    private final int QUAD_X_SCALE = 3;
    private final int QUAD_Y_SCALE = 2;
    private int quadWidth = 0;
    private int quadHeight = 0;
    private Quadrant[][] quads = new Quadrant[QUAD_GRID_SIZE][QUAD_GRID_SIZE];
    private HashSet<Quadrant> spawnQuads = new HashSet<Quadrant>();
    private ArrayList<Quadrant> visibleQuads = new ArrayList<Quadrant>();
    private ArrayList<Quadrant> playerQuads = new ArrayList<Quadrant>();
    private ArrayList<Quadrant> emptySpawnQuads = new ArrayList<Quadrant>();

    //Tilt
    private final float TILT_CENTER_LIMIT = 0.5f;
    private final float TILT_MAX = 5.0f;
    private final float TILT_NOISE = 0.25f;

    //Game Screen
    private int gameFieldWidth = 0;
    private int gameFieldHeight = 0;
    private boolean initialized = false;
    private int canvasWidth = 0;
    private int canvasHeight = 0;

    //Player
    private int playerHealth = 100;
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
    private Sprite asteroidSprite;
    private ArrayList<Rect> asteroidRects = new ArrayList<Rect>();
    private ArrayList<Rect> freeAsteroidRects = new ArrayList<Rect>();
    private final int ASTEROID_COUNT_MAX = 10;
    private int asteroidCount = 0;
    private int asteroidRadius = 0;

    //Stars
    private HashMap<Sprite, Rect> stars = new HashMap<Sprite, Rect>();
    private final int STAR_COUNT_MAX = 100;


    public GameModel(Context context) {
        this.context = context;

        asteroidSprite = new Sprite(context, SpriteType.ASTEROID);

        createAsteroids();
//        createStars();
    }

    private void initialize(Canvas c) {
        //Size the screen
        canvasWidth = c.getWidth();
        canvasHeight = c.getHeight();

        //Size the game field
        gameFieldWidth = canvasWidth * QUAD_X_SCALE;
        gameFieldHeight = canvasHeight * QUAD_Y_SCALE;

        //Size the player
        playerWidth = canvasWidth / 8;
        playerHeight = playerWidth;

        //Size the asteroids
        asteroidRadius = canvasWidth / 8;

        //Create the player
        player = new Sprite(context, SpriteType.PLAYER);
        playerLocation = new Rect(
                canvasWidth/2 - playerWidth/2,
                3*canvasHeight/4,
                canvasWidth/2 + playerWidth/2,
                3*canvasHeight/4 + playerHeight
        );

        //Set constants
        PLAYER_VELOCITY_MAX = PLAYER_VELOCITY_MAX_PERC * canvasWidth;
        PLAYER_ACCELERATION = PLAYER_ACCELERATION_PERC * canvasWidth;

        //Create quadrant system
        initializeQuadrants();

        initialized = true;
    }

    private void initializeQuadrants() {
        quadWidth = gameFieldWidth / QUAD_GRID_SIZE;
        quadHeight = gameFieldHeight / QUAD_GRID_SIZE;

        for (int row = 0; row < QUAD_GRID_SIZE; row++) {
            for (int col = 0; col < QUAD_GRID_SIZE; col++) {
                int left = col * quadWidth;
                int top = row * quadHeight;
                Rect r = new Rect(left, top, left+quadWidth, top+quadHeight);
                Quadrant q = new Quadrant(r, row, col);

                if ((row == 3 || row == 4 || row == 5) && (col == 2 || col == 3)) {
                    visibleQuads.add(q);
                }
                else {
                    spawnQuads.add(q);
                    emptySpawnQuads.add(q);
                }

                quads[row][col] = q;
            }
        }
    }

    private void createAsteroids() {
        for (; asteroidCount < ASTEROID_COUNT_MAX; asteroidCount++) {
            asteroidRects.add(new Rect());
        }
    }

    private void createStars() {
        for (int i = 0; i < STAR_COUNT_MAX; i++) {
//            asteroids.put(new Sprite(context, SpriteType.STAR), new Rect());
        }
    }

    public void update(int playerShots, float tilt) {
        if (!isGameOver && initialized) {
            updateVelocityFromTilt(tilt);
            updatePlayerTiltAnimation(tilt);
            updateAsteroidPositions();
            recalculateAsteroidQuadrants();
            assignUnusedAsteroids();
//            checkForCollisions();
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

    private void updatePlayerTiltAnimation(float tilt) {
        if(player != null) {
//            player.showAnimationFrame(Animation.TILT_LEFT, 7);
        }
    }

    private void updateAsteroidPositions() {
        for (Rect r : asteroidRects) {
            r.top += PLAYER_VELOCITY_MAX;
            r.bottom += PLAYER_VELOCITY_MAX;
            r.left -= playerVelocity;
            r.right -= playerVelocity;
        }
    }

    private void recalculateAsteroidQuadrants() {
//        for (int row = 0; row < QUAD_GRID_SIZE; row++) {
//            for (int col = 0; col < QUAD_GRID_SIZE; col++) {
//                boolean placedAsteroid = false;
//                quads[row][col].clearSprites();
//
//                for (Rect r : asteroidRects) {
//                    if (rectsOverlap(quads[row][col].getLocation(), r)) {
//                        quads[row][col].addSprite(r, asteroidSprite);
//                        placedAsteroid = true;
//                    }
//                }
//
//                if (!placedAsteroid && !emptySpawnQuads.contains(quads[row][col]) && !visibleQuads.contains(quads[row][col])) {
//                    emptySpawnQuads.add(quads[row][col]);
//                }
//            }
//        }

        //Clear free rects list
        freeAsteroidRects.clear();

        //Clear all quadrants
        for (int row = 0; row < QUAD_GRID_SIZE; row++) {
            for (int col = 0; col < QUAD_GRID_SIZE; col++) {
                quads[row][col].clearSprites();
            }
        }

        //Re-assign rects to quadrants
        for (Rect r : asteroidRects) {
            boolean rectAdded = false;

            for (int row = 0; row < QUAD_GRID_SIZE; row++) {
                for (int col = 0; col < QUAD_GRID_SIZE; col++) {
                    if (rectsOverlap(quads[row][col].getLocation(), r)) {
                        rectAdded = true;
                        quads[row][col].addSprite(r, asteroidSprite);
                    }
                }
            }

            if (!rectAdded) {
                freeAsteroidRects.add(r);
            }
        }

        //List emptySpawnQuads
        emptySpawnQuads.clear();
        for (int row = 0; row < QUAD_GRID_SIZE; row++) {
            for (int col = 0; col < QUAD_GRID_SIZE; col++) {
                if (quads[row][col].isEmpty() && !visibleQuads.contains(quads[row][col])) {
                    emptySpawnQuads.add(quads[row][col]);
                }
            }
        }

//        HashSet<Rect> updated = new HashSet<Rect>();
//        HashMap<Rect, Point> remove = new HashMap<Rect, Point>();
//
//        for (int row = 0; row < QUAD_GRID_SIZE; row++) {
//            for (int col = 0; col < QUAD_GRID_SIZE; col++) {
//                for (Map.Entry<Rect, Sprite> e : quads[row][col].getSprites().entrySet()) {
//                    if (!updated.contains(e.getKey())) {
//                        //Update rect to new location
//                        Rect pos = e.getKey();
//                        pos.top += PLAYER_VELOCITY_MAX;
//                        pos.bottom += PLAYER_VELOCITY_MAX;
//                        pos.left -= playerVelocity;
//                        pos.right -= playerVelocity;
//
//                        //Mark sprite as updated
//                        updated.add(e.getKey());
//
//                        //Check if moving to bottom & if completely in bottom
//                        if (pos.bottom > quads[row][col].getLocation().bottom) {
//                            //Bottom & top sticking out
//                            if (pos.top > quads[row][col].getLocation().bottom) {
//                                //Last row
//                                if (row == QUAD_GRID_SIZE - 1) {
//                                    remove.put(e.getKey(), new Point(row, col));
//                                }
//                                //Not last row
//                                else {
//                                    if (!quads[row+1][col].containsSprite(e.getKey())) {
//                                        quads[row + 1][col].addSprite(e.getKey(), e.getValue());
//                                    }
//                                    remove.put(e.getKey(), new Point(row, col));
//                                }
//                            }
//                            //Bottom sticking out
//                            else {
//                                if (row < QUAD_GRID_SIZE - 1) {
//                                    if (!quads[row+1][col].containsSprite(e.getKey())) {
//                                        quads[row+1][col].addSprite(e.getKey(), e.getValue());
//                                    }
//                                }
//                            }
//                        }
//
//                        //Check if moving to bottom & if completely in left
//
//                        //Check if moving to bottom & if completely in right
//                    }
//                }
//            }
//        }
//
//        //Remove old asteroids
//        for (Map.Entry<Sprite, Point> e : remove.entrySet()) {
//            Point p = e.getValue();
//            quads[p.x][p.y].removeSprite(e.getKey());
//            if (quads[p.x][p.y].isEmpty() && spawnQuads.contains(quads[p.x][p.y])) {
//                emptySpawnQuads.add(quads[p.x][p.y]);
//            }
//        }
    }

    private void assignUnusedAsteroids() {
        while (freeAsteroidRects.size() > 0 && emptySpawnQuads.size() > 0) {
            Random rand = new Random();

            //Pick random quadrant
            Quadrant q = emptySpawnQuads.get(rand.nextInt(emptySpawnQuads.size()));

            //Calculate random asteroid position in quadrant
            Rect qRect = q.getLocation();
            int left = rand.nextInt(quadWidth - asteroidRadius * 2) + qRect.left;
            int top = rand.nextInt(quadHeight - asteroidRadius * 2) + qRect.top;

            //Get asteroid rect to transfer to quadrant
            Rect r = freeAsteroidRects.get(freeAsteroidRects.size() - 1);

            //Place rect
            r.left = left;
            r.top = top;
            r.right = left + asteroidRadius * 2;
            r.bottom = top + asteroidRadius * 2;

            //Add asteroid data to quadrant
            q.addSprite(r, asteroidSprite);

            //Remove quadrant from empty list and remove
            freeAsteroidRects.remove(freeAsteroidRects.size() - 1);
            emptySpawnQuads.remove(q);
        }
    }

//    private void checkForCollisions() {
//        if (playerLocation != null) {
//            int pRadius = playerWidth / 2;
//            Point pCenter = new Point(playerLocation.left + pRadius, playerLocation.top + pRadius);
//
//            for (Map.Entry<Sprite, Rect> e : asteroids.entrySet()) {
//                int aRadius = (e.getValue().right - e.getValue().left) / 2;
//                Point aCenter = new Point(e.getValue().left + aRadius, e.getValue().top + aRadius);
//
//                int xSquared = (pCenter.x-aCenter.x) * (pCenter.x-aCenter.x);
//                int ySquared = (pCenter.y-aCenter.y) * (pCenter.y-aCenter.y);
//                double distance = Math.sqrt(xSquared + ySquared);
//
//                //Collision
//                if (distance < pRadius + aRadius) {
//                    playerHealth = 0;
//                    break;
//                }
//            }
//        }
//    }

    private boolean rectsOverlap(Rect r1, Rect r2) {
        return !(r1.right <= r2.left || r1.top >= r2.bottom || r1.left >= r2.right || r1.bottom <= r2.top);
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
            for (Quadrant q : visibleQuads) {
                for (Map.Entry<Rect, Sprite> e : q.getSprites().entrySet()) {
                    Rect offset = new Rect(e.getKey());
                    offset.left -= canvasWidth;
                    offset.top -= canvasHeight;
                    offset.right -= canvasWidth;
                    offset.bottom -= canvasHeight;
                    c.drawBitmap(e.getValue().getBitmap(), null, offset, null);
                }
            }
        }
    }
}

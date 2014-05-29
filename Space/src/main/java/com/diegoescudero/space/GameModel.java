package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameModel {
    //General
    private Context context;
    private boolean isGameOver = false;
    private boolean initialized = false;
    Random rand = new Random();

    //Quadrants 6x6 Grid
    private final int QUAD_X_DENSITY = 2;
    private final int QUAD_Y_DENSITY = 3;
    private final int QUAD_X_SCALE = 3;
    private final int QUAD_Y_SCALE = 2;
    private final int QUAD_X_GRID_SIZE = QUAD_X_DENSITY * QUAD_X_SCALE;
    private final int QUAD_Y_GRID_SIZE = QUAD_Y_DENSITY * QUAD_Y_SCALE;
    private int quadWidth = 0;
    private int quadHeight = 0;
    private Quadrant[][] quads = new Quadrant[QUAD_Y_GRID_SIZE][QUAD_X_GRID_SIZE];
    private ArrayList<Quadrant> visibleQuads = new ArrayList<Quadrant>();
    private ArrayList<Quadrant> playerQuads = new ArrayList<Quadrant>();
    private ArrayList<Quadrant> spawnQuads = new ArrayList<Quadrant>();
    private ArrayList<Quadrant> emptySpawnQuads = new ArrayList<Quadrant>();

    //Tilt
    private final float TILT_CENTER_LIMIT = 0.5f;
    private final float TILT_MAX = 4.0f;

    //Game Screen
    private int gameFieldWidth = 0;
    private int gameFieldHeight = 0;
    private int canvasWidth = 0;
    private int canvasHeight = 0;

    //Player
    private int playerHealth = 100;
    private final double PLAYER_VELOCITY_X_MAX_PERC = 1.0d; //100% of screen in 1 second
    private final double PLAYER_VELOCITY_Y_MAX_PERC = 0.75d; //75% of screen in 1 second
    private final double PLAYER_ACCELERATION_X_PERC = 0.5d; //50% of screen in 1 second
    private int PLAYER_VELOCITY_X_MAX = 0;
    private int PLAYER_VELOCITY_Y_MAX = 0;
    private int PLAYER_ACCELERATION_X = 0;
    private int playerVelocityX = 0;
    private int playerVelocityY = 0;
    private int playerWidth = 0;
    private int playerHeight = 0;
    private Sprite playerSprite = null;
    private Rect playerLocation = null;

    //Asteroids
    private Sprite asteroidSprite;
    private ArrayList<Rect> asteroidRects = new ArrayList<Rect>();
    private ArrayList<Rect> freeAsteroidRects = new ArrayList<Rect>();
    private final int ASTEROID_COUNT_MAX = 10;
    private int asteroidCount = 0;
    private int asteroidRadius = 0;

    //Stars
    private Sprite starSprite;
    private HashMap<Rect, Integer> stars = new HashMap<Rect, Integer>();
    private ArrayList<Rect> freeStars = new ArrayList<Rect>();
    private final int STAR_COUNT_MAX = 100;
    private final double STAR_VELOCITY_MAX_PERC = 0.05d;
    private int STAR_VELOCITY_MAX = 0;
    private int STAR_DIAMETER_MAX = 0;

    //Rockets
    private Sprite rocketSprite;
    private ArrayList<Rect> rockets = new ArrayList<Rect>();

    //Flares
    private Sprite flareSprite;
    private ArrayList<Rect> flares = new ArrayList<Rect>();

    public GameModel(Context context) {
        this.context = context;

        createAsteroids();
    }

    public void initialize(Canvas c) {
        //Size the screen
        canvasWidth = c.getWidth();
        canvasHeight = c.getHeight();

        //Size the game field
        gameFieldWidth = canvasWidth * QUAD_X_SCALE;
        gameFieldHeight = canvasHeight * QUAD_Y_SCALE;

        //Size the player
        playerWidth = canvasWidth / 6;
        playerHeight = playerWidth;

        //Size the asteroids
        asteroidRadius = canvasWidth / 8;

        //Create the player
        playerSprite = new Sprite(context, SpriteType.PLAYER, playerWidth, playerHeight);
        playerLocation = new Rect(
                canvasWidth/2 - playerWidth/2,
                3*canvasHeight/4,
                canvasWidth/2 + playerWidth/2,
                3*canvasHeight/4 + playerHeight
        );

        //Create Asteroid
        asteroidSprite = new Sprite(context, SpriteType.ASTEROID, asteroidRadius * 2, asteroidRadius * 2);

        //Set constants
        PLAYER_VELOCITY_X_MAX = (int) (PLAYER_VELOCITY_X_MAX_PERC * canvasWidth);
        PLAYER_VELOCITY_Y_MAX = (int) (PLAYER_VELOCITY_Y_MAX_PERC * canvasHeight);
        PLAYER_ACCELERATION_X = (int) (PLAYER_ACCELERATION_X_PERC * canvasWidth);
        playerVelocityY = PLAYER_VELOCITY_Y_MAX;
        STAR_VELOCITY_MAX = (int) (canvasHeight * STAR_VELOCITY_MAX_PERC);
        STAR_DIAMETER_MAX = canvasWidth / 24;

        //Create quadrant system
        initializeQuadrants();

        //Set player quadrants
        initializePlayerQuadrants();

        //Init stars
        starSprite = new Sprite(context, SpriteType.STAR, 100, 100);
        initializeStars();

        initialized = true;
    }

    private void initializeQuadrants() {
        quadWidth = gameFieldWidth / QUAD_X_GRID_SIZE;
        quadHeight = gameFieldHeight / QUAD_Y_GRID_SIZE;

        for (int row = 0; row < QUAD_Y_GRID_SIZE; row++) {
            for (int col = 0; col < QUAD_X_GRID_SIZE; col++) {
                int left = col * quadWidth - canvasWidth;
                int top = row * quadHeight - canvasHeight;
                Rect r = new Rect(left, top, left+quadWidth, top+quadHeight);
                Quadrant q = new Quadrant(r, row, col);

                if ((row == 3 || row == 4 || row == 5) && (col == 2 || col == 3)) {
                    visibleQuads.add(q);
                }
                else {
                    emptySpawnQuads.add(q);
                    spawnQuads.add(q);
                }

                quads[row][col] = q;
            }
        }
    }

    private void initializePlayerQuadrants() {
        for (int row = 0; row < QUAD_Y_GRID_SIZE; row++) {
            for (int col = 0; col < QUAD_X_GRID_SIZE; col++) {
                if (Rect.intersects(quads[row][col].getLocation(), playerLocation)) {
                    playerQuads.add(quads[row][col]);
                }
            }
        }
    }

    private void createAsteroids() {
        for (; asteroidCount < ASTEROID_COUNT_MAX; asteroidCount++) {
            asteroidRects.add(new Rect(0, 0, 0, 0));
        }
    }

    private void initializeStars() {
        int left, top, width;

        for (int i = 0; i < STAR_COUNT_MAX; i++) {
            Float factor = rand.nextFloat();

            width = (int) ((3 * STAR_DIAMETER_MAX / 4) * factor) + (STAR_DIAMETER_MAX / 4);
            left = rand.nextInt(gameFieldWidth - width) - canvasWidth;
            top = rand.nextInt(gameFieldHeight - width) - canvasHeight;

            Rect r = new Rect(
                    left,
                    top,
                    left + width,
                    top + width
            );

            Integer in = (int) (rand.nextInt(STAR_VELOCITY_MAX) * factor) + (STAR_VELOCITY_MAX / 2);

            stars.put(r, in);
        }
    }

    public void update(double seconds, float tilt) {
        if (!isGameOver && initialized) {
            float adjustedTilt = getTiltPercent(tilt);

            //Update player based on user input
            updateVelocityFromTilt(adjustedTilt);
            updatePlayerTiltAnimation(adjustedTilt);

            //Update Positions
            updateAsteroidPositions(seconds);
            updateStarPositions(seconds);

            //Cleanup gamestate
            recalculateAsteroidQuadrants();
            assignFreeAsteroids();
            assignFreeStars();

            //Check for collisions and game over
            checkAsteroidCollisions();
            updateIsGameOver();
        }
    }

    private float getTiltPercent(float tilt) {
        float t;

        if (Math.abs(tilt) < TILT_CENTER_LIMIT) {
            return 0;
        }
        else if (tilt > 0) {
            t = Math.min(TILT_MAX, tilt);
            return (t - TILT_CENTER_LIMIT) / (TILT_MAX - TILT_CENTER_LIMIT);
        }
        else {
            t = Math.max(-TILT_MAX, tilt);
            return (t +TILT_CENTER_LIMIT) / (TILT_MAX - TILT_CENTER_LIMIT);
        }
    }

    private void updateVelocityFromTilt(float tiltPercent) {
        int targetVelocity = (int) (tiltPercent * PLAYER_VELOCITY_X_MAX);

        //Positive Velocity
        if (playerVelocityX < targetVelocity) {
            if (playerVelocityX + PLAYER_ACCELERATION_X > targetVelocity) {
                playerVelocityX = targetVelocity;
            }
            else {
                playerVelocityX += PLAYER_ACCELERATION_X;
            }

            playerVelocityX = Math.min(PLAYER_VELOCITY_X_MAX, playerVelocityX);
        }

        //Negative Velocity
        else if (playerVelocityX > targetVelocity) {
            if (playerVelocityX - PLAYER_ACCELERATION_X < targetVelocity) {
                playerVelocityX = targetVelocity;
            }
            else {
                playerVelocityX -= PLAYER_ACCELERATION_X;
            }

            playerVelocityX = Math.max(-PLAYER_VELOCITY_X_MAX, playerVelocityX);
        }
    }

    private void updatePlayerTiltAnimation(float tiltPercent) {
        if(playerSprite != null) {
            int frameCount = playerSprite.getSpriteType().getWCount() - 1;

            if (tiltPercent > 0) {
                playerSprite.showAnimationFrame(Animation.TILT_RIGHT, (int)(frameCount * tiltPercent));
            }
            else if (tiltPercent < 0) {
                playerSprite.showAnimationFrame(Animation.TILT_LEFT, (int)(frameCount * -tiltPercent));
            }
            else {
                playerSprite.showAnimationFrame(Animation.TILT_RIGHT, 0);
            }
        }
    }

    private void updateAsteroidPositions(double seconds) {
        for (Rect r : asteroidRects) {
            r.top += playerVelocityY * seconds;
            r.bottom += playerVelocityY * seconds;
            r.left -= playerVelocityX * seconds;
            r.right -= playerVelocityX * seconds;
        }
    }

    private void updateStarPositions(double seconds) {
        for (Map.Entry<Rect, Integer> e : stars.entrySet()) {
            e.getKey().left -= playerVelocityX * seconds;
            e.getKey().right -= playerVelocityX * seconds;
            e.getKey().top += (int) (e.getValue() * seconds);
            e.getKey().bottom += (int) (e.getValue() * seconds);

            if (e.getKey().top > canvasHeight
                    || (e.getKey().right < ((QUAD_X_SCALE - 1) / 2) * -canvasWidth)
                    || (e.getKey().left > (((QUAD_X_SCALE - 1) / 2) * canvasWidth) + canvasWidth)) {
                freeStars.add(e.getKey());
            }
        }
    }

    private int getQuadrantRowFromPoint(int p) {
        int ret = (p / quadHeight) + ((QUAD_Y_SCALE - 1) * QUAD_Y_DENSITY);

        return ret;
    }

    private int getQuadrantColFromPoint(int p) {
        int ret = (p / quadWidth) + (((QUAD_X_SCALE - 1) / 2) * QUAD_X_DENSITY);

        return ret;
    }

    private void recalculateAsteroidQuadrants() {
        //Clear free rects list
        freeAsteroidRects.clear();

        //Clear all asteroids from quadrants
        for (int row = 0; row < QUAD_Y_GRID_SIZE; row++) {
            for (int col = 0; col < QUAD_X_GRID_SIZE; col++) {
                quads[row][col].clearAsteroids();
            }
        }

        //TODO go through each rectangle and check what quadrant they belong to based on the top/bot/left/right boundaries
//        for (Rect r :  asteroidRects) {
//            boolean rectAdded = false;
//
//            int left = getQuadrantColFromPoint(r.left);
//            int top = getQuadrantRowFromPoint(r.top);
//            int right = getQuadrantColFromPoint(r.right);
//            int bottom = getQuadrantRowFromPoint(r.bottom);
//
//            for (int row = top; row <= bottom && row < QUAD_Y_GRID_SIZE && row > 0; row++) {
//                for (int col = left; col <= right && col < QUAD_X_GRID_SIZE && col > 0; col++) {
//                    quads[row][col].addAsteroid(r, asteroidSprite);
//                    rectAdded = true;
//                }
//            }
//
//            if (!rectAdded) {
//                freeAsteroidRects.add(r);
//            }
//        }

        //TODO REPLACE THIS PART
        //Re-assign rects to quadrants
        for (Rect r : asteroidRects) {
            boolean rectAdded = false;

            for (int row = 0; row < QUAD_Y_GRID_SIZE; row++) {
                for (int col = 0; col < QUAD_X_GRID_SIZE; col++) {
                    if (Rect.intersects(quads[row][col].getLocation(), r)) {
                        rectAdded = true;
                        quads[row][col].addAsteroid(r, asteroidSprite);
                    }
                }
            }

            if (!rectAdded) {
                freeAsteroidRects.add(r);
            }
        }

        //Update emptySpawnQuads
        emptySpawnQuads.clear();
        for (Quadrant q : spawnQuads) {
            if (q.isEmptyAsteroids()) {
                emptySpawnQuads.add(q);
            }
        }
    }

    private void assignFreeAsteroids() {
        while (freeAsteroidRects.size() > 0 && emptySpawnQuads.size() > 0) {

            //Pick random quadrant
            Quadrant q = emptySpawnQuads.get(rand.nextInt(emptySpawnQuads.size()));

            //TODO FIX OFFSET FOR NEGATIVE EMPTY QUADS
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
            q.addAsteroid(r, asteroidSprite);

            //Remove quadrant from empty list and remove
            freeAsteroidRects.remove(freeAsteroidRects.size() - 1);
            emptySpawnQuads.remove(q);
        }
    }

    private void assignFreeStars() {
        while (freeStars.size() > 0) {
            //Random spawn quad
            int index = rand.nextInt(spawnQuads.size());
            Rect qRect = spawnQuads.get(index).getLocation();

            //New values
            Rect sRect = freeStars.get(freeStars.size() - 1);

            int width = sRect.right - sRect.left;
            int left = rand.nextInt(quadWidth - width) + qRect.left;
            int top = rand.nextInt(quadHeight - width) + qRect.top;

            sRect.left = left;
            sRect.top = top;
            sRect.right = left + width;
            sRect.bottom = top + width;

            freeStars.remove(freeStars.size() - 1);
        }
    }

    private void checkAsteroidCollisions() {
        for (Quadrant q : playerQuads) {
            if (!q.isEmptyAsteroids()) {
                for (Rect r : q.getAsteroids().keySet()) {
                    if (isCollision(r, asteroidSprite, playerLocation, playerSprite)) {
                        playerHealth = 0;
                        return;
                    }
                }
            }
        }
    }

    private boolean isCollision(Rect r1, Sprite s1, Rect r2, Sprite s2) {
        if (Rect.intersects(r1, r2)) {
            return isPixelCollision(r1, s1, r2, s2);
        }

        return false;
    }

    private boolean isPixelCollision(Rect r1, Sprite s1, Rect r2, Sprite s2) {
        int left = Math.max(r1.left, r2.left);
        int top = Math.max(r1.top, r2.top);
        int right = Math.min(r1.right, r2.right);
        int bottom = Math.min(r1.bottom, r2.bottom);

        for (int i = left; i < right; i++) {
            for (int j = top; j < bottom; j++) {
                if (s1.pixelFilled(i - r1.left, j - r1.top) && s2.pixelFilled(i - r2.left, j - r2.top)) {
                    return true;
                }
            }
        }

        return false;
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

    public boolean isInitialized() {
        return initialized;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public Rect getPlayerLocation() {
        return playerLocation;
    }

    public ArrayList<Quadrant> getVisibleQuads() {
        return visibleQuads;
    }

    public Sprite getStarSprite() {
        return starSprite;
    }

    public HashMap<Rect, Integer> getStars() {
        return stars;
    }

    public void drawToCanvas(Canvas c) {
        if (!initialized) {
            initialize(c);
        }

        if (c != null) {
            //Clear Canvas
            c.drawColor(Color.BLACK);

            //Draw Player Ship
            if (playerSprite != null) {
                c.drawBitmap(playerSprite.getBitmap(), playerSprite.getFrameRect(), playerLocation, null);
            }

            //Draw Asteroids
            for (Quadrant q : visibleQuads) {
                for (Map.Entry<Rect, Sprite> e : q.getAsteroids().entrySet()) {
                    c.drawBitmap(e.getValue().getBitmap(), e.getValue().getFrameRect(), e.getKey(), null);
                }
            }
        }
    }
}

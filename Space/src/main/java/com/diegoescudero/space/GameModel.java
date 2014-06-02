package com.diegoescudero.space;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameModel {
    //General
    private Context context;
    private boolean isGameOver = false;
    private boolean initialized = false;
    private Random rand = new Random();
    private long lastUpdateDeltaMS = 0;
    private long lastUpdateTimeMS = SystemClock.uptimeMillis();

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
    private final float TILT_MAX = 3.0f;

    //Game Screen
    private int gameFieldWidth = 0;
    private int gameFieldHeight = 0;
    private int canvasWidth = 0;
    private int canvasHeight = 0;

    //Player
    private int playerHealth = 100;
    private final int PLAYER_SIZE_FACTOR = 6;
    private final double PLAYER_VELOCITY_X_MAX_PERC = 1.0d; //100% of screen in 1 second
    private final double PLAYER_VELOCITY_Y_MAX_PERC = 0.75d; //60% of screen in 1 second
    private int PLAYER_VELOCITY_X_MAX = 0;
    private int PLAYER_VELOCITY_Y_MAX = 0;
    private double playerVelocityX = 0;
    private double playerVelocityY = 0;
    private Sprite playerSprite = null;
    private SpritePosition playerPosition;

    //Asteroids
    private Sprite asteroidSprite;
    private ArrayList<SpritePosition> asteroidPositions = new ArrayList<SpritePosition>();
    private ArrayList<SpritePosition> freeAsteroidPositions = new ArrayList<SpritePosition>();
    private int ASTEROID_COUNT_MAX = 10;
    private int asteroidCount = 0;
    private int asteroidRadius = 0;

    //Stars
    private Sprite starSprite;
    private HashMap<SpritePosition, Integer> stars = new HashMap<SpritePosition, Integer>();
    private ArrayList<SpritePosition> freeStars = new ArrayList<SpritePosition>();
    private final int STAR_COUNT_MAX = 100;
    private final double STAR_VELOCITY_MAX_PERC = 0.05d;
    private int STAR_VELOCITY_MAX = 0;
    private int STAR_DIAMETER_MAX = 0;

    //Missile
    private final int MISSILE_SIZE_FACTOR = 6;
    private final double MISSILE_VELOCITY_X_MAX_PERC = 0.90d;
    private final double MISSILE_VELOCITY_Y_MAX_PERC = 0.35d;
    private double MISSILE_VELOCITY_X_MAX;
    private double MISSILE_VELOCITY_Y_MAX;
    private Sprite missileSprite;
    private SpritePosition missilePosition;
    private boolean missileActive = false;
    private long lastMissileMS = 0;

    //Flares
    private final int FLARE_SIZE_FACTOR = 8;
    private final double FLARE_VELOCITY_MAX_PERC = 0.1d;
    private double FLARE_VELOCITY_MAX;
    private Sprite flareSprite;
    private SpritePosition flarePosition;
    private boolean flareActive = false;

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
        int pWidth = canvasWidth / PLAYER_SIZE_FACTOR;
        int pLeft = (canvasWidth / 2) - (pWidth / 2);
        int pTop = canvasHeight / 2;
        playerSprite = new Sprite(context, SpriteType.PLAYER, pWidth, pWidth);
        playerPosition = new SpritePosition(pLeft, pTop, pWidth, pWidth);

        //Size the asteroids
        asteroidRadius = canvasWidth / 8;

        //Create Asteroid
        asteroidSprite = new Sprite(context, SpriteType.ASTEROID, asteroidRadius * 2, asteroidRadius * 2);

        //Set constants
        PLAYER_VELOCITY_X_MAX = (int) (PLAYER_VELOCITY_X_MAX_PERC * canvasWidth);
        PLAYER_VELOCITY_Y_MAX = (int) (PLAYER_VELOCITY_Y_MAX_PERC * canvasHeight);
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

        //Flare and Missile Velocities
        FLARE_VELOCITY_MAX = FLARE_VELOCITY_MAX_PERC * canvasHeight;
        MISSILE_VELOCITY_X_MAX = MISSILE_VELOCITY_X_MAX_PERC * canvasWidth;
        MISSILE_VELOCITY_Y_MAX = MISSILE_VELOCITY_Y_MAX_PERC * canvasHeight;

        //Flare and Missile Sprites
        int flareWidth = canvasWidth / FLARE_SIZE_FACTOR;
        flareSprite = new Sprite(context, SpriteType.FLARE, flareWidth, flareWidth);
        int missileWidth = canvasWidth / MISSILE_SIZE_FACTOR;
        missileSprite = new Sprite(context, SpriteType.MISSILE, missileWidth, missileWidth);

        //Flare and Missile initial Positions
        flarePosition = new SpritePosition(canvasWidth, canvasHeight, flareWidth, flareWidth);
        missilePosition = new SpritePosition(canvasWidth, canvasHeight, missileWidth, missileWidth);

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
                if (Rect.intersects(quads[row][col].getLocation(), playerPosition.rect())) {
                    playerQuads.add(quads[row][col]);
                }
            }
        }
    }

    private void createAsteroids() {
        for (; asteroidCount < ASTEROID_COUNT_MAX; asteroidCount++) {
            asteroidPositions.add(new SpritePosition(0, 0, 0, 0));
        }
    }

    private void initializeStars() {
        int left, top, width;

        for (int i = 0; i < STAR_COUNT_MAX; i++) {
            Float factor = rand.nextFloat();

            width = (int) ((3 * STAR_DIAMETER_MAX / 4) * factor) + (STAR_DIAMETER_MAX / 4);
            left = rand.nextInt(gameFieldWidth - width) - canvasWidth;
            top = rand.nextInt(gameFieldHeight - width) - canvasHeight;

            SpritePosition p = new SpritePosition(left, top, width, width);

            Integer in = (int) (rand.nextInt(STAR_VELOCITY_MAX) * factor) + (STAR_VELOCITY_MAX / 2);

            stars.put(p, in);
        }
    }

    public void update(double seconds, float tilt, Gesture gesture) {
        if (!isGameOver && initialized) {
            float adjustedTilt = getTiltPercent(tilt);
            long now = SystemClock.uptimeMillis();
            lastUpdateDeltaMS = now - lastUpdateTimeMS;
            lastUpdateTimeMS = now;

            //Update player based on user input
            updateVelocityFromTilt(adjustedTilt);
            updatePlayerTiltAnimation(adjustedTilt);

            //Update Positions
            updateAsteroidPositions(seconds);
            updateStarPositions(seconds);
            updateFlarePosition(seconds);
            updateMissilePosition(seconds);

            //Cleanup and add to gamestate
            recalculateAsteroidQuadrants();
            assignFreeAsteroids();
            assignFreeStars();
            createFlare(gesture);
            createMissile(gesture);

            //Check for collisions and game over
            checkAsteroidCollisions();
            checkMissileCollision();
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
        double targetVelocity = tiltPercent * PLAYER_VELOCITY_X_MAX;
        playerVelocityX = targetVelocity;

//        //Positive Velocity
//        if (playerVelocityX < targetVelocity) {
//            playerVelocityX += PLAYER_ACCELERATION_X;
//            playerVelocityX = Math.min(PLAYER_VELOCITY_X_MAX, playerVelocityX);
//        }
//
//        //Negative Velocity
//        else if (playerVelocityX > targetVelocity) {
//            playerVelocityX -= PLAYER_ACCELERATION_X;
//            playerVelocityX = Math.max(-PLAYER_VELOCITY_X_MAX, playerVelocityX);
//        }
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
        for (SpritePosition p : asteroidPositions) {
            p.changeX(-playerVelocityX * seconds);
            p.changeY(playerVelocityY * seconds);
        }
    }

    private void updateStarPositions(double seconds) {
        for (Map.Entry<SpritePosition, Integer> e : stars.entrySet()) {
            SpritePosition p = e.getKey();
            p.changeX(-playerVelocityX * seconds);
            p.changeY(e.getValue() * seconds);

            if (p.rect().top > canvasHeight
                    || (p.rect().right < ((QUAD_X_SCALE - 1) / 2) * -canvasWidth)
                    || (p.rect().left > (((QUAD_X_SCALE - 1) / 2) * canvasWidth) + canvasWidth)) {
                freeStars.add(e.getKey());
            }
        }
    }

    private void updateFlarePosition(double seconds) {
        flarePosition.changeX(-playerVelocityX * seconds);
        flarePosition.changeY(FLARE_VELOCITY_MAX * seconds);

        if (flarePosition.getTop() > canvasHeight) {
            flareActive = false;
        }
    }

    private void seekToPosition(double seconds, double speedX, double speedY, SpritePosition source, SpritePosition dest) {
        double xDiff = source.getCenterX() - dest.getCenterX();
        //Object needs to move left
        if (xDiff > 0) {
            if (speedX * seconds > xDiff) {
                source.setCenterX(dest.getCenterX());
            }
            else {
                source.changeX(-speedX * seconds);
            }
        }
        //Object needs to move right
        else if (xDiff < 0) {
            if (speedX * seconds > Math.abs(xDiff)) {
                source.setCenterX(dest.getCenterX());
            }
            else {
                source.changeX(speedX * seconds);
            }
        }

        //Move y axis
        double yDiff = source.getCenterY() - dest.getCenterY();
        //Object needs to move up
        if (yDiff > 0) {
            if (speedY * seconds > yDiff) {
                source.setCenterY(dest.getCenterY());
            }
            else {
                source.changeY(-speedY * seconds);
            }
        }
        //Object needs to move down
        else if (yDiff < 0) {
            if (speedY * seconds > Math.abs(yDiff)) {
                source.setCenterY(dest.getCenterY());
            }
            else {
                source.changeY(speedY * seconds);
            }
        }
    }

    private void updateMissilePosition(double seconds) {
        if (missileActive) {
            //Account for player movement
            missilePosition.changeX(-playerVelocityX * seconds);

            //Seek to target
            if (flareActive) {
                seekToPosition(seconds, MISSILE_VELOCITY_X_MAX, MISSILE_VELOCITY_Y_MAX, missilePosition, flarePosition);
            }
            else {
                seekToPosition(seconds, MISSILE_VELOCITY_X_MAX, MISSILE_VELOCITY_Y_MAX, missilePosition, playerPosition);
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
        freeAsteroidPositions.clear();

        //Clear all asteroids from quadrants
        for (int row = 0; row < QUAD_Y_GRID_SIZE; row++) {
            for (int col = 0; col < QUAD_X_GRID_SIZE; col++) {
                quads[row][col].clearAsteroids();
            }
        }

        //TODO go through each rectangle and check what quadrant they belong to based on the top/bot/left/right boundaries
//        for (Rect r :  asteroidPositions) {
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
//                freeAsteroidPositions.add(r);
//            }
//        }

        //TODO REPLACE THIS PART WITH ABOVE
        //Re-assign rects to quadrants
        for (SpritePosition p : asteroidPositions) {
            boolean rectAdded = false;

            for (int row = 0; row < QUAD_Y_GRID_SIZE; row++) {
                for (int col = 0; col < QUAD_X_GRID_SIZE; col++) {
                    if (Rect.intersects(quads[row][col].getLocation(), p.rect())) {
                        rectAdded = true;
                        quads[row][col].addAsteroid(p);
                    }
                }
            }

            if (!rectAdded) {
                freeAsteroidPositions.add(p);
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
        while (freeAsteroidPositions.size() > 0 && emptySpawnQuads.size() > 0) {

            //Pick random quadrant
            Quadrant q = emptySpawnQuads.get(rand.nextInt(emptySpawnQuads.size()));

            //TODO FIX OFFSET FOR NEGATIVE EMPTY QUADS
            //Calculate random asteroid position in quadrant
            Rect qRect = q.getLocation();
            int left = rand.nextInt(quadWidth - asteroidRadius * 2) + qRect.left;
            int top = rand.nextInt(quadHeight - asteroidRadius * 2) + qRect.top;

            //Get asteroid rect to transfer to quadrant
            SpritePosition p = freeAsteroidPositions.get(freeAsteroidPositions.size() - 1);

            //Place rect
            p.setLeft(left);
            p.setTop(top);
            p.setWidth(asteroidRadius * 2);
            p.setHeight(asteroidRadius * 2);
//            p.recalculateRect();

            //Add asteroid data to quadrant
            q.addAsteroid(p);

            //Remove quadrant from empty list and remove
            freeAsteroidPositions.remove(freeAsteroidPositions.size() - 1);
            emptySpawnQuads.remove(q);
        }
    }

    private void assignFreeStars() {
        while (freeStars.size() > 0) {
            //Random spawn quad
            int index = rand.nextInt(spawnQuads.size());
            Rect qRect = spawnQuads.get(index).getLocation();

            //New values
            SpritePosition p = freeStars.get(freeStars.size() - 1);

            int left = rand.nextInt(quadWidth - p.getWidth()) + qRect.left;
            int top = rand.nextInt(quadHeight - p.getHeight()) + qRect.top;

            p.setLeft(left);
            p.setTop(top);

            freeStars.remove(freeStars.size() - 1);
        }
    }

    private void createFlare(Gesture gesture) {
        if (gesture == Gesture.SWIPE_DOWN && !flareActive) {
            double left = playerPosition.getLeft() + (playerPosition.getWidth() / 2) - (flarePosition.getWidth() / 2);
            double top = playerPosition.getTop() + playerPosition.getHeight();

            flarePosition.setLeft(left);
            flarePosition.setTop(top);

            flareActive = true;
        }
    }

    private void createMissile(Gesture gesture) {
        lastMissileMS += Math.abs(lastUpdateDeltaMS);
        if (!missileActive) {
            boolean create = false;

            if (lastMissileMS > 3000) {
                create = rand.nextBoolean();
                lastMissileMS = 0;
            }

            if (gesture == Gesture.SWIPE_UP || create) {
                double left = playerPosition.getLeft() + (playerPosition.getWidth() / 2) - (missilePosition.getWidth() / 2);
                double top = canvasHeight;

                missilePosition.setLeft(left);
                missilePosition.setTop(top);

                missileActive = true;
            }
        }
    }

    private void checkAsteroidCollisions() {
        for (Quadrant q : playerQuads) {
            if (!q.isEmptyAsteroids()) {
                for (SpritePosition p : q.getAsteroids()) {
                    if (isCollision(p.rect(), asteroidSprite, playerPosition.rect(), playerSprite)) {
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

    private void checkMissileCollision() {
        //Missile -> Flare (reset their positions off screen)
        if (isCollision(missilePosition.rect(), missileSprite, flarePosition.rect(), flareSprite)) {
            missilePosition.setLeft(canvasWidth);
            missilePosition.setTop(canvasHeight);
            flarePosition.setLeft(canvasWidth);
            flarePosition.setTop(canvasHeight);
            missileActive = false;
            flareActive = false;
        }
        //Missile -> Player (set health to 0)
        else if (isCollision(missilePosition.rect(), missileSprite, playerPosition.rect(), playerSprite)) {
            playerHealth = 0;
            missileActive = false;
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

    public boolean isInitialized() {
        return initialized;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public SpritePosition getPlayerPosition() {
        return playerPosition;
    }

    public ArrayList<Quadrant> getVisibleQuads() {
        return visibleQuads;
    }

    public Sprite getStarSprite() {
        return starSprite;
    }

    public Sprite getAsteroidSprite() {
        return asteroidSprite;
    }

    public Sprite getFlareSprite() {
        return flareSprite;
    }

    public Sprite getMissileSprite() {
        return missileSprite;
    }

    public SpritePosition getFlarePosition() {
        return flarePosition;
    }

    public SpritePosition getMissilePosition() {
        return missilePosition;
    }

    public HashMap<SpritePosition, Integer> getStars() {
        return stars;
    }

//    public void drawToCanvas(Canvas c) {
//        if (!initialized) {
//            initialize(c);
//        }
//
//        if (c != null) {
//            //Clear Canvas
//            c.drawColor(Color.BLACK);
//
//            //Draw Player Ship
//            if (playerSprite != null) {
//                c.drawBitmap(playerSprite.getBitmap(), playerSprite.getFrameRect(), playerLocation, null);
//            }
//
//            //Draw Asteroids
//            for (Quadrant q : visibleQuads) {
//                for (Map.Entry<Rect, Sprite> e : q.getAsteroids().entrySet()) {
//                    c.drawBitmap(e.getValue().getBitmap(), e.getValue().getFrameRect(), e.getKey(), null);
//                }
//            }
//        }
//    }
}

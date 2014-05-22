package com.diegoescudero.space;

import android.graphics.Rect;

import java.util.HashMap;

public class Quadrant {
    private int row;
    private int col;
    private Rect location;
    private HashMap<Rect, Sprite> asteroids = new HashMap<Rect, Sprite>();

    public Quadrant(Rect r, int row, int col) {
        this.location = r;
        this.row = row;
        this.col = col;
    }

    public Rect getLocation() {
        return location;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public HashMap<Rect, Sprite> getAsteroids() {
        return asteroids;
    }

    public void addAsteroid(Rect r, Sprite s) {
        asteroids.put(r, s);
    }

    public void clearAsteroids() {
        asteroids.clear();
    }

    public boolean containsSprite(Rect r) {
        return asteroids.containsKey(r);
    }

    public boolean isEmptyAsteroids() {
        return asteroids.size() == 0;
    }
}

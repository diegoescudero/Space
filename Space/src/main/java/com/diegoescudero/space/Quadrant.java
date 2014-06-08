package com.diegoescudero.space;

import android.graphics.Rect;

import java.util.HashSet;

public class Quadrant {
    private int row;
    private int col;
    private Rect location;
    private HashSet<SpritePosition> asteroids = new HashSet<SpritePosition>();

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

    public HashSet<SpritePosition> getAsteroids() {
        return asteroids;
    }

    public void addAsteroid(SpritePosition p) {
        asteroids.add(p);
    }

    public void clearAsteroids() {
        asteroids.clear();
    }

    public boolean isEmptyAsteroids() {
        return asteroids.size() == 0;
    }
}

package com.diegoescudero.space;

import android.graphics.Rect;

import java.util.HashMap;
import java.util.HashSet;

public class Quadrant {
    private int row;
    private int col;
    private Rect location;
    private HashMap<Rect, Sprite> sprites = new HashMap<Rect, Sprite>();

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

    public HashMap<Rect, Sprite> getSprites() {
        return sprites;
    }

    public void addSprite(Rect r, Sprite s) {
        sprites.put(r, s);
    }

    public void removeSprite(Rect r) {
        sprites.remove(r);
    }

    public void clearSprites() {
        sprites.clear();
    }

    public boolean containsSprite(Rect r) {
        return sprites.containsKey(r);
    }

    public boolean isEmpty() {
        return sprites.size() == 0;
    }
}

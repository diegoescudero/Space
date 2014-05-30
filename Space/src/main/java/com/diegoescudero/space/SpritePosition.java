package com.diegoescudero.space;

import android.graphics.Rect;

public class SpritePosition {
    private double left;
    private double top;
    private int width;
    private int height;

    private Rect currentPosition;

    public SpritePosition(double left, double top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;

        currentPosition = new Rect((int)Math.round(left), (int)Math.round(top), (int)Math.round(left + width), (int)Math.round(top + height));
    }

    private void recalculateRect() {
        currentPosition.left = (int)Math.round(left);
        currentPosition.top = (int)Math.round(top);
        currentPosition.right = (int)Math.round(left + width);
        currentPosition.bottom = (int)Math.round(top + height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;

        currentPosition.left = (int)Math.round(left);
        currentPosition.right = (int)Math.round(left + width);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;

        currentPosition.top = (int)Math.round(top);
        currentPosition.bottom = (int)Math.round(top + height);
    }

    public void setLeft(double p) {
        left = p;

        currentPosition.left = (int)Math.round(left);
        currentPosition.right = (int)Math.round(left + width);
    }

    public void setTop(double p) {
        top = p;

        currentPosition.top = (int)Math.round(top);
        currentPosition.bottom = (int)Math.round(top + height);
    }

    public void changeX(double p) {
        left += p;

        int pixelLeft = (int)Math.round(left);
        currentPosition.left = pixelLeft;
        currentPosition.right = pixelLeft + width;
    }

    public void changeY(double p) {
        top += p;

        int pixelTop = (int)Math.round(top);
        currentPosition.top = pixelTop;
        currentPosition.bottom = pixelTop + height;
    }

    public Rect rect() {
        return currentPosition;
    }
}

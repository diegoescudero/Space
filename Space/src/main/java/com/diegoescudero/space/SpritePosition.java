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

    private void recalculateRectX() {
        currentPosition.left = (int)Math.round(left);
        currentPosition.right = (int)Math.round(left + width);
    }

    private void recalculateRectY() {
        currentPosition.top = (int)Math.round(top);
        currentPosition.bottom = (int)Math.round(top + height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;

        recalculateRectX();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;

        recalculateRectY();
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double p) {
        left = p;

        recalculateRectX();
    }

    public double getTop() {
        return top;
    }

    public void setTop(double p) {
        top = p;

        recalculateRectY();
    }

    public void changeX(double p) {
        left += p;

        recalculateRectX();
    }

    public void changeY(double p) {
        top += p;

        recalculateRectY();
    }

    public double getCenterX() {
        return left + width / 2;
    }

    public double getCenterY() {
        return top + height / 2;
    }

    public void setCenterX(double center) {
        left = center - (width / 2);

        recalculateRectX();
    }

    public void setCenterY(double center) {
        top = center - (height / 2);

        recalculateRectY();
    }

    public Rect rect() {
        return currentPosition;
    }
}

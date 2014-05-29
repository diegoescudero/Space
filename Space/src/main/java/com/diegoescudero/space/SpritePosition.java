package com.diegoescudero.space;

public class SpritePosition {
    double left;
    double top;
    int width;

    public SpritePosition(double left, double top, int width) {
        this.left = left;
        this.top = top;
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setLeft(double p) {
        left = p;
    }

    public double getLeft() {
        return left;
    }

    public void setTop(double p) {
        top = p;
    }

    public double getTop() {
        return top;
    }

    public void changeX(double p) {
        left += p;
    }

    public void changeY(double p) {
        top += p;
    }
}

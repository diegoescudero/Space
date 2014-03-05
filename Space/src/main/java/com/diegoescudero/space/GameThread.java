package com.diegoescudero.space;

public class GameThread extends Thread {
    private boolean isRunning;
    private final int FPS = 30;

    public GameThread() {
        this.isRunning = isRunning;
    }

    public void start() {
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }
}

package com.diegoescudero.space;

import java.util.Arrays;
import java.util.HashSet;

public enum SpriteType {
    PLAYER (R.drawable.player, new HashSet<Animation>(Arrays.asList(Animation.TILT_LEFT, Animation.TILT_RIGHT)), 9, 2),
    ASTEROID (R.drawable.asteroid, new HashSet<Animation>(Arrays.asList(Animation.DEATH)), 1, 1),
    MISSILE (R.drawable.missile, new HashSet<Animation>(Arrays.asList(Animation.DEATH)), 1, 1),
    STAR (R.drawable.sun, null, 1, 1);

    private int resource;
    private HashSet<Animation> animations;
    private int wCount;
    private int hCount;

    private SpriteType(int resource, HashSet<Animation> animations, int wCount, int hCount) {
        this.resource = resource;
        this.animations = animations;
        this.wCount = wCount;
        this.hCount = hCount;
    }

    public int getResource() {
        return resource;
    }

    public int getWCount() {
        return wCount;
    }

    public int getHCount() {
        return hCount;
    }

    public HashSet<Animation> getAnimations() {
        return animations;
    }

    public boolean containsAnimation(Animation a) {
        if (animations.contains(a)) {
            return true;
        }

        return false;
    }
}

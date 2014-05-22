package com.diegoescudero.space;

import java.util.Arrays;
import java.util.List;

public enum Animation {
    TILT_LEFT,
    TILT_RIGHT,
    DEATH;

    //Make sure in same order as above
    public static List<Animation> getAllAnimations() {
        return Arrays.asList(TILT_LEFT, TILT_RIGHT, DEATH);
    }
}

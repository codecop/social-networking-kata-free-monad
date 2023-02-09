package org.codecop.socialnetworking;

/**
 * A side effect, impure.
 */
public class Timer {

    private static long currentTime = 0;

    static long time() {
        return ++currentTime;
    }

}

package org.codecop.socialnetworking;

/**
 * A side effect, impure.
 */
public class Timer {

    private static long currentTime = 0;

    static Long time() {
        return ++currentTime;
    }

}

class FreeTimer {

    static Free<Long> time() {
        return new FreeTime();
    }

    static class FreeTime extends Free<Long> {
    }

}

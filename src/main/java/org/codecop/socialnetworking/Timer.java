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

interface TimerOps {

    static Unrestricted<DslCommand<Long>> time() {
        return Unrestricted.liftF(new Time());
    }

    static class Time extends DslCommand<Long> {
    }

}

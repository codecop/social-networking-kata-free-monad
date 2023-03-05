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

    // "smart" constructors
    static Free<DomainOps, Long> time() {
        return Free.liftM(new GetTime());
    }

    static class GetTime extends DomainOps {
    }

}

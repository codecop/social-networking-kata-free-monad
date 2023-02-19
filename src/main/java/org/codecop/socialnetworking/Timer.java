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

    static Free<DslCommand<Long>> time() {
        return Free.liftF(new GetTime());
    }

    static class GetTime extends DslCommand<Long> {
    }

}

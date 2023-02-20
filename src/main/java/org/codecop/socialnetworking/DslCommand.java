package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * The command is only a functor.
 */
abstract class DslCommand implements Transformable {

    @Override
    public <T, U> DslCommand map(Function<? super T, ? extends U> mapper) {
        // only values can be mapped and translation creates values then in the interpret
        throw new UnsupportedOperationException("Commands cannot be mapped, only results");
    }

    @Override
    public String toString() {
        // debugging
        return "Command " + getClass().getSimpleName();
    }

}

class DslResult extends DslCommand {

    public static DslCommand of(Object value) { // "pure"
        return new DslResult(value);
    }

    public static DslCommand nil() {
        return new DslResult(null);
    }

    final Object value;

    private DslResult() {
        this(null);
    }

    private DslResult(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        // debugging
        return super.toString() + " with " + value;
    }
    
    @Override
    public <T, U> DslCommand map(Function<? super T, ? extends U> mapper) {
        return of(mapper.apply((T) value));
        // OK: only used inside Free
    }

}

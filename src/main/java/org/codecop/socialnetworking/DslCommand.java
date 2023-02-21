package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * The command is only a functor. Must have flatMap for reduce.
 */
abstract class DslCommand implements Transformable {

    @Override
    public <T, U> DslCommand map(Function<? super T, ? extends U> mapper) {
        throw new UnsupportedOperationException("DslCommand cannot be mapped, only results");
    }

    @Override
    public <T> DslCommand flatMap(Function<? super T, ? extends Transformable> mapper) {
        throw new UnsupportedOperationException("DslCommand cannot be mapped, only results");
    }

    @Override
    public String toString() {
        // debugging
        return "DslCommand " + getClass().getSimpleName();
    }

}

class DslResult/*<T>*/ extends DslCommand {

    public static /*<T>*/ DslCommand of(Object value) {
        return new DslResult(value);
    }

    public static DslCommand nil() {
        return new DslResult(null);
    }

    final /*<T>*/ Object value;

    private DslResult() {
        this(null);
    }

    private DslResult(Object value) {
        this.value = value;
    }

    @Override
    public <T, U> DslCommand map(Function<? super T, ? extends U> mapper) {
        // only used inside Free
        return of(mapper.apply((T) value));
    }

    @Override
    public <T> DslCommand flatMap(Function<? super T, ? extends Transformable> mapper) {
        // only used inside Free
        return (DslCommand) mapper.apply((T) value);
    }

    @Override
    public String toString() {
        // debugging
        return super.toString() + " with " + value;
    }

}

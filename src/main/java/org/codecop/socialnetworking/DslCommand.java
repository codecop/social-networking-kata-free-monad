package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * The command is only a functor.
 */
abstract class DslCommand implements Transformable {

    @Override
    public <T, U> DslCommand map(Function<? super T, ? extends U> mapper) {
        throw new UnsupportedOperationException("Commands cannot be mapped, only results");
    }

    @Override
    public <T> DslCommand flatMap(Function<? super T, ? extends Transformable> mapper) {
        throw new UnsupportedOperationException("Commands cannot be mapped, only results");
    }

    @Override
    public String toString() {
        // debugging
        return "Command " + getClass().getSimpleName();
    }

}

class DslResult extends DslCommand {

    public static DslCommand of(Object value) {
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
    public <T, U> DslCommand map(Function<? super T, ? extends U> mapper) {
        return of(mapper.apply((T) value));
        // OK: only used inside Free
    }

    @Override
    public <T> DslCommand flatMap(Function<? super T, ? extends Transformable> mapper) {
        return (DslCommand) mapper.apply((T) value);
        // TODO restrict usage
    }

    @Override
    public String toString() {
        // debugging
        return super.toString() + " with " + value;
    }

}

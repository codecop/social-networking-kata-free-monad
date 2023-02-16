package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * The command is only a functor.
 */
abstract class DslCommand<T> implements Transformable<T> {

    @Override
    public <U> DslCommand<U> map(Function<? super T, ? extends U> mapper) {
        // only values can be mapped and translation creates values then in the interpret
        throw new UnsupportedOperationException("Commands cannot be mapped, only results");
    }

    public <U> DslCommand<U> flatMap(Function<? super T, DslCommand<? extends U>> mapper) {
        throw new UnsupportedOperationException("Commands cannot be mapped, only results");
    }

    @Override
    public String toString() {
        // debugging
        return "Command " + getClass().getSimpleName();
    }

}

class DslResult<T> extends DslCommand<T> {

    public static <T> DslCommand<T> of(T value) { // "pure"
        return new DslResult<>(value);
    }

    public static DslCommand<Void> nil() {
        return new DslResult<>(null);
    }

    final T value;

    private DslResult() {
        this(null);
    }

    private DslResult(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        // debugging
        return super.toString() + " with " + value;
    }
    
    @Override
    public <U> DslCommand<U> map(Function<? super T, ? extends U> mapper) {
        return of(mapper.apply(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> DslCommand<U> flatMap(Function<? super T, DslCommand<? extends U>> mapper) {
        return (DslCommand<U>) mapper.apply(value);
    }
}

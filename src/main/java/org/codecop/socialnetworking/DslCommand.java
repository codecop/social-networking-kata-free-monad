package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * The command is only a functor.
 */
abstract class DslCommand<T> implements Transformable<T> {

    public static <T> DslCommand<T> of(T value) { // "pure"
        return new DslCommand<T>(value) {
        };
    }

    public static DslCommand<Void> nil() {
        return new DslCommand<Void>(null) {
        };
    }

    final T value;

    protected DslCommand() {
        this(null);
    }

    protected DslCommand(T value) {
        this.value = value;
    }

    @Override
    public <U> DslCommand<U> map(Function<? super T, ? extends U> mapper) {
        return of(mapper.apply(value));
    }

    @SuppressWarnings("unchecked")
    public <U> DslCommand<U> flatMap(Function<? super T, DslCommand<? extends U>> mapper) {
        return (DslCommand<U>) mapper.apply(value);
    }

}

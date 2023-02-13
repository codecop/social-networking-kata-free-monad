package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * Free Monad.
 * 
 * @see "https://blog.rockthejvm.com/free-monad/#2-enter-the-free-monad"
 */
public interface Free<GENERIC_TYPE extends Generic<HIGHER_TYPE>, HIGHER_TYPE, T> {
    // trait Free[M[_], A] {

    static <GENERIC_TYPE extends Generic<HIGHER_TYPE>, HIGHER_TYPE, T> //
    Free<GENERIC_TYPE, HIGHER_TYPE, T> pure(T value) {
        return new Pure<>(value);
    }

    <B> Free<GENERIC_TYPE, HIGHER_TYPE, B> flatMap(Function<T, Free<GENERIC_TYPE, HIGHER_TYPE, B>> mapper);

    static <GENERIC_TYPE extends Generic<HIGHER_TYPE>, HIGHER_TYPE, T> //
    Free<GENERIC_TYPE, HIGHER_TYPE, T> liftM(GENERIC_TYPE m) {
        return null;
    }
}

class Pure<GENERIC_TYPE extends Generic<HIGHER_TYPE>, HIGHER_TYPE, T> //
        implements Free<GENERIC_TYPE, HIGHER_TYPE, T> {

    private final T value;

    public Pure(T value) {
        this.value = value;
    }

    static <GENERIC_TYPE extends Generic<HIGHER_TYPE>, HIGHER_TYPE, T> //
    Free<GENERIC_TYPE, HIGHER_TYPE, T> pure(T value) {
        return new Pure<>(value);
    }

    @Override
    public <B> Free<GENERIC_TYPE, HIGHER_TYPE, B> flatMap(Function<T, Free<GENERIC_TYPE, HIGHER_TYPE, B>> mapper) {
        return mapper.apply(value);
    }

}
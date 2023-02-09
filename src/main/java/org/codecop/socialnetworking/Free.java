package org.codecop.socialnetworking;

import java.util.function.BinaryOperator;
import java.util.function.Function;

public abstract class Free<T> {

    public static <T> Free<T> of(T value) {
        return new FreeValue<>(value);
    }

    public static Free<Void> nil() {
        return new FreeValue<>(null);
    }

    static class FreeValue<T> extends Free<T> {
        final T value;

        public FreeValue(T value) {
            this.value = value;
        }
    }

    public <U> Free<U> map(Function<T, U> mapper) {
        return new FreeMapper<>(this, mapper);
    }

    static class FreeMapper<T, U> extends Free<U> {
        final Free<T> before;
        final Function<T, U> mapper;

        public FreeMapper(Free<T> before, Function<T, U> mapper) {
            this.before = before;
            this.mapper = mapper;
        }
    }

    public <U> Free<U> flatMap(Function<T, Free<U>> mapper) {
        return new FreeFlatMapper<>(this, mapper);
    }

    static class FreeFlatMapper<T, U> extends Free<U> {
        final Free<T> before;
        final Function<T, Free<U>> mapper;

        public FreeFlatMapper(Free<T> before, Function<T, Free<U>> mapper) {
            this.before = before;
            this.mapper = mapper;
        }
    }

    static class Joiner<T extends Joining<T>> implements BinaryOperator<Free<T>> {
        @Override
        public Free<T> apply(Free<T> a, Free<T> b) {
            return b.flatMap(bMessages -> a.map(aMessages -> aMessages.join(bMessages)));
        }
    }

}

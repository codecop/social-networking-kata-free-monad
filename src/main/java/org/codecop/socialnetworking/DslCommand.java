package org.codecop.socialnetworking;

import java.util.function.BinaryOperator;
import java.util.function.Function;

abstract class DslCommand<T> implements Transformable<T> {

    public static <T> DslCommand<T> of(T value) { // "pure"
        return new FreeValue<>(value);
    }

    public static DslCommand<Void> nil() {
        return new FreeValue<>(null);
    }

    static class FreeValue<T> extends DslCommand<T> {
        final T value;

        public FreeValue(T value) {
            this.value = value;
        }
    }

    @Override
    public <U> DslCommand<U> map(Function<? super T, ? extends U> mapper) {
        return new FreeMapper<>(this, mapper);
    }

    static class FreeMapper<T, U> extends DslCommand<U> {
        final DslCommand<T> before;
        final Function<? super T, ? extends U> mapper;

        public FreeMapper(DslCommand<T> before, Function<? super T, ? extends U> mapper) {
            this.before = before;
            this.mapper = mapper;
        }
    }

}

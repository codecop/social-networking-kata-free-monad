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

    public <U> DslCommand<U> flatMap(Function<T, DslCommand<U>> mapper) {
        return new FreeFlatMapper<>(this, mapper);
    }

    static class FreeFlatMapper<T, U> extends DslCommand<U> {
        final DslCommand<T> before;
        final Function<T, DslCommand<U>> mapper;

        public FreeFlatMapper(DslCommand<T> before, Function<T, DslCommand<U>> mapper) {
            this.before = before;
            this.mapper = mapper;
        }
    }

    static class Joiner<T extends Joining<T>> implements BinaryOperator<DslCommand<T>> {
        @Override
        public DslCommand<T> apply(DslCommand<T> a, DslCommand<T> b) {
            return b.flatMap(bs -> a.map(as -> as.join(bs)));
        }
    }

}

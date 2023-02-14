package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * The command is only a functor.
 */
abstract class DslCommand<T> implements Transformable<T> {

    public static <T> DslCommand<T> of(T value) { // "pure"
        return new DslCommandValue<>(value);
    }

    public static DslCommand<Void> nil() {
        return new DslCommandValue<>(null);
    }

    static class DslCommandValue<T> extends DslCommand<T> {
        final T value;

        public DslCommandValue(T value) {
            this.value = value;
        }
    }

    @Override
    public <U> DslCommand<U> map(Function<? super T, ? extends U> mapper) {
        return new DslCommandMapper<>(this, mapper);
    }

    static class DslCommandMapper<T, U> extends DslCommand<U> {
        final DslCommand<T> before;
        final Function<? super T, ? extends U> mapper;

        public DslCommandMapper(DslCommand<T> before, Function<? super T, ? extends U> mapper) {
            this.before = before;
            this.mapper = mapper;
        }
    }

}

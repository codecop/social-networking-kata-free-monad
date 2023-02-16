package org.codecop.socialnetworking;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Functional helpers.
 */
public class F {

    private static final class NamedFunction<T, R> implements Function<T, R> {

        private final Supplier<String> name;
        private final Function<T, R> f;

        private NamedFunction(Supplier<String> name, Function<T, R> f) {
            this.name = name;
            this.f = f;
        }

        @Override
        public R apply(T t) {
            return f.apply(t);
        }

        @Override
        public String toString() {
            return name.get();
        }
    }

    public static <T, R> Function<T, R> named(String name, Function<T, R> f) {
        if (f instanceof NamedFunction) {
            return f;
        }
        if (name == null || name.isEmpty()) {
            return new NamedFunction<>(() -> " mapped by " + f, f);
        }

        return new NamedFunction<>(() -> " mapped by " + name, f);
    }

    public static <T, R> Function<T, R> named(Function<?, ?> innerMapper, Function<T, R> f) {
        if (f instanceof NamedFunction) {
            return f;
        }
        return new NamedFunction<>(innerMapper::toString, f);
    }

}

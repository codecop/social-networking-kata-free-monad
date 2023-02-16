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
        private final StackTraceElement stack;

        private NamedFunction(Supplier<String> name, Function<T, R> f) {
            this.name = name;
            this.f = f;
            this.stack = getTrace();
        }

        private StackTraceElement getTrace() {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            int i = 0;
            while (stackTrace[i].getClassName().endsWith("F$NamedFunction")
                    || stackTrace[i].getClassName().endsWith("F")
                    //|| stackTrace[i].getClassName().endsWith("Unrestricted")
                    //|| stackTrace[i].getClassName().endsWith("DslResult")
                    ) {
                i++;
            }
            StackTraceElement x = stackTrace[i];
            return x;
        }

        @Override
        public R apply(T t) {
            try {
                return f.apply(t);
            } catch (ClassCastException e) {
                System.err.println(e.toString() + " AT " + stack);
                throw e;
            }
        }

        @Override
        public String toString() {
            return name.get() + " > " + stack;
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

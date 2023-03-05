package org.codecop.socialnetworking;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Functional debug helpers.
 */
public class F {

    private static final class NamedFunction<T, TV, R, RV> implements HigherMap<T, TV, R, RV> {

        private final Supplier<String> name;
        private final Function<TV, RV> f;
        private final StackTraceElement stack;

        private NamedFunction(Supplier<String> name, Function<TV, RV> f) {
            this.name = name;
            this.f = f;
            this.stack = getTrace();
        }

        private StackTraceElement getTrace() {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            int i = 0;
            while (stackTrace[i].getClassName().endsWith("F$NamedFunction")
                    || stackTrace[i].getClassName().endsWith("F")) {
                i++;
            }
            return stackTrace[i];
        }

        @Override
        public RV apply(TV t) {
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

    @SuppressWarnings("unused")
    interface HigherMap<T, TV, R, RV> extends Function<TV, RV> {
        @Override
        RV apply(TV t);
    }

    public static <T, TV, R, RV> HigherMap<T, TV, R, RV> named(String name, HigherMap<T, TV, R, RV> f) {
        if (f instanceof NamedFunction) {
            return f;
        }
        if (name == null || name.isEmpty()) {
            return new NamedFunction<>(() -> " mapped by " + f, f);
        }

        return new NamedFunction<>(() -> " mapped by " + name, f);
    }

    public static <T, TV, R, RV> HigherMap<T, TV, R, RV> named(Function<TV, ?> innerMapper, HigherMap<T, TV, R, RV> f) {
        if (f instanceof NamedFunction) {
            return f;
        }
        return new NamedFunction<>(innerMapper::toString, f);
    }

}

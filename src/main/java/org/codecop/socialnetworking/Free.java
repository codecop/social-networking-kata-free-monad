package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.codecop.socialnetworking.F.HigherMap;

/**
 * Free Monad. This is a library class.
 * 
 * @See "https://blog.rockthejvm.com/free-monad"
 */
public /*sealed*/ abstract class Free<OPS, VALUE> {

    public static <O, V> Free<O, V> of(V value) {
        return new FreePure<>(value);
    }

    public static <O, V> Free<O, V> liftM(O ops) {
        return new FreeSuspend<>(ops);
    }

    public <RV> Free<OPS, RV> map(Function<VALUE, RV> mapper) {
        return flatMap(named(mapper, t -> Free.of(mapper.apply(t))));
    }

    // TODO later, flatmap changes V->RV but OPS stays the same
    public <RV> Free<OPS, RV> flatMap(HigherMap<OPS, VALUE, OPS, Free<OPS, RV>> mapper) {
        // this is a value. the mapper will "mapper.apply(this.transformable)"
        // so we need to create a tree now because the old value will need evaluation
        // and the flatMap result will need evaluation.
        return new FreeFlatMap<>(this, mapper);
    }

    public Free<OPS, VALUE> join(Free<OPS, VALUE> other, BiFunction<VALUE, VALUE, VALUE> joiner) {
        HigherMap<OPS, VALUE, OPS, Free<OPS, VALUE>> m1 = (VALUE otherValue) -> //
        {
            Function<VALUE, VALUE> m2 = (VALUE value) -> //
            joiner.apply(value, otherValue);
            return this.map(m2);
        };
        return other.flatMap(named("outer join", m1));
    }

    static final class FreePure<OPS, VALUE> extends Free<OPS, VALUE> {

        final VALUE value;

        private FreePure(VALUE value) {
            this.value = value;
        }

        @Override
        public String toString() {
            // debugging
            return String.valueOf(value);
        }
    }

    static final class FreeSuspend<OPS, VALUE> extends Free<OPS, VALUE> {

        final OPS ops;

        private FreeSuspend(OPS ops) {
            this.ops = ops;
        }

        @Override
        public String toString() {
            // debugging
            return "[" + ops + "]";
        }
    }

    static final class FreeFlatMap<T, TV, R, RV> extends Free<R, RV> {

        final Free<T, TV> previous;
        final HigherMap<? super T, TV, R, Free<R, RV>> mapper;

        private FreeFlatMap(Free<T, TV> previous, HigherMap<? super T, TV, R, Free<R, RV>> mapper) {
            this.previous = previous;
            this.mapper = mapper;
        }

        @Override
        public String toString() {
            // debugging
            return "[" + previous + mapper + "]";
        }
    }
}

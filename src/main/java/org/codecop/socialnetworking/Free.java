package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.NamedFunction.named;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Free Monad. This is a library class.
 * 
 * @See "https://blog.rockthejvm.com/free-monad"
 */
public /*sealed*/ abstract class Free<OPS, VALUE> {

    public static <O, V> Free<O, V> pure(V value) {
        return new FreePure<>(value);
    }

    public static <O, V> Free<O, V> liftM(O ops) {
        return new FreeSuspend<>(ops);
    }

    public <RV> Free<OPS, RV> map(Function<VALUE, RV> mapper) {
        return flatMap(named(mapper, t -> Free.pure(mapper.apply(t))));
    }

    public <RV> Free<OPS, RV> flatMap(Function<VALUE, Free<OPS, RV>> mapper) {
        // this is a value. the mapper will "mapper.apply"
        // so we need to create a tree now because the old value will need evaluation
        // and the flatMap result will need evaluation.
        return new FreeFlatMap<>(this, mapper);
    }

    public Free<OPS, VALUE> join(Free<OPS, VALUE> other, BiFunction<VALUE, VALUE, VALUE> joiner) {
        return other.flatMap(named("outer join", otherValue -> //
            map(named("inner join", value -> joiner.apply(value, otherValue)))));
    }

    public abstract <X> X foldMap(Function<OPS, X> natTrans);
    
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

        @Override
        public <X> X foldMap(Function<OPS, X> natTrans) {
            System.err.println("evaluating " + toString());
            return (X) value; // ? probably not
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

        @Override
        public <X> X foldMap(Function<OPS, X> natTrans) {
            System.err.println("evaluating " + toString());
            return natTrans.apply(ops);
        }
    }

    static final class FreeFlatMap<OPS, V, RV> extends Free<OPS, RV> {

        final Free<OPS, V> previous;
        final Function<V, Free<OPS, RV>> mapper;

        private FreeFlatMap(Free<OPS, V> previous, Function<V, Free<OPS, RV>> mapper) {
            this.previous = previous;
            this.mapper = mapper;
        }

        @Override
        public String toString() {
            // debugging
            return "[" + previous + mapper + "]";
        }

        @Override
        public <X> X foldMap(Function<OPS, X> natTrans) {
            X p = previous.foldMap(natTrans);
            System.err.println("evaluating " + toString());
            Free<OPS, ?> current = mapper.apply((V) p); // ? probably not
            return current.foldMap(natTrans);
        }
    }

    static <O, V> String format(Free<O, V> free) {
        // debugging
        String whitespace = "                                        ";
        int intend = 0;
        StringBuilder res = new StringBuilder();
        for (char c : free.toString().toCharArray()) {
            if (c == ']') {
                intend--;
                res.append('\n');
                res.append(whitespace.substring(0, intend * 2));
            }
            res.append(c);
            if (c == '[') {
                intend++;
                res.append('\n');
                res.append(whitespace.substring(0, intend * 2));
            }
        }
        return res.toString();
    }

}

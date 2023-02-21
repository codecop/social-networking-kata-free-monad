package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Simplified Free Monad.
 * 
 * @See "https://medium.com/modernnerd-code/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8"
 */
public abstract class Free<TRANSFORMABLE, VALUE> {

    public static <T, V> Free<T, V> liftF(T transformable) {
        return new FreeValue<>(transformable);
    }

    /**
     * Shortcut for flatMap/map
     */
    public <RV> Free<TRANSFORMABLE, RV> mapF(Function<VALUE, RV> mapper) {
        return flatMap(named(mapper, t -> {
            TRANSFORMABLE mapped = fromFunctor(asFunctor(t).map(mapper));
            return Free.<TRANSFORMABLE, RV>liftF(mapped);
        }));
    }

    private Transformable asFunctor(TRANSFORMABLE t) {
        // assume TRANSFORMABLE of VALUE is a Transformable<VALUE> 
        return (Transformable) t;
    }

    @SuppressWarnings("unchecked")
    private TRANSFORMABLE fromFunctor(Transformable t) {
        // assume Transformable<RV> is a TRANSFORMABLE of RV 
        return (TRANSFORMABLE) t;
    }

    public <R, RV> Free<R, RV> flatMap(F.HigherMap<? super TRANSFORMABLE, VALUE, Free<R, RV>, RV> mapper) {
        // this is a value. the mapper will "mapper.apply(this.transformable)"
        // so we need to create a tree now because the old value will need evaluation
        // and the flatMap result will need evaluation.
        return new FreeFlatMapped<>(this, mapper);
    }

    @SuppressWarnings("unchecked")
    public Free<TRANSFORMABLE, VALUE> join(Free<TRANSFORMABLE, VALUE> other, BiFunction<VALUE, VALUE, VALUE> joiner) {
        return other.flatMap(named("outer join", otherT -> //
                        flatMap(named("inner join", t -> //
                            Free.liftF(fromFunctor(asFunctor(t).flatMap(value -> //
                                asFunctor(otherT).map(otherValue -> //
                                    joiner.apply((VALUE) value, (VALUE) otherValue)))))))));
    }

    static class FreeValue<TRANSFORMABLE, VALUE> extends Free<TRANSFORMABLE, VALUE> {
        
        final TRANSFORMABLE transformable;

        /**
         * @param transformable a Transformable of some type.
         */
        private FreeValue(TRANSFORMABLE transformable) {
            if (!(transformable instanceof Transformable)) {
                throw new ClassCastException(transformable.getClass().getName());
            }
            this.transformable = transformable;
        }

        @Override
        public String toString() {
            // debugging
            return "[" + transformable + "]";
        }
    }

    static class FreeFlatMapped<T, TV, R, RV> extends Free<R, RV> {

        final Free<T, TV> previous;
        final F.HigherMap<? super T, TV, Free<R, RV>, RV> mapper;

        private FreeFlatMapped(Free<T, TV> previous, F.HigherMap<? super T, TV, Free<R, RV>, RV> mapper) {
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

/**
 * Functor. To have "reduce", need a Monad.
 */
interface Transformable {
    <T, R> Transformable map(Function<? super T, ? extends R> mapper);

    <T> Transformable flatMap(Function<? super T, ? extends Transformable> mapper);
}

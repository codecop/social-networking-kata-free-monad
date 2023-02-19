package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

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
     * Shortcut for flatmap/liftF
     */
    public <R, RV> Free<R, RV> map(F.HigherMap<TRANSFORMABLE, VALUE, R, RV> mapper) {
        return flatMap(named(mapper, t -> liftF(mapper.apply(t))));
        // return flatMap(t -> liftF(mapper.apply(t)));
    }

    /**
     * Shortcut for flatmap/map
     */
    public <RV> Free<TRANSFORMABLE, RV> mapF(Function<VALUE, RV> mapper) {
        return flatMap(named(mapper, t -> {
        // return flatMap(t -> {
            // assume TRANSFORMABLE of VALUE is a Transformable<VALUE> 
            Transformable<VALUE> value = (Transformable<VALUE>) t;
            Transformable<RV> mappedValue = value.map(mapper);
            // assume Transformable<RV> is a TRANSFORMABLE of RV 
            TRANSFORMABLE cast = (TRANSFORMABLE) mappedValue;
            return Free.<TRANSFORMABLE, RV>liftF(cast);
        }));
    }

    public <R, RV> Free<R, RV> flatMap(F.HigherMap<? super TRANSFORMABLE, VALUE, Free<R, RV>, RV> mapper) {
        // this is a value. the mapper will "mapper.apply(this.transformable)"
        // so we need to create a tree now because the old value will need evaluation
        // and the flatmap result will need evaluation.
        return new FreeFlatMapped<>(this, mapper);
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
            return "[" + transformable + "]";
        }
    }

    static class FreeFlatMapped<T, TV, R, RV> extends Free<R, RV> {

        final Free<T, TV> previous;
        final F.HigherMap<? super T, TV, Free<R, RV>, RV> mapper;

        public FreeFlatMapped(Free<T, TV> previous, F.HigherMap<? super T, TV, Free<R, RV>, RV> mapper) {
            this.previous = previous;
            this.mapper = mapper;
        }

        @Override
        public String toString() {
            return "[" + previous + mapper + "]";
        }
    }

}

/**
 * Functor.
 */
interface Transformable<T> {
    <R> Transformable<R> map(Function<? super T, ? extends R> mapper);
}

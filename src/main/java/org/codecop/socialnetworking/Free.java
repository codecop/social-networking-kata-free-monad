package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.util.function.Function;

/**
 * Simplified Free Monad.
 * 
 * @See "https://medium.com/modernnerd-code/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8"
 */
public abstract class Free<TRANSFORMABLE> {

    public static <T> Free<T> liftF(T transformable) {
        return new FreeValue<>(transformable);
    }

    /**
     * Shortcut for flatmap/liftF
     */
    public <R> Free<R> map(Function<TRANSFORMABLE, R> mapper) {
        // R must also be Transformable 
        return flatMap(named(mapper, t -> liftF(mapper.apply(t))));
    }

    /**
     * Shortcut for flatmap/map
     */
    public <A, B> Free mapF(Function<A, B> mapper) {
        return flatMap(named(mapper, t -> {
            Transformable<A> a = (Transformable<A>) t;
            Transformable<B> b = a.map(mapper);
            return Free.liftF(b);
        }));
    }

    public <R> Free<R> flatMap(Function<? super TRANSFORMABLE, Free<R>> mapper) {
        // this is a value. the mapper will "mapper.apply(this.transformable)"
        // so we need to create a tree now because the old value will need evaluation
        // and the flatmap result will need evaluation.
        return new FreeFlatMapped<>(this, mapper);
    }

    static class FreeValue<TRANSFORMABLE> extends Free<TRANSFORMABLE> {
        final TRANSFORMABLE transformable;

        /**
         * @param transformable a Transformable of some type.
         */
        protected FreeValue(TRANSFORMABLE transformable) {
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

    static class FreeFlatMapped<T, R> extends Free<R> {

        final Free<T> previous;
        final Function<? super T, Free<R>> mapper;

        public FreeFlatMapped(Free<T> previous, Function<? super T, Free<R>> mapper) {
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

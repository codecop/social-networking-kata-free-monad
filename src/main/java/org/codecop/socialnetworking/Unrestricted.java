package org.codecop.socialnetworking;

import static org.codecop.socialnetworking.F.named;

import java.util.function.Function;

/**
 * Simplified Free Monad.
 * 
 * @See "https://medium.com/modernnerd-code/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8"
 */
public class Unrestricted<TRANSFORMABLE> {

    final TRANSFORMABLE transformable;

    /**
     * @param transformable a Transformable of some type.
     */
    protected Unrestricted(TRANSFORMABLE transformable) {
        if (transformable != null && !(transformable instanceof Transformable)) {
            throw new ClassCastException(transformable.getClass().getName());
        }
        this.transformable = transformable;
    }

    public static <T> Unrestricted<T> liftF(T transformable) {
        return new Unrestricted<>(transformable);
    }

    /**
     * Shortcut for flatmap/liftF
     */
    public <R> Unrestricted<R> map(Function<TRANSFORMABLE, R> mapper) {
        // R must also be Transformable 
        return flatMap(named(mapper, t -> new Unrestricted<>(mapper.apply(t))));
    }

    /**
     * Shortcut for flatmap/map
     */
    public <A, B> Unrestricted mapF(Function<A, B> mapper) {
        return flatMap(named(mapper, t -> {
            Transformable<A> a = (Transformable<A>) t;
            Transformable<B> b = a.map(mapper);
            return Unrestricted.liftF(b);
        }));
    }

    public <R> Unrestricted<R> flatMap(Function<? super TRANSFORMABLE, Unrestricted<R>> mapper) {
        // this is a value. the mapper will "mapper.apply(this.transformable)"
        // so we need to create a tree now because the old value will need evaluation
        // and the flatmap result will need evaluation.
        return new UnrestrictedNode<>(this, mapper);
    }

    static class UnrestrictedNode<T, R> extends Unrestricted<R> {

        private final Unrestricted<T> previous;
        private final Function<? super T, Unrestricted<R>> mapper;

        public UnrestrictedNode(Unrestricted<T> previous, Function<? super T, Unrestricted<R>> mapper) {
            super(null);
            this.previous = previous;
            this.mapper = mapper;
        }

        @Override
        public Object run(DslVisitor v) {
            Object x = previous.run(v);
            System.err.println("evaluating " + toString());
            Unrestricted<DslCommand<?>> current = (Unrestricted<DslCommand<?>>) mapper.apply((T) DslResult.of(x));
            return current.run(v);
        }

        @Override
        public String toString() {
            return "[" + previous + mapper + "]";
        }
    }

    public Object run(DslVisitor v) {
        // TODO this is fixing the type, we just want something for transformable
        System.err.println("evaluating " + toString());
        return v.matchCommand((DslCommand<?>) transformable);
    }

    @Override
    public String toString() {
        return "[" + transformable + "]";
    }

}

/**
 * Functor.
 */
interface Transformable<T> {
    <R> Transformable<R> map(Function<? super T, ? extends R> mapper);
}

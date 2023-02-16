package org.codecop.socialnetworking;

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
        return flatMap(t -> new Unrestricted<>(mapper.apply(t)));
    }

    /**
     * Shortcut for flatmap/map
     */
    public <A, B> Unrestricted mapF(Function<A, B> mapper) {
        return flatMap(t -> new Unrestricted<>(((Transformable<A>) t).map(mapper)));
    }

    public <R> Unrestricted<R> flatMap(Function<? super TRANSFORMABLE, Unrestricted<R>> mapper) {
        // TODO temp hack to eval non lazy hack 
//        DslCommand result = Interpret.evalCommand((DslCommand) this.transformable);
//        Unrestricted<R> x = mapper.apply((TRANSFORMABLE) result);
//        return new UnrestrictedNode<R>(mapper, this, x.transformable);
        return new UnrestrictedNode<R>(mapper, this, null);
    }

    static class UnrestrictedNode<R> extends Unrestricted<R> {

        private final Function mapper;
        private final Unrestricted previous;

        public UnrestrictedNode(Function mapper, Unrestricted previous, R x) {
            super(x);
            this.mapper = mapper;
            this.previous = previous;
        }

        // return mapper.apply(previous);

        @Override
        public String toString() {
            return "[" + previous + "]";
        }
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

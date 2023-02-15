package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * Simplified Free Monad.
 * 
 * @See "https://medium.com/modernnerd-code/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8"
 */
public class Unrestricted<TRANSFORMABLE> {

    private final TRANSFORMABLE transformable;

    /**
     * @param transformable a Transformable of some type.
     */
    private Unrestricted(TRANSFORMABLE transformable) {
        if ((transformable instanceof Transformable)) {
            throw new ClassCastException(Transformable.class.getName());
        }
        this.transformable = transformable;
    }

    public static <T> Unrestricted<T> liftF(T transformable) {
        return new Unrestricted<>(transformable);
    }

    public <R> Unrestricted<R> map(Function<TRANSFORMABLE, R> mapper) {
        // R must also be Transformable 
        return flatMap(transformable -> new Unrestricted<>(mapper.apply(transformable)));
    }

    public <R> Unrestricted<R> mapF(Function<?, R> mapper) {
        return flatMap(transformable -> {
            Transformable t = (Transformable) transformable;
            Transformable x = t.map(mapper);
            return new Unrestricted<>(x);
        });
    }

    public <R> Unrestricted<R> flatMap(Function<? super TRANSFORMABLE, Unrestricted<R>> mapper) {
        return mapper.apply(transformable);
    }

    //    public static class Joiner<T extends Joining<T>> implements BinaryOperator<DslCommand<T>> {
    //        @Override
    //        public DslCommand<T> apply(DslCommand<T> a, DslCommand<T> b) {
    //            return b.flatMap(bs -> a.map(as -> as.join(bs)));
    //        }
    //    }

}

/**
 * Functor.
 */
interface Transformable<T> {
    <R> Transformable<R> map(Function<? super T, ? extends R> mapper);
}

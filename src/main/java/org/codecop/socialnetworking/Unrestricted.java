package org.codecop.socialnetworking;

import java.util.function.Function;

/**
 * Simplified Free Monad.
 * 
 * @See "https://medium.com/modernnerd-code/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8"
 */
public class Unrestricted<T extends Transformable<U>, U> {

    private final T value;

    private Unrestricted(T value) {
        this.value = value;
    }

    public static <T extends Transformable<U>, U> Unrestricted<T, U> liftF(T value) {
        return new Unrestricted<>(value);
    }

    public <R extends Transformable<S>, S> Unrestricted<R, S> map(Function<T, R> mapper) {
        return new Unrestricted<>(mapper.apply(value));
    }

    public <R extends Transformable<S>, S> Unrestricted<R, S> flatMap(Function<? super T, Unrestricted<R, S>> mapper) {
        return mapper.apply(value);
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

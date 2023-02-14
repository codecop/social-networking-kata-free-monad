package org.codecop.socialnetworking;

import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Simplified Free Monad.
 * 
 * @See "https://medium.com/modernnerd-code/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8"
 */
public class Unrestricted<T> {

    static <TRANSFORMABLE extends Transformable<V>, V> Unrestricted<TRANSFORMABLE> liftF(TRANSFORMABLE t) {
        return null;
    }

    public <U> Unrestricted<U> map(Function<? super T, ? extends U> mapper) {
        return new FreeMapper<>(this, mapper);
    }
    
    public <U> Unrestricted<U> flatMap(Function<T, Unrestricted<U>> mapper) {
        return new FreeFlatMapper<>(this, mapper);
    }

//    static class FreeFlatMapper<T, U> extends DslCommand<U> {
//        final DslCommand<T> before;
//        final Function<T, DslCommand<U>> mapper;
//
//        public FreeFlatMapper(DslCommand<T> before, Function<T, DslCommand<U>> mapper) {
//            this.before = before;
//            this.mapper = mapper;
//        }
//    }

    static class Joiner<T extends Joining<T>> implements BinaryOperator<DslCommand<T>> {
        @Override
        public DslCommand<T> apply(DslCommand<T> a, DslCommand<T> b) {
            return b.flatMap(bs -> a.map(as -> as.join(bs)));
        }
    }
    
}

/**
 * Functor.
 */
interface Transformable<T> {
    <R> Transformable<R> map(Function<? super T, ? extends R> mapper);
}

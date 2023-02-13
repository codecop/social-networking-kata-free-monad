package org.codecop.socialnetworking;

import java.util.function.BinaryOperator;
import java.util.function.Function;

abstract class AstNode<T> {

    public static <T> AstNode<T> of(T value) { // "pure"
        return new FreeValue<>(value);
    }

    public static AstNode<Void> nil() {
        return new FreeValue<>(null);
    }

    static class FreeValue<T> extends AstNode<T> {
        final T value;

        public FreeValue(T value) {
            this.value = value;
        }
    }

    public <U> AstNode<U> map(Function<T, U> mapper) {
        return new FreeMapper<>(this, mapper);
    }

    static class FreeMapper<T, U> extends AstNode<U> {
        final AstNode<T> before;
        final Function<T, U> mapper;

        public FreeMapper(AstNode<T> before, Function<T, U> mapper) {
            this.before = before;
            this.mapper = mapper;
        }
    }

    public <U> AstNode<U> flatMap(Function<T, AstNode<U>> mapper) {
        return new FreeFlatMapper<>(this, mapper);
    }

    static class FreeFlatMapper<T, U> extends AstNode<U> {
        final AstNode<T> before;
        final Function<T, AstNode<U>> mapper;

        public FreeFlatMapper(AstNode<T> before, Function<T, AstNode<U>> mapper) {
            this.before = before;
            this.mapper = mapper;
        }
    }

    static class Joiner<T extends Joining<T>> implements BinaryOperator<AstNode<T>> {
        @Override
        public AstNode<T> apply(AstNode<T> a, AstNode<T> b) {
            return b.flatMap(bs -> a.map(as -> as.join(bs)));
        }
    }

}

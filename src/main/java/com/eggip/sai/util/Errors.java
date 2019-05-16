package com.eggip.sai.util;

import com.eggip.sai.util.Functions.Function;
import com.jnape.palatable.lambda.adt.Either;

public class Errors {

    public static <T, R> Either<RuntimeException, R> wrap(Function<T, R> fn, T param) {
        try {
            R value = fn.apply(param);
            return Either.right(value);
        } catch (Throwable t) {
            return Either.left(new RuntimeException(t));
        }
    }


}
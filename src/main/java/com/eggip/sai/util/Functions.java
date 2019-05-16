package com.eggip.sai.util;

/**
 * Java标准库的函数类型均不能抛出异常，处理比较麻烦
 */
public class Functions {

    @FunctionalInterface
    public static interface Function<T, R> {
        R apply(T t) throws Exception;
    }

    
}
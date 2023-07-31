package com.github.wohaopa.zpl.ui.zplui.util;

import java.util.concurrent.Callable;

public class Lazy<T> {
    private final Callable<T> callable;
    private T value;

    public Lazy(Callable<T> callable) {
        this.callable = callable;
    }

    public T getValue() {
        if (value == null) try {
            value = callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }
}

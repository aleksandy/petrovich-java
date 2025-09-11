package com.github.aleksandy.petrovich;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static <T> List<T> nullToEmpty(List<T> in) {
        return in == null ? List.of() : in;
    }

    public static <T> Set<T> nullToEmpty(Set<T> in) {
        return in == null ? Set.of() : in;
    }

    public static <T> Supplier<T> lazy(Supplier<? extends T> supplier) {
        return new Lazy<>(supplier);
    }

    private static final class Lazy<T> implements Supplier<T> {

        private static final Object SENTINEL = new Object();

        private volatile T value = (T) SENTINEL;

        private Supplier<? extends T> delegate;

        private Lazy(Supplier<? extends T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            if (this.value == SENTINEL) {
                synchronized (this) {
                    if (this.value == SENTINEL) {
                        this.value = this.delegate.get();
                        this.delegate = null;
                    }
                }
            }

            return this.value;
        }

    }

}

package com.github.aleksandy.petrovich;

import java.util.List;
import java.util.Set;

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

}

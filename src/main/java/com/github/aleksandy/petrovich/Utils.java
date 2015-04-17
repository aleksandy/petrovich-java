package com.github.aleksandy.petrovich;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Utils {

    private Utils() {
    }

    public static <T> List<T> nullToEmpty(List<T> in) {
        return in == null ? Collections.<T>emptyList() : in;
    }

    public static <T> Set<T> nullToEmpty(Set<T> in) {
        return in == null ? Collections.<T>emptySet() : in;
    }

    public static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().isEmpty();
    }

}

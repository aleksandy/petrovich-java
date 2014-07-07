package com.github.aleksandy.petrovich.rules.data;

import java.util.Collections;
import java.util.List;

final class Utils {

    private Utils() {
    }

    public static <T> List<T> nullToEmpty(List<T> in) {
        return in == null ? Collections.<T>emptyList() : in;
    }

}

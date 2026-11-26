/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich.rules;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Правило определения пола по суффиксу части имени.
 *
 * @param gender пол, который назначает правило
 * @param suffix суффикс в нижнем регистре для сравнения с концом части имени
 */
public record GenderRule(Gender gender, String suffix) {

    /**
     * Создаёт правило, приводя суффикс к нижнему регистру.
     *
     * @param gender пол, к которому относится правило
     * @param suffix суффикс для сравнения с концом части имени
     * @throws NullPointerException если {@code gender} или {@code suffix}
     *                              равны {@code null}
     */
    public GenderRule {
        requireNonNull(gender, "Gender must not be null.");
        suffix = requireNonNull(suffix, "Suffix must not be null.").toLowerCase(Locale.ROOT);
    }

    /**
     * Проверяет, оканчивается ли переданная часть имени на суффикс правила.
     *
     * <p>Сравнение чувствительно к регистру: ожидается часть имени, уже
     * приведённая к нижнему регистру.
     *
     * @param name часть имени в нижнем регистре
     * @return {@code true}, если часть имени подходит под правило
     */
    public boolean matches(String name) {
        return name.endsWith(this.suffix);
    }

}

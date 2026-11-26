/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich.rules;

import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Правило склонения части имени.
 *
 * <p>Правило срабатывает, если часть имени (без учёта регистра) оканчивается
 * на один из {@code testSuffixes}. Для исключений {@code testSuffixes}
 * содержит целые слова, которые сравниваются с частью имени целиком.
 *
 * <p>{@code modSuffixes} содержит ровно пять элементов — окончания для
 * родительного, дательного, винительного, творительного и предложного падежей.
 * В каждом элементе символ {@code -} удаляет один символ с конца имени,
 * {@code .} ничего не добавляет, а остальные символы дописываются в конец.
 *
 * <p>Инварианты проверяются в конструкторе: список {@code testSuffixes}
 * не должен быть пустым, а {@code modSuffixes} должен содержать ровно пять
 * элементов. Поэтому некорректный файл правил падает сразу при загрузке.
 *
 * @param gender пол, к которому относится правило
 * @param testSuffixes суффиксы (для исключений — целые слова) для проверки части имени
 * @param modSuffixes окончания для пяти косвенных падежей
 * @param tags теги правила; правило подходит, только если все его теги
 *             входят в контекст слова (например, {@code first_word} — для всех
 *             слов составной части имени, кроме последнего)
 */
public record Rule(
    Gender gender,
    List<String> testSuffixes,
    List<String> modSuffixes,
    Set<String> tags
) {

    private static final int MOD_SUFFIXES_COUNT = 5;

    /**
     * Создаёт правило, проверяя его инварианты и нормализуя {@code null}-теги.
     *
     * @param gender пол, к которому относится правило
     * @param testSuffixes суффиксы (для исключений — целые слова) для проверки части имени
     * @param modSuffixes окончания для пяти косвенных падежей
     * @param tags теги правила или {@code null}
     * @throws NullPointerException если {@code gender} равен {@code null}
     * @throws IllegalArgumentException если {@code testSuffixes} пуст или {@code modSuffixes}
     *                                  содержит не пять элементов
     */
    public Rule {
        requireNonNull(gender, "Gender must not be null.");

        if (testSuffixes == null || testSuffixes.isEmpty()) {
            throw new IllegalArgumentException(
                "Rule test suffixes must not be empty for gender " + gender + "."
            );
        }

        int actualSize = 0;
        if (modSuffixes == null || (actualSize = modSuffixes.size()) != MOD_SUFFIXES_COUNT) {
            throw new IllegalArgumentException(
                "Rule mod suffixes must contain exactly " + MOD_SUFFIXES_COUNT
                    + " elements, but got " + actualSize
                    + " for test suffixes " + testSuffixes + '.'
            );
        }

        tags = tags == null ? Set.of() : tags;
    }

    /**
     * Создаёт правило без тегов.
     *
     * @param gender пол, к которому относится правило
     * @param testSuffixes суффиксы (для исключений — целые слова) для проверки части имени
     * @param modSuffixes окончания для пяти косвенных падежей
     */
    public Rule(Gender gender, List<String> testSuffixes, List<String> modSuffixes) {
        this(gender, testSuffixes, modSuffixes, null);
    }

}

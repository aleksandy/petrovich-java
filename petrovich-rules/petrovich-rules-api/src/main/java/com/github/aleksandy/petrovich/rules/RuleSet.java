package com.github.aleksandy.petrovich.rules;

import java.util.List;

/**
 * Набор правил склонения для одной части имени (фамилии, имени или отчества).
 *
 * <p>Сначала проверяются исключения (совпадение с частью имени целиком),
 * затем суффиксные правила в порядке их объявления.
 *
 * @param suffixes суффиксные правила
 * @param exceptions правила-исключения, сравниваемые с частью имени целиком
 */
public record RuleSet(List<Rule> suffixes, List<Rule> exceptions) {

    /**
     * Пустой набор правил.
     */
    public static final RuleSet EMPTY = new RuleSet(List.of(), List.of());

    /**
     * Создаёт набор только из суффиксных правил, без исключений.
     *
     * @param suffixes суффиксные правила
     */
    public RuleSet(List<Rule> suffixes) {
        this(suffixes, null);
    }

    /**
     * Создаёт набор правил, нормализуя {@code null}-списки в пустые.
     *
     * @param suffixes суффиксные правила
     * @param exceptions правила-исключения, сравниваемые с частью имени целиком
     */
    public RuleSet {
        suffixes = suffixes == null ? List.of() : suffixes;
        exceptions = exceptions == null ? List.of() : exceptions;
    }

}

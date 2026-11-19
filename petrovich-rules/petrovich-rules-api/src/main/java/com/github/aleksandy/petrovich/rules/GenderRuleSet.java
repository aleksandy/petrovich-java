package com.github.aleksandy.petrovich.rules;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Набор правил определения пола для одной части имени (фамилии, имени или отчества).
 *
 * <p>Исключения сравниваются с частью имени целиком (точное совпадение без учёта
 * регистра); суффиксные правила проверяются от самого длинного суффикса к короткому.
 *
 * @param exceptions правила, сравниваемые с частью имени целиком
 * @param suffixes суффиксные правила, упорядоченные по точности (сначала длинные)
 */
public record GenderRuleSet(Map<String, GenderRule> exceptions, List<GenderRule> suffixes) {

    /**
     * Пустой набор правил: ни одно имя не определяется.
     */
    public static final GenderRuleSet EMPTY = new GenderRuleSet(Map.of(), List.of());

    /**
     * Создаёт набор правил, нормализуя {@code null} в пустые коллекции
     * и сортируя суффиксные правила от самого длинного суффикса к короткому.
     *
     * @param exceptions правила, сравниваемые с частью имени целиком
     * @param suffixes суффиксные правила
     */
    public GenderRuleSet {
        exceptions = exceptions == null ? Map.of() : Map.copyOf(exceptions);
        suffixes = suffixes == null
            ? List.of()
            : suffixes.stream()
                .sorted(Comparator.<GenderRule>comparingInt(rule -> rule.suffix().length()).reversed())
                .toList();
    }

    /**
     * Находит правило определения пола для части имени: точное совпадение с
     * исключением имеет приоритет, иначе возвращается первое (самое длинное)
     * подходящее суффиксное правило.
     *
     * @param name часть имени (в любом регистре)
     * @return подходящее правило или {@code null}, если ничего не совпало
     */
    public GenderRule find(String name) {
        String loweredName = name.toLowerCase(Locale.ROOT);

        GenderRule exception = this.exceptions.get(loweredName);
        if (exception != null) {
            return exception;
        }

        return this.suffixes.stream()
            .filter(rule -> rule.matches(loweredName))
            .findFirst()
            .orElse(null);
    }

}

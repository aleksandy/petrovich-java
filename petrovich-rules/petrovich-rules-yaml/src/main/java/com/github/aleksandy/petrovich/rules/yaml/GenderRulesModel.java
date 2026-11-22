package com.github.aleksandy.petrovich.rules.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.GenderRule;
import com.github.aleksandy.petrovich.rules.GenderRuleSet;
import com.github.aleksandy.petrovich.rules.GenderRules;

/**
 * Промежуточная модель файла правил определения пола.
 *
 * <p>Повторяет структуру YAML-файла и не зависит от библиотеки разбора.
 * Реализует {@link GenderRules}, преобразуя части имени в доменные
 * {@link GenderRuleSet} при обращении к ним.
 *
 * @param gender правила по частям имени; ключ — {@code lastname},
 *               {@code firstname} или {@code middlename}
 */
public record GenderRulesModel(Map<String, GenderPartModel> gender) implements GenderRules {

    @Override
    public GenderRuleSet lastName() {
        return part("lastname");
    }

    @Override
    public GenderRuleSet firstName() {
        return part("firstname");
    }

    @Override
    public GenderRuleSet middleName() {
        return part("middlename");
    }

    private GenderRuleSet part(String name) {
        GenderPartModel model = this.gender == null ? null : this.gender.get(name);

        return model == null ? GenderRuleSet.EMPTY : model.toGenderRuleSet();
    }

    /**
     * Промежуточная модель правил определения пола для одной части имени.
     *
     * <p>Повторяет структуру YAML-файла и не зависит от библиотеки разбора.
     * Преобразование в доменный {@link GenderRuleSet} выполняется явно.
     *
     * @param exceptions исключения по целым словам, сгруппированные по полу
     * @param suffixes суффиксные правила, сгруппированные по полу
     */
    public record GenderPartModel(
        Map<Gender, List<String>> exceptions,
        Map<Gender, List<String>> suffixes
    ) {

        GenderRuleSet toGenderRuleSet() {
            Map<String, GenderRule> exceptionRules = new HashMap<>();
            forEach(this.exceptions, (gender, words) ->
                words.forEach(word ->
                    exceptionRules.put(word.toLowerCase(Locale.ROOT), new GenderRule(gender, word))
                )
            );

            List<GenderRule> suffixRules = new ArrayList<>();
            for (Gender gender : Gender.values()) {
                List<String> suffixes = this.suffixes == null ? null : this.suffixes.get(gender);

                if (suffixes != null) {
                    suffixes.forEach(suffix -> suffixRules.add(new GenderRule(gender, suffix)));
                }
            }

            return new GenderRuleSet(exceptionRules, suffixRules);
        }

        private static void forEach(
            Map<Gender, List<String>> source,
            BiConsumer<Gender, List<String>> action
        ) {
            if (source != null) {
                source.forEach(action);
            }
        }

    }
}

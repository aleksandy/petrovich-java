/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich;

import static com.github.aleksandy.petrovich.rules.Gender.ANDROGYNOUS;
import static com.github.aleksandy.petrovich.rules.Gender.FEMALE;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Locale;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

/**
 * Склонение частей имени по правилам (порт руби-реализации).
 *
 * <p>Правила перебираются в порядке загрузки: первое подходящее правило
 * выигрывает (как {@code @case_rules.find} в ruby). Сначала проверяются
 * исключения, затем суффиксы. Если правило не найдено, часть имени
 * возвращается без изменений.
 */
class CaseInflection {

    private static final Locale RUSSIA = Locale.forLanguageTag("ru-RU");

    private final Rules rules;
    private final Gender gender;
    private final boolean knownGender;

    CaseInflection(Rules rules, Gender gender) {
        this(rules, gender, false);
    }

    CaseInflection(Rules rules, Gender gender, boolean knownGender) {
        this.rules = requireNonNull(rules, "Rules must not be null.");
        this.gender = gender;
        this.knownGender = knownGender;
    }

    String inflectFirstNameTo(String firstName, Case inflectionCase) {
        return inflectTo(firstName, inflectionCase, this.rules.firstName());
    }

    String inflectLastNameTo(String lastName, Case inflectionCase) {
        return inflectTo(lastName, inflectionCase, this.rules.lastName());
    }

    String inflectMiddleNameTo(String middleName, Case inflectionCase) {
        return inflectTo(middleName, inflectionCase, this.rules.middleName());
    }

    private String inflectTo(String name, Case inflectionCase, RuleSet ruleSet) {
        if (name == null || (name = name.strip()).isEmpty()) {
            throw new PetrovichException("Name must not be blank.");
        }

        String[] chunks = name.split("-");
        if (chunks.length == 0) {
            return name;
        }

        for (int idx = 0; idx < chunks.length; idx++) {
            // Строгий режим по полу применяется только к последней части
            // составного имени — как в ruby (find_all_case_rules).
            boolean knownGenderChunk = this.knownGender && idx == chunks.length - 1;
            Rule rule = findFor(chunks[idx], ruleSet, knownGenderChunk);
            if (rule != null) {
                chunks[idx] = apply(chunks[idx], rule, inflectionCase);
            }
        }

        return String.join("-", chunks);
    }

    private String apply(String name, Rule rule, Case inflectionCase) {
        StringBuilder result = new StringBuilder(name.length() + 5).append(name);

        CharacterIterator itr = new StringCharacterIterator(modificatorFor(inflectionCase, rule));

        for (char $char = itr.current(); $char != CharacterIterator.DONE; $char = itr.next()) {
            switch ($char) {
                case '.' -> {
                    // пропускаем символ
                }
                case '-' -> {
                    if (result.isEmpty()) {
                        throw new PetrovichException(
                            format("Can't apply rule for '%s' case %s", name, inflectionCase)
                        );
                    }
                    result.setLength(result.length() - 1);
                }
                default -> result.append($char);
            }
        }

        return result.toString();
    }

    private String modificatorFor(Case inflectionCase, Rule rule) {
        return switch (inflectionCase) {
            case NOMINATIVE -> ".";
            case GENITIVE, DATIVE, ACCUSATIVE, INSTRUMENTAL, PREPOSITIONAL ->
                rule.modSuffixes().get(inflectionCase.getSuffixesIndex());
        };
    }

    /**
     * Ищет правило для части имени.
     *
     * <p>Порядок как в ruby: при явно заданном поле сначала ищем правило,
     * совпадающее по полу; если такого нет — повторяем поиск в мягком режиме
     * (androgynous). Теги правил при матчинге не учитываются.
     */
    private Rule findFor(String name, RuleSet ruleSet, boolean knownGenderChunk) {
        Rule rule = find(name, ruleSet.exceptions(), true, this.gender, knownGenderChunk);
        if (rule == null) {
            rule = find(name, ruleSet.suffixes(), false, this.gender, knownGenderChunk);
        }

        if (rule == null && knownGenderChunk) {
            // Fallback на androgynous, как в ruby find_case_rule.
            rule = find(name, ruleSet.exceptions(), true, ANDROGYNOUS, false);
            if (rule == null) {
                rule = find(name, ruleSet.suffixes(), false, ANDROGYNOUS, false);
            }
        }

        return rule;
    }

    private Rule find(
        String name,
        List<Rule> rules,
        boolean matchWholeWord,
        Gender matchGender,
        boolean strictGender
    ) {
        String loweredName = name.toLowerCase(RUSSIA);
        return rules.stream()
            .filter(rule -> genderMatches(rule.gender(), matchGender, strictGender))
            .filter(rule -> nameMatches(loweredName, rule, matchWholeWord))
            .findFirst()
            .orElse(null);
    }

    /**
     * Проверяет, подходит ли правило по полу.
     *
     * <p>В строгом режиме пол правила должен совпасть с заданным. В мягком
     * режиме женские правила подходят только для женского пола, а мужские и
     * андрогинные — для всех остальных (включая неопределённый пол).
     */
    private static boolean genderMatches(
        Gender ruleGender,
        Gender matchGender,
        boolean strictGender
    ) {
        if (strictGender) {
            return ruleGender == matchGender;
        }

        boolean femaleRule = ruleGender == FEMALE;
        boolean femaleName = matchGender == FEMALE;
        return femaleRule == femaleName;
    }

    private static boolean nameMatches(String loweredName, Rule rule, boolean matchWholeWord) {
        for (String suffix : rule.testSuffixes()) {
            String loweredSuffix = suffix.toLowerCase(RUSSIA);
            if (matchWholeWord
                ? loweredName.equals(loweredSuffix)
                : loweredName.endsWith(loweredSuffix)) {
                return true;
            }
        }

        return false;
    }

}

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Разделитель слов в составной части имени: серия дефисов и пробелов.
     */
    private static final Pattern WORD_SEPARATOR = Pattern.compile("[- ]+");

    /**
     * Контекстный тег для всех слов составной части имени, кроме последнего.
     */
    private static final String FIRST_WORD_TAG = "first_word";

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

        List<String> words = new ArrayList<>();
        List<String> separators = new ArrayList<>();
        tokenize(name, words, separators);

        for (int idx = 0, size = words.size(); idx < size; idx++) {
            boolean last = idx == size - 1;
            // Строгий режим по полу применяется только к последней части
            // составного имени — как в ruby (find_all_case_rules).
            boolean knownGenderChunk = this.knownGender && last;
            // Все слова, кроме последнего, находятся в контексте first_word:
            // правила с этим тегом (несклоняемые частицы) применяются только там.
            Set<String> contextTags = last ? Set.of() : Set.of(FIRST_WORD_TAG);

            Rule rule = findFor(words.get(idx), ruleSet, knownGenderChunk, contextTags);
            if (rule != null) {
                words.set(idx, apply(words.get(idx), rule, inflectionCase));
            }
        }

        // Разделители (дефисы и пробелы) сохраняются как в исходном имени.
        StringBuilder result = new StringBuilder(name.length() + 5);
        for (int idx = 0; idx < words.size(); idx++) {
            result.append(words.get(idx));
            if (idx < separators.size()) {
                result.append(separators.get(idx));
            }
        }

        return result.toString();
    }

    /**
     * Разбивает часть имени на слова и разделители, как ruby
     * {@code WORD_SEPARATOR_CAPTURE}: слова попадают в {@code words},
     * а идущие между ними серии дефисов и пробелов — в {@code separators}.
     */
    private static void tokenize(String name, List<String> words, List<String> separators) {
        Matcher matcher = WORD_SEPARATOR.matcher(name);
        int position = 0;

        while (matcher.find()) {
            words.add(name.substring(position, matcher.start()));
            separators.add(matcher.group());
            position = matcher.end();
        }

        words.add(name.substring(position));
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
     * <p>Порядок как в ruby: сначала ищем правило, совпадающее по полу; если
     * такого нет — повторяем поиск в мягком режиме (androgynous). Правило
     * учитывается, только если все его теги входят в контекстные теги слова.
     */
    private Rule findFor(
        String name,
        RuleSet ruleSet,
        boolean knownGenderChunk,
        Set<String> contextTags
    ) {
        Rule rule = find(
            name, ruleSet.exceptions(), true, this.gender, knownGenderChunk, contextTags
        );
        if (rule == null) {
            rule = find(
                name, ruleSet.suffixes(), false, this.gender, knownGenderChunk, contextTags
            );
        }

        if (rule == null) {
            // Fallback на androgynous, как в ruby find_case_rule.
            rule = find(name, ruleSet.exceptions(), true, ANDROGYNOUS, false, contextTags);
            if (rule == null) {
                rule = find(name, ruleSet.suffixes(), false, ANDROGYNOUS, false, contextTags);
            }
        }

        return rule;
    }

    private Rule find(
        String name,
        List<Rule> rules,
        boolean matchWholeWord,
        Gender matchGender,
        boolean strictGender,
        Set<String> contextTags
    ) {
        String loweredName = name.toLowerCase(RUSSIA);
        return rules.stream()
            .filter(rule -> contextTags.containsAll(rule.tags()))
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

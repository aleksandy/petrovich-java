/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich;

import static java.util.Objects.requireNonNull;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.Rules;

/**
 * Склонение русских имён, фамилий и отчеств по падежам.
 */
public class Petrovich {

    private final Rules rules;
    private final GenderDetector detector;

    /**
     * Создаёт экземпляр с заданным источником правил.
     *
     * @param rules правила склонения и определения пола
     * @throws NullPointerException если {@code rules} равен {@code null}
     */
    public Petrovich(Rules rules) {
        this.rules = requireNonNull(rules, "Rules must not be null.");
        this.detector = new GenderDetector(rules.gender());
    }

    /**
     * Определяет пол по имени: явно заданный пол имеет приоритет,
     * иначе пол определяется по правилам.
     *
     * @param names имя
     * @return пол или {@code null}, если его не удалось определить
     */
    public Gender gender(Names names) {
        requireNonNull(names, "Names must not be null.");

        Gender gender = names.gender();
        return gender != null ? gender : this.detector.detect(names);
    }

    /**
     * Склоняет имя в указанный падеж.
     *
     * <p>Пол берётся из {@link Names#gender()}, если он задан явно, иначе
     * определяется по правилам. Отсутствующие части имени остаются {@code null}.
     *
     * @param names имя
     * @param inflectionCase падеж
     * @return имя в заданном падеже с определённым полом
     *         (или {@code null}, если пол определить не удалось)
     */
    public Names inflectTo(Names names, Case inflectionCase) {
        requireNonNull(names, "Names must not be null.");
        requireNonNull(inflectionCase, "Case must not be null.");

        boolean knownGender = names.gender() != null;
        Gender gender = gender(names);

        CaseInflection inflection = new CaseInflection(this.rules, gender, knownGender);

        return new Names(
            names.lastName() == null ? null : inflection.inflectLastNameTo(names.lastName(), inflectionCase),
            names.firstName() == null ? null : inflection.inflectFirstNameTo(names.firstName(), inflectionCase),
            names.middleName() == null ? null : inflection.inflectMiddleNameTo(names.middleName(), inflectionCase),
            gender
        );
    }

}

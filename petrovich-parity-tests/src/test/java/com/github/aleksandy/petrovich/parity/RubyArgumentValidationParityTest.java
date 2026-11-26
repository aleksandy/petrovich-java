/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2013-2014 Andrew Kozloff & Dmitry Ustalov
 * Copyright (c) 2014-2026 Andrey Alekseenko
 * Ported from petrovich-ruby test/petrovich_gender_test.rb; adapted for this project.
 */

package com.github.aleksandy.petrovich.parity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.aleksandy.petrovich.Names;
import org.junit.jupiter.api.Test;

/**
 * Валидация, портированная из petrovich-ruby {@code petrovich_gender_test.rb}.
 *
 * <p>В ruby {@code Gender.detect} принимает произвольные аргументы и падает
 * с {@code ArgumentError} на неизвестных ключах или пустых именах. В java API
 * типизировано, поэтому «неизвестные ключи» невозможны на этапе компиляции;
 * остаётся проверка, что имя без единой непустой части отвергается.
 */
class RubyArgumentValidationParityTest {

    @Test void shouldRejectNamesWithoutParts() {
        assertThatThrownBy(() -> new Names(null, null, null, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("At least one of lastName, firstName or middleName must be provided.");

        assertThatThrownBy(() -> new Names("   ", "", "\t\n", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("At least one of lastName, firstName or middleName must be provided.");
    }

}

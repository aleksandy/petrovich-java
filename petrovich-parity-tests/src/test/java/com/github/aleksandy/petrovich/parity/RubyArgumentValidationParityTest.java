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

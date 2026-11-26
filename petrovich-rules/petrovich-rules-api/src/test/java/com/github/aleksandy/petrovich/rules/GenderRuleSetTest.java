/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class GenderRuleSetTest {

    private final GenderRuleSet rules = new GenderRuleSet(
        Map.of("саша", new GenderRule(Gender.ANDROGYNOUS, "саша")),
        List.of(
            new GenderRule(Gender.MALE, "ий"),
            new GenderRule(Gender.FEMALE, "ский"),
            new GenderRule(Gender.MALE, "ов")
        )
    );

    @Test void findShouldReturnExceptionByWholeWordIgnoringCase() {
        assertThat(this.rules.find("САША"))
            .extracting(GenderRule::gender)
            .isEqualTo(Gender.ANDROGYNOUS);
    }

    @Test void findShouldPreferTheLongestSuffix() {
        assertThat(this.rules.find("Склифасовский"))
            .extracting(GenderRule::gender)
            .isEqualTo(Gender.FEMALE);
    }

    @Test void findShouldMatchSuffixIgnoringCase() {
        assertThat(this.rules.find("ИВАНОВ"))
            .extracting(GenderRule::gender)
            .isEqualTo(Gender.MALE);
    }

    @Test void findShouldReturnNullWhenNothingMatched() {
        assertThat(this.rules.find("Андрейчук")).isNull();
    }

    @Test void suffixesShouldBeSortedByAccuracyDescending() {
        assertThat(this.rules.suffixes())
            .extracting(GenderRule::suffix)
            .containsExactly("ский", "ий", "ов");
    }

}

package com.github.aleksandy.petrovich.rules;

import static com.github.aleksandy.petrovich.rules.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class RuleTest {

    private static final List<String> MODS = List.of("а", "у", "а", "ом", "е");

    @Test void shouldRejectNullTestSuffixes() {
        assertThatThrownBy(() -> new Rule(MALE, null, MODS))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Rule test suffixes must not be empty for gender MALE.");
    }

    @Test void shouldRejectEmptyTestSuffixes() {
        assertThatThrownBy(() -> new Rule(MALE, List.of(), MODS))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Rule test suffixes must not be empty for gender MALE.");
    }

    @Test void shouldRejectNullModSuffixes() {
        assertThatThrownBy(() -> new Rule(MALE, List.of("н"), null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "Rule mod suffixes must contain exactly 5 elements, but got 0 for test suffixes [н]."
            );
    }

    @Test void shouldRejectModSuffixesWithWrongCount() {
        assertThatThrownBy(() -> new Rule(MALE, List.of("н"), List.of("а")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "Rule mod suffixes must contain exactly 5 elements, but got 1 for test suffixes [н]."
            );
    }

    @Test void shouldRequireGender() {
        assertThatThrownBy(() -> new Rule(null, List.of("н"), MODS))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Gender must not be null.");
    }

    @Test void shouldDefaultTagsToEmptySet() {
        assertThat(new Rule(MALE, List.of("н"), MODS).tags()).isEqualTo(Set.of());
    }

}

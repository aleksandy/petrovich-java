package com.github.aleksandy.petrovich.rules;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GenderTest {

    @Test void predicatesShouldMatchEnumValues() {
        assertThat(Gender.MALE.isMale()).isTrue();
        assertThat(Gender.MALE.isFemale()).isFalse();
        assertThat(Gender.MALE.isAndrogynous()).isFalse();

        assertThat(Gender.FEMALE.isFemale()).isTrue();
        assertThat(Gender.FEMALE.isMale()).isFalse();
        assertThat(Gender.FEMALE.isAndrogynous()).isFalse();

        assertThat(Gender.ANDROGYNOUS.isAndrogynous()).isTrue();
        assertThat(Gender.ANDROGYNOUS.isMale()).isFalse();
        assertThat(Gender.ANDROGYNOUS.isFemale()).isFalse();
    }

}

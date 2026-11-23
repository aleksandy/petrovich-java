package com.github.aleksandy.petrovich.parity;

import static com.github.aleksandy.petrovich.rules.Gender.ANDROGYNOUS;
import static com.github.aleksandy.petrovich.rules.Gender.FEMALE;
import static com.github.aleksandy.petrovich.rules.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import com.github.aleksandy.petrovich.Names;
import com.github.aleksandy.petrovich.Petrovich;
import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.yaml.YamlRules;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Кейсы определения пола, портированные из petrovich-ruby
 * {@code test/petrovich_gender_test.rb} и {@code test/petrovich_test.rb}.
 */
class RubyGenderParityTest {

    private final Petrovich petrovich = new Petrovich(new YamlRules());

    @ParameterizedTest(name = "[{index}] {0} → {1}")
    @MethodSource("genders")
    void shouldDetectGenderLikeRuby(Names names, Gender expected) {
        assertThat(this.petrovich.gender(names)).isEqualTo(expected);
    }

    static Stream<Arguments> genders() {
        return Stream.of(
            // petrovich_gender_test.rb
            Arguments.of(new Names(null, "Александр", null, null), MALE),
            Arguments.of(new Names("Склифасовский", null, null, null), MALE),
            Arguments.of(new Names(null, "Александра", null, null), FEMALE),
            Arguments.of(new Names("Склифасовская", null, null, null), FEMALE),
            Arguments.of(new Names("Склифасовская", "Александра", null, null), FEMALE),
            Arguments.of(new Names(null, "Саша", null, null), ANDROGYNOUS),
            Arguments.of(new Names("Андрейчук", "Саша", null, null), ANDROGYNOUS),
            Arguments.of(new Names("Иванов", "Саша", null, null), MALE),
            Arguments.of(new Names("Андрейчук", "Саша", "Олегович", null), MALE),
            Arguments.of(new Names(null, "Саша", "Олегович", null), MALE),
            Arguments.of(new Names("Осипчук", null, null, null), ANDROGYNOUS),
            Arguments.of(new Names(null, null, "Олегович", null), MALE),
            Arguments.of(new Names(null, null, "Олеговна", null), FEMALE),

            // petrovich_test.rb: androgynous? / male? / female?
            Arguments.of(new Names("Андрейчук", "Саша", null, null), ANDROGYNOUS),
            Arguments.of(new Names("Андрейчук", "Александр", null, null), MALE),
            Arguments.of(new Names("Андрейчук", "Александра", null, null), FEMALE)
        );
    }

}

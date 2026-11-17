package com.github.aleksandy.petrovich;

import static com.github.aleksandy.petrovich.rules.Gender.ANDROGYNOUS;
import static com.github.aleksandy.petrovich.rules.Gender.FEMALE;
import static com.github.aleksandy.petrovich.rules.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.Rules;
import com.github.aleksandy.petrovich.rules.yaml.YamlRules;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Порт тестов из petrovich-ruby {@code test/petrovich_gender_test.rb}.
 */
class GenderDetectorTest {

    private static final Rules RULES = new YamlRules();
    private static final GenderDetector DETECTOR = new GenderDetector(RULES.gender());

    @ParameterizedTest(name = "[{index}] {arguments}")
    @MethodSource("genderData")
    void shouldDetectGender(String lastName, String firstName, String middleName, Gender expected) {
        assertThat(DETECTOR.detect(new Names(lastName, firstName, middleName, null))).isEqualTo(expected);
    }

    static Stream<Arguments> genderData() {
        return Stream.of(
            Arguments.of(null, "Александр", null, MALE),
            Arguments.of("Склифосовский", null, null, MALE),
            Arguments.of(null, "Александра", null, FEMALE),
            Arguments.of("Склифосовская", null, null, FEMALE),
            Arguments.of("Склифосовская", "Александра", null, FEMALE),
            Arguments.of(null, "Саша", null, ANDROGYNOUS),
            Arguments.of("Андрейчук", "Саша", null, ANDROGYNOUS),
            Arguments.of("Иванов", "Саша", null, MALE),
            Arguments.of("Андрейчук", "Саша", "Олегович", MALE),
            Arguments.of(null, "Саша", "Олегович", MALE),
            Arguments.of("Осипчук", null, null, ANDROGYNOUS),
            Arguments.of(null, null, "Олегович", MALE),
            Arguments.of(null, null, "Олеговна", FEMALE)
        );
    }

}

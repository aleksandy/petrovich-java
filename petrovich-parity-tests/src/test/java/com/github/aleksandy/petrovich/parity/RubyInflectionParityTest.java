package com.github.aleksandy.petrovich.parity;

import static com.github.aleksandy.petrovich.Case.DATIVE;
import static com.github.aleksandy.petrovich.Case.GENITIVE;
import static com.github.aleksandy.petrovich.Case.INSTRUMENTAL;
import static com.github.aleksandy.petrovich.rules.Gender.ANDROGYNOUS;
import static com.github.aleksandy.petrovich.rules.Gender.FEMALE;
import static com.github.aleksandy.petrovich.rules.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Names;
import com.github.aleksandy.petrovich.Petrovich;
import com.github.aleksandy.petrovich.rules.yaml.YamlRules;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Кейсы склонения, портированные из petrovich-ruby {@code test/petrovich_test.rb}.
 *
 * <p>В ruby каждый кейс вызывает per-case метод ({@code .dative},
 * {@code .genitive} и т.д.). В java per-case методов сознательно нет, поэтому
 * всё выражено через {@link Petrovich#inflectTo(Names, Case)}.
 */
class RubyInflectionParityTest {

    private final Petrovich petrovich = new Petrovich(new YamlRules());

    @ParameterizedTest(name = "[{index}] {0} → {2}")
    @MethodSource("inflections")
    void shouldInflectLikeRuby(Names names, Case inflectionCase, Names expected) {
        assertThat(this.petrovich.inflectTo(names, inflectionCase)).isEqualTo(expected);
    }

    static Stream<Arguments> inflections() {
        return Stream.of(
            // it 'have inflect method' / it 'inflects firstname 1'
            Arguments.of(
                new Names(null, "Саша", null, MALE),
                DATIVE,
                new Names(null, "Саше", null, MALE)
            ),
            // it 'inflects lastname 1'
            Arguments.of(
                new Names("Кочубей", null, null, MALE),
                DATIVE,
                new Names("Кочубею", null, null, MALE)
            ),
            // it 'inflects lastname 2'
            Arguments.of(
                new Names("Козлов", null, null, MALE),
                DATIVE,
                new Names("Козлову", null, null, MALE)
            ),
            // it 'inflects lastname 3'
            Arguments.of(
                new Names("Салтыков-Щедрин", null, null, MALE),
                DATIVE,
                new Names("Салтыкову-Щедрину", null, null, MALE)
            ),
            // it 'inflects lastname 4'
            Arguments.of(
                new Names("Дюма", null, null, MALE),
                DATIVE,
                new Names("Дюма", null, null, MALE)
            ),
            // it 'inflects lastname 5'
            Arguments.of(
                new Names("Воробей", null, null, MALE),
                DATIVE,
                new Names("Воробью", null, null, MALE)
            ),
            // it 'inflects lastname 6'
            Arguments.of(
                new Names("Воробей", null, null, FEMALE),
                DATIVE,
                new Names("Воробей", null, null, FEMALE)
            ),
            // it 'inflects firstname 2'
            Arguments.of(
                new Names(null, "Анна-Мария", null, FEMALE),
                DATIVE,
                new Names(null, "Анне-Марии", null, FEMALE)
            ),
            // it 'inflects middlename 1'
            Arguments.of(
                new Names(null, null, "Борух-Бендитовна", FEMALE),
                DATIVE,
                new Names(null, null, "Борух-Бендитовне", FEMALE)
            ),
            // it 'inflects middlename 2'
            Arguments.of(
                new Names(null, null, "Георгиевна-Авраамовна", FEMALE),
                DATIVE,
                new Names(null, null, "Георгиевне-Авраамовне", FEMALE)
            ),
            // it 'inflects lastname 2' (Плевако)
            Arguments.of(
                new Names("Плевако", "Иван", null, MALE),
                DATIVE,
                new Names("Плевако", "Ивану", null, MALE)
            ),
            // it 'inflects lastname [ая]'
            Arguments.of(
                new Names("Слуцкая", null, null, null),
                DATIVE,
                new Names("Слуцкой", null, null, FEMALE)
            ),
            // it 'inflects male lastname' (Штеттер)
            Arguments.of(
                new Names("Штеттер", null, null, MALE),
                DATIVE,
                new Names("Штеттеру", null, null, MALE)
            ),
            // it 'inflects male lastname' (Коломиец)
            Arguments.of(
                new Names("Коломиец", null, null, null),
                GENITIVE,
                new Names("Коломийца", null, null, ANDROGYNOUS)
            ),
            // it 'inflects male lastname' (Быхун)
            Arguments.of(
                new Names("Быхун", null, null, null),
                INSTRUMENTAL,
                new Names("Быхуном", null, null, ANDROGYNOUS)
            )
        );
    }

}

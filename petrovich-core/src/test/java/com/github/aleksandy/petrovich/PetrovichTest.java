package com.github.aleksandy.petrovich;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.yaml.YamlRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.github.aleksandy.petrovich.Case.DATIVE;
import static com.github.aleksandy.petrovich.Case.GENITIVE;
import static com.github.aleksandy.petrovich.Case.INSTRUMENTAL;
import static com.github.aleksandy.petrovich.rules.Gender.ANDROGYNOUS;
import static com.github.aleksandy.petrovich.rules.Gender.FEMALE;
import static com.github.aleksandy.petrovich.rules.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Сквозные тесты на реальных YAML-правилах с автоматическим определением пола.
 * Часть портирована из petrovich-ruby {@code test/petrovich_test.rb}.
 */
class PetrovichTest {

    final Petrovich petrovich = new Petrovich(new YamlRules());

    @ParameterizedTest(name = "{0} {1} {2}")
    @CsvSource(
        textBlock = """
        Иванов | Иван | Иванович | - | GENITIVE | Иванова | Ивана | Ивановича | MALE
        Мамин-Сибиряк | Дмитрий | Наркисович | MALE | ACCUSATIVE | Мамина-Сибиряка | Дмитрия | Наркисовича | MALE
        Склодовская-Кюри | Мария | - | FEMALE | ACCUSATIVE | Склодовскую-Кюри | Марию | - | FEMALE
        Череззаборногузадерищенко | Акакий | Панасьевич | MALE | PREPOSITIONAL | Череззаборногузадерищенко | Акакии | Панасьевиче | MALE
        Иванова | Ольга | Ивановна | FEMALE | GENITIVE | Ивановой | Ольги | Ивановны | FEMALE
        """,
        delimiter = '|',
        nullValues = "-"
    )
    void shouldInflectNames(
        String lastName,
        String firstName,
        String middleName,
        String gender,
        Case inflectionCase,
        String expectedLastName,
        String expectedFirstName,
        String expectedMiddleName,
        String expectedGender
    ) {
        Names names = new Names(
            lastName,
            firstName,
            middleName,
            gender == null ? null : Gender.valueOf(gender)
        );

        assertThat(this.petrovich.inflectTo(names, inflectionCase))
            .isEqualTo(new Names(
                expectedLastName,
                expectedFirstName,
                expectedMiddleName,
                Gender.valueOf(expectedGender)
            ));
    }

    @Test void genderShouldBeDetectedWhenNotSpecified() {
        assertThat(this.petrovich.gender(new Names(null, "Саша", null, null)))
            .isEqualTo(ANDROGYNOUS);
        assertThat(this.petrovich.gender(new Names("Иванов", "Саша", null, null)))
            .isEqualTo(MALE);
        assertThat(this.petrovich.gender(new Names("Склодовская", null, null, null)))
            .isEqualTo(FEMALE);
        assertThat(this.petrovich.gender(new Names("Склодовская-Кюри", null, null, null)))
            .isEqualTo(FEMALE);
        assertThat(this.petrovich.gender(new Names("Дудикоф-Зайцева", null, null, null)))
            .isEqualTo(FEMALE);
        assertThat(this.petrovich.gender(new Names("Немирович-Данченко", null, null, null)))
            .isEqualTo(ANDROGYNOUS);
    }

    @Test void explicitGenderShouldWinOverDetection() {
        assertThat(this.petrovich.gender(new Names("Иванов", null, null, FEMALE)))
            .isEqualTo(FEMALE);
    }

    @Test void shouldInflectFemaleLastNameWithDetectedGender() {
        assertThat(this.petrovich.inflectTo(new Names("Слуцкая", null, null, null), DATIVE))
            .isEqualTo(new Names("Слуцкой", null, null, FEMALE));
    }

    @Test void shouldInflectMaleLastNameWithDetectedGender() {
        assertThat(this.petrovich.inflectTo(new Names("Коломиец", null, null, null), GENITIVE))
            .isEqualTo(new Names("Коломийца", null, null, ANDROGYNOUS));
    }

    @Test void shouldInflectMaleLastNameInInstrumental() {
        assertThat(this.petrovich.inflectTo(new Names("Быхун", null, null, null), INSTRUMENTAL))
            .isEqualTo(new Names("Быхуном", null, null, ANDROGYNOUS));
    }

}
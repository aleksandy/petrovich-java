package com.github.aleksandy.petrovich;

import static com.github.aleksandy.petrovich.Case.*;
import static com.github.aleksandy.petrovich.rules.Gender.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.function.BiFunction;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.GenderRules;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CaseInflectionTest {

    private static final Rules EMPTY = new Rules() {

        private static final RuleSet EMPTY = new RuleSet(null, null);

        @Override public RuleSet lastName() {
            return EMPTY;
        }

        @Override public RuleSet firstName() {
            return EMPTY;
        }

        @Override public RuleSet middleName() {
            return EMPTY;
        }

        @Override public GenderRules gender() {
            return new GenderRules() {};
        }

    };

    @CsvSource({
        "MALE, male, male#g, male#d, male#a, male#i, male#p",
        "FEMALE, female, female#gen, femal#dat, fema#acc, fem#inst, fe#prop",
        "ANDROGYNOUS, andr, andr, andr, andr, andr, andr"
    })
    @ParameterizedTest(name = "[{index}] {0}")
    void inflectFirstNameShouldBeSuccess(
        Gender gender,
        String nominative,
        String genitive,
        String dative,
        String accusative,
        String instrumental,
        String prepositional
    ) {
        CaseInflection inflection = new CaseInflection(rulesFor(gender), gender);

        assertSoftly(softly -> {
            softly.assertThat(inflection.inflectFirstNameTo(nominative, GENITIVE))
                .as("GENITIVE inflection of %s", nominative)
                .isEqualTo(genitive);
            softly.assertThat(inflection.inflectFirstNameTo(nominative, DATIVE))
                .as("DATIVE inflection of %s", nominative)
                .isEqualTo(dative);
            softly.assertThat(inflection.inflectFirstNameTo(nominative, ACCUSATIVE))
                .as("ACCUSATIVE inflection of %s", nominative)
                .isEqualTo(accusative);
            softly.assertThat(inflection.inflectFirstNameTo(nominative, INSTRUMENTAL))
                .as("INSTRUMENTAL inflection of %s", nominative)
                .isEqualTo(instrumental);
            softly.assertThat(inflection.inflectFirstNameTo(nominative, PREPOSITIONAL))
                .as("PREPOSITIONAL inflection of %s", nominative)
                .isEqualTo(prepositional);
        });
    }

    @NullSource
    @EmptySource
    @ValueSource(strings = { "   ", "\t\n     \r\t" })
    @ParameterizedTest(name = "[{index}] {arguments}")
    void inflectToMethodsShouldThrowExceptionWhenNameIsBlank(String name) {
        assertSoftly(softly -> {
            var inflection = new CaseInflection(EMPTY, null);

            softly.assertThatThrownBy(() -> inflection.inflectLastNameTo(name, null))
                .isInstanceOf(PetrovichException.class)
                .hasMessage("Name must not be blank.");
            softly.assertThatThrownBy(() -> inflection.inflectFirstNameTo(name, null))
                .isInstanceOf(PetrovichException.class)
                .hasMessage("Name must not be blank.");
            softly.assertThatThrownBy(() -> inflection.inflectMiddleNameTo(name, null))
                .isInstanceOf(PetrovichException.class)
                .hasMessage("Name must not be blank.");
        });
    }

    @Test void inflectToMethodsShouldReturnNameUnchangedWhenInflectionRuleNotFound() {
        CaseInflection inflection = new CaseInflection(EMPTY, null);
        List<BiFunction<String, Case, String>> inflects = List.of(
            inflection::inflectLastNameTo, inflection::inflectFirstNameTo, inflection::inflectMiddleNameTo
        );

        assertThat(inflects).allSatisfy(inflect -> {
            String anyName = "any-name";
            assertThat(inflect.apply(anyName, GENITIVE)).isEqualTo(anyName);
        });
    }

    @Test void firstMatchingRuleShouldWinRegardlessOfSuffixLength() {
        // Порядок правил имеет значение (как в ruby): побеждает первое подходящее,
        // а не самое длинное совпадение.
        RuleSet lastNameRules = new RuleSet(
            List.of(
                new Rule(
                    FEMALE,
                    List.of("а"),
                    List.of("-и", "-и", "-ю", "-ей", "-е")
                ),
                new Rule(
                    FEMALE,
                    List.of("ова"),
                    List.of("--ой", "--ой", "--ую", "--ой", "--ой")
                )
            )
        );

        Rules orderedRules = new Rules() {
            @Override public RuleSet lastName() {
                return lastNameRules;
            }

            @Override public RuleSet firstName() {
                throw new AssertionError("should not be called");
            }

            @Override public RuleSet middleName() {
                throw new AssertionError("should not be called");
            }

            @Override public GenderRules gender() {
                throw new AssertionError("should not be called");
            }
        };

        CaseInflection inflection = new CaseInflection(orderedRules, FEMALE);
        assertThat(inflection.inflectLastNameTo("Иванова", GENITIVE)).isEqualTo("Иванови");
    }

    private static Rules rulesFor(Gender gender) {
        Rule rule = switch (gender) {
            case MALE -> new Rule(
                MALE,
                List.of("male"),
                List.of("#g", "#d", "#a", "#i", "#p")
            );
            case FEMALE -> new Rule(
                FEMALE,
                List.of("female"),
                List.of("#gen", "-#dat", "--#acc", "---#inst", "----#prop")
            );
            case ANDROGYNOUS -> new Rule(
                ANDROGYNOUS,
                List.of("andr"),
                List.of(".", ".", ".", ".", ".")
            );
        };

        return new FirstNameRules(new RuleSet(List.of(rule)));
    }

    private record FirstNameRules(RuleSet firstName) implements Rules {

        @Override public RuleSet lastName() {
            return RuleSet.EMPTY;
        }

        @Override public RuleSet middleName() {
            return RuleSet.EMPTY;
        }

        @Override public GenderRules gender() {
            return new GenderRules() {};
        }

    }
}


package com.github.aleksandy.petrovich;

import static com.github.aleksandy.petrovich.Case.*;
import static com.github.aleksandy.petrovich.CaseInflection.*;
import static com.github.aleksandy.petrovich.Gender.*;
import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

import com.github.aleksandy.petrovich.rules.data.Rules;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class CaseInflectionTest {

    private static final int COUNT_CASES = Case.values().length;

    private final Rules rules = new YamlRulesLoader().getRules();

    @EnumSource(value = Gender.class, names = { "MALE", "FEMALE" })
    @ParameterizedTest(name = "[{index}] {arguments}")
    void maleNames(Gender gender) throws Exception {
        CaseInflection inflection = new CaseInflection(this.rules, gender);
        String resourceName = String.format("/%s.names.expected", gender.name().toLowerCase(Locale.ROOT));
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(getClass().getResourceAsStream(resourceName), UTF_8)
        )) {
            String name;
            int pos = 1;
            while ((name = in.readLine()) != null) {
                String[] expected = name.split("\\|");
                assertThat(expected)
                    .as(name)
                    .hasSize(COUNT_CASES);

                String firstName = expected[0];
                testName(expected[1], firstName, GENITIVE, inflection, pos);
                testName(expected[2], firstName, DATIVE, inflection, pos);
                testName(expected[3], firstName, ACCUSATIVE, inflection, pos);
                testName(expected[4], firstName, INSTRUMENTAL, inflection, pos);
                testName(expected[5], firstName, PREPOSITIONAL, inflection, pos);

                pos++;
            }
        }
    }

    private void testName(String expected, String nominative, Case $case, CaseInflection inflection, int pos) {
        assertThat(inflection.inflectFirstNameTo(nominative, $case))
            .as("%04d, %s<%s>", pos, nominative, $case.name())
            .isEqualTo(expected);
    }

    @Test void inflectToMethodsShouldThrowNullPointerExceptionWhenNameIsNull() {
        var inflection = new CaseInflection(this.rules, MALE);
        List<BiConsumer<String, Case>> inflects = List.of(
            inflection::inflectLastNameTo, inflection::inflectFirstNameTo, inflection::inflectMiddleNameTo
        );

        assertThat(inflects).allSatisfy(inflect ->
            assertThatThrownBy(() -> inflect.accept(null, GENITIVE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name must not be null.")
        );
    }

    @CsvSource({"-1,1", "1-,1", "-1-,1", "1-2-3,1-2-3", "-,''", "--,''"})
    @ParameterizedTest(name = "[{index}] {arguments}")
    void normalizeNameShouldStripLeadingAndTrailingDashes(String input, String expected) {
        assertThat(normalizeName(input)).isEqualTo(expected);
    }

    @ValueSource(strings = { "", "1-2-3", "-----"})
    @ParameterizedTest(name = "[{index}] {arguments}")
    void inflectToMethodsShouldThrowExceptionWhenInflectionRuleNotFound(String name) {
        CaseInflection inflection = new CaseInflection(this.rules, MALE);
        List<BiConsumer<String, Case>> inflects = List.of(
            inflection::inflectLastNameTo, inflection::inflectFirstNameTo, inflection::inflectMiddleNameTo
        );

        assertThat(inflects).allSatisfy(inflect ->
            assertThatThrownBy(() -> inflect.accept(name, GENITIVE))
                .isInstanceOf(PetrovichException.class)
                .hasMessageMatching("Inflection rule '([^']*)' has not been found.")
        );
    }

}


package com.github.aleksandy.petrovich.inflection;

import static com.github.aleksandy.petrovich.Case.*;
import static com.github.aleksandy.petrovich.Gender.*;
import static com.github.aleksandy.petrovich.inflection.CaseInflection.*;
import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.*;
import java.util.Locale;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CaseInflectionTest {

    private static final int COUNT_CASES = Case.values().length;

    private final RulesProvider provider = new RulesProvider(new YamlRulesLoader());

    @Test void maleNames() throws Exception {
        testNames(MALE);
    }

    @Test void femaleNames() throws Exception {
        testNames(FEMALE);
    }

    void testNames(Gender gender) throws Exception {
        CaseInflection inflection = new CaseInflection(this.provider, gender);
        String resourceName = String.format(
            "/%s.names.expected",
            gender.name().toLowerCase(Locale.ROOT)
        );
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    getClass().getResourceAsStream(resourceName),
                    UTF_8
                )
            );
        ) {
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

    @Test void nullName() {
        assertThatThrownBy(() -> new CaseInflection(this.provider, MALE).inflectFirstNameTo(null, GENITIVE))
            .isInstanceOf(NullPointerException.class);
        ;
    }

    @CsvSource({"-1,1", "1-,1", "-1-,1", "1-2-3,1-2-3", "-,''", "--,''"})
    @ParameterizedTest(name = "[{index}] {arguments}")
    void testNormalizeName(String input, String expected) {
        assertThat(normalizeName(input)).isEqualTo(expected);
    }

    @ValueSource(strings = { "", "1-2-3", "-----"})
    @ParameterizedTest(name = "[{index}] {arguments}")
    void trash(String name) {
        CaseInflection inflection = new CaseInflection(this.provider, MALE);
        assertThat(inflection.inflectFirstNameTo(name, GENITIVE)).isEqualTo(name);
    }

}


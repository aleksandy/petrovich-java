package com.github.aleksandy.petrovich.inflection;

import static com.github.aleksandy.petrovich.Case.*;
import static com.github.aleksandy.petrovich.Gender.*;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;

public class CaseInflectionTest {

    private final RulesProvider provider = (
        new RulesProvider(
            new YamlRulesLoader()
        )
    );

    @Test
    public void maleNames() throws Exception {
        testNames(MALE);
    }

    @Test
    public void femaleNames() throws Exception {
        testNames(FEMALE);
    }

    void testNames(Gender gender) throws Exception {
        CaseInflection inflection = new CaseInflection(this.provider, gender);
        String resourceName = String.format(
            "/%s.names.expected",
            gender.name()
        );
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    getClass().getResourceAsStream(resourceName),
                    UTF_8
                )
            );
        ) {
            String name = null;
            int pos = 1;
            while ((name = in.readLine()) != null) {
                String[] expected = name.split("\\|");
                assertEquals(name, 6, expected.length);
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
        String message = String.format("%04d, %s<%s>", pos, nominative, $case.name());
        assertEquals(message, expected, inflection.inflectFirstNameTo(nominative, $case));
    }
}

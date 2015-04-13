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
import com.github.aleksandy.petrovich.test.DataLoader;

public class CaseInflectionTest {

    private final RulesProvider provider = (
        new RulesProvider(
            new YamlRulesLoader()
        )
    );

    @Test
    public void femaleNames() throws Exception {
        testNames(female);
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
                    DataLoader.class.getResourceAsStream(resourceName),
                    UTF_8
                )
            );
        ) {
            String name = null;
            while ((name = in.readLine()) != null) {
                String[] expected = name.split("\\|");
                assertEquals(name, 6, expected.length);
                String firstName = expected[0];
                testName(expected[1], firstName, GENITIVE, inflection);
                testName(expected[2], firstName, DATIVE, inflection);
                testName(expected[3], firstName, ACCUSATIVE, inflection);
                testName(expected[4], firstName, INSTRUMENTAL, inflection);
                testName(expected[5], firstName, PREPOSITIONAL, inflection);
            }
        }
    }

    private void testName(String expected, String nominative, Case $case, CaseInflection inflection) {
        String message = String.format("%s<%s>", nominative, $case.name());
        assertEquals(message, expected, inflection.inflectFirstNameTo(nominative, $case));
    }
}

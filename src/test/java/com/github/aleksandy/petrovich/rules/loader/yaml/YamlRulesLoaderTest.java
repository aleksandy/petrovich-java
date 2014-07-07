package com.github.aleksandy.petrovich.rules.loader.yaml;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.aleksandy.petrovich.rules.data.RuleSet;
import com.github.aleksandy.petrovich.rules.data.Rules;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;

public class YamlRulesLoaderTest {

    @Test
    public void testLoad() {
        Rules rules = new YamlRulesLoader().load();
        assertNotNull(rules);

        {
            RuleSet ruleSet = rules.getLastName();
            assertNotNull(ruleSet);

            assertNotNull(ruleSet.getExceptions());
            assertEquals(5, ruleSet.getExceptions().size());

            assertNotNull(ruleSet.getSuffixes());
            assertEquals(39, ruleSet.getSuffixes().size());
        }

        {
            RuleSet ruleSet = rules.getMiddleName();
            assertNotNull(ruleSet);

            assertNotNull(ruleSet.getExceptions());
            assertEquals(0, ruleSet.getExceptions().size());

            assertNotNull(ruleSet.getSuffixes());
            assertEquals(2, ruleSet.getSuffixes().size());
        }

        {
            RuleSet ruleSet = rules.getFirstName();
            assertNotNull(ruleSet);

            assertNotNull(ruleSet.getExceptions());
            assertEquals(6, ruleSet.getExceptions().size());

            assertNotNull(ruleSet.getSuffixes());
            assertEquals(17, ruleSet.getSuffixes().size());
        }

    }

}

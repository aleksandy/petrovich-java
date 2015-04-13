package com.github.aleksandy.petrovich.rules.loader.yaml;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.aleksandy.petrovich.rules.data.RuleSet;
import com.github.aleksandy.petrovich.rules.data.Rules;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;

public class YamlRulesLoaderTest {

    @Test
    public void testLoad() {
        final Rules rules = new YamlRulesLoader().load();
        assertNotNull(rules);

        {
            final RuleSet ruleSet = rules.getLastName();
            assertNotNull(ruleSet);
            assertNotNull(ruleSet.getExceptions());
            assertNotNull(ruleSet.getSuffixes());
        }

        {
            final RuleSet ruleSet = rules.getMiddleName();
            assertNotNull(ruleSet);
            assertNotNull(ruleSet.getExceptions());
            assertNotNull(ruleSet.getSuffixes());
        }

        {
            final RuleSet ruleSet = rules.getFirstName();
            assertNotNull(ruleSet);
            assertNotNull(ruleSet.getExceptions());
            assertNotNull(ruleSet.getSuffixes());
        }

    }

}

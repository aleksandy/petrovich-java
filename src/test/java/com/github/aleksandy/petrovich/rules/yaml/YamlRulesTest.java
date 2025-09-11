package com.github.aleksandy.petrovich.rules.yaml;

import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YamlRulesTest {

    @Test void testLoad() {
        assertThat(new YamlRules())
            .isNotNull()
            .extracting(Rules::firstName, Rules::lastName, Rules::middleName)
            .asInstanceOf(InstanceOfAssertFactories.list(RuleSet.class))
            .allSatisfy(ruleSet ->
                assertThat(ruleSet)
                    .isNotNull()
                    .extracting(RuleSet::exceptions, RuleSet::suffixes)
                    .doesNotContainNull()
            );
    }

}

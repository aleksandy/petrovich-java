package com.github.aleksandy.petrovich.rules.loader.yaml;

import com.github.aleksandy.petrovich.rules.data.RuleSet;
import com.github.aleksandy.petrovich.rules.data.Rules;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YamlRulesLoaderTest {

    @Test void testLoad() {
        assertThat(new YamlRulesLoader().load())
            .isNotNull()
            .extracting(Rules::getFirstName, Rules::getLastName, Rules::getMiddleName)
            .asInstanceOf(InstanceOfAssertFactories.list(RuleSet.class))
            .allSatisfy(ruleSet ->
                assertThat(ruleSet)
                    .isNotNull()
                    .extracting(RuleSet::getExceptions, RuleSet::getSuffixes)
                    .doesNotContainNull()
            );
    }

}

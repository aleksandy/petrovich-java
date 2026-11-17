package com.github.aleksandy.petrovich.rules.yaml;

import static com.github.aleksandy.petrovich.rules.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.aleksandy.petrovich.rules.GenderRuleSet;
import com.github.aleksandy.petrovich.rules.GenderRules;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class YamlRulesTest {

    @Test void testLoad() {
        YamlRules rules = new YamlRules();
        assertThat(rules)
            .extracting(Rules::firstName, Rules::lastName, Rules::middleName)
            .asInstanceOf(InstanceOfAssertFactories.list(RuleSet.class))
            .allSatisfy(ruleSet ->
                assertThat(ruleSet)
                    .isNotNull()
                    .extracting(RuleSet::exceptions, RuleSet::suffixes)
                    .doesNotContainNull()
            );
        assertThat(rules.gender())
            .isNotNull()
            .extracting(GenderRules::firstName, GenderRules::lastName, GenderRules::middleName)
            .asInstanceOf(InstanceOfAssertFactories.list(GenderRuleSet.class))
            .allSatisfy(ruleSet ->
                assertThat(ruleSet)
                    .isNotNull()
                    .extracting(GenderRuleSet::exceptions, GenderRuleSet::suffixes)
                    .doesNotContainNull()
            );
    }

    @Test void shouldFailWhenRulesResourceIsMissing() {
        assertThatThrownBy(() -> new YamlRules("missing-rules.yml", "missing-gender.yml"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Rules resource 'missing-rules.yml' has not been found on the classpath.");
    }

    @Test void shouldFailWhenGenderResourceIsMissing() {
        assertThatThrownBy(() -> new YamlRules("rules.yml", "missing-gender.yml"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Gender rules resource 'missing-gender.yml' has not been found on the classpath.");
    }

    @Test void shouldRejectNullResourcePath() {
        assertThatThrownBy(() -> new YamlRules(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Rules resource path must not be null.");
    }

    @Test void shouldSupportLeadingSlashInRulesPath() {
        YamlRules rules = new YamlRules("/rules.yml");

        assertThat(rules.firstName()).isNotNull();
    }

    @Test void shouldSupportLeadingSlashInGenderPath() {
        YamlRules rules = new YamlRules("rules.yml", "/gender.yml");

        assertThat(rules.gender()).isNotNull();
    }

    @Test void shouldValidateResourcesEagerlyButParseLazily() {
        // Ресурс существует, поэтому конструктор проходит; некорректный YAML
        // разбирается только при первом обращении к правилам.
        YamlRules rules = new YamlRules("invalid-rule-test.yml");

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class);
    }

    @Test void shouldFailOnInvalidGenderValue() {
        YamlRules rules = new YamlRules("rules.yml", "invalid-gender.yml");

        assertThatThrownBy(rules::gender)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class)
            .cause()
            .hasMessageContaining("Invalid value 'unknown' for enum Gender");
    }

    @Test void shouldFailOnRuleWithoutTestSuffixes() {
        YamlRules rules = new YamlRules("invalid-rule-test.yml");

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class)
            .cause()
            .hasMessageContaining("Rule test suffixes must not be empty")
            .hasMessageContaining(MALE.toString());
    }

    @Test void shouldFailOnRuleWithWrongModSuffixesCount() {
        YamlRules rules = new YamlRules("invalid-rule-mods.yml");

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class)
            .cause()
            .hasMessageContaining("Rule mod suffixes must contain exactly 5 elements, but got 2")
            .hasMessageContaining("[н]");
    }

    @Test void shouldLoadResourcesExplicitly() {
        YamlRules rules = new YamlRules();

        rules.load();

        assertThat(rules.firstName()).isNotNull();
        assertThat(rules.gender()).isNotNull();
    }

    @Test void shouldFailOnExplicitLoadOfInvalidRules() {
        YamlRules rules = new YamlRules("invalid-rule-test.yml");

        assertThatThrownBy(rules::load)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class);
    }

}

package com.github.aleksandy.petrovich.rules.yaml;

import static com.github.aleksandy.petrovich.rules.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.aleksandy.petrovich.rules.GenderRuleSet;
import com.github.aleksandy.petrovich.rules.GenderRules;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;
import com.github.aleksandy.petrovich.rules.yaml.jackson2.Jackson2YamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.jackson3.Jackson3YamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.snakeyaml.SnakeYamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.snakeyamlengine.SnakeYamlEngineLoader;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DatabindException;

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
        assertThatThrownBy(() -> new YamlRules((String) null))
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
        // Ресурс существует, поэтому конструктор проходит; некорректные правила
        // разбираются и валидируются только при первом обращении к ним.
        YamlRules rules = jackson2Rules("invalid-rule-test.yml", "gender.yml");

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class)
            .cause()
            .hasMessageContaining("Rule test suffixes must not be empty");
    }

    @Test void shouldFailOnInvalidGenderValue() {
        YamlRules rules = jackson2Rules("rules.yml", "invalid-gender.yml");

        assertThatThrownBy(rules::gender)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class)
            .cause()
            .hasMessageContaining("Invalid value 'unknown' for enum Gender");
    }

    @Test void shouldFailOnRuleWithoutTestSuffixes() {
        YamlRules rules = jackson2Rules("invalid-rule-test.yml", "gender.yml");

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class)
            .cause()
            .hasMessageContaining("Rule test suffixes must not be empty")
            .hasMessageContaining(MALE.toString());
    }

    @Test void shouldFailOnRuleWithWrongModSuffixesCount() {
        YamlRules rules = jackson2Rules("invalid-rule-mods.yml", "gender.yml");

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
        YamlRules rules = jackson2Rules("invalid-rule-test.yml", "gender.yml");

        assertThatThrownBy(rules::load)
            .isInstanceOf(UncheckedIOException.class)
            .hasCauseInstanceOf(JsonMappingException.class);
    }

    @Test void shouldUseJackson3ByDefaultWhenAllLibrariesAvailable() {
        // В тестовом classpath доступны все библиотеки, поэтому
        // по приоритету выбирается Jackson 3.
        YamlRules rules = new YamlRules("invalid-rule-test.yml");

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(DatabindException.class)
            .hasMessageContaining("Rule test suffixes must not be empty");
    }

    @Test void shouldLoadWithDefaultLoader() {
        YamlRules rules = new YamlRules();

        assertThat(rules.firstName()).isNotNull();
        assertThat(rules.gender()).isNotNull();
    }

    @Test void shouldPreferJackson3() {
        assertThat(YamlRules.createLoader(rulesUrl(), genderUrl(), name -> true))
            .isInstanceOf(Jackson3YamlLoader.class);
    }

    @Test void shouldPreferSnakeYamlEngineOverOtherLibraries() {
        Set<String> available = Set.of(
            YamlRules.JACKSON2_YAML,
            YamlRules.SNAKEYAML,
            YamlRules.SNAKEYAML_ENGINE
        );

        assertThat(YamlRules.createLoader(rulesUrl(), genderUrl(), available::contains))
            .isInstanceOf(SnakeYamlEngineLoader.class);
    }

    @Test void shouldPreferJackson2WhenJackson3AndSnakeYamlEngineAreUnavailable() {
        Set<String> available = Set.of(YamlRules.JACKSON2_YAML, YamlRules.SNAKEYAML);

        assertThat(YamlRules.createLoader(rulesUrl(), genderUrl(), available::contains))
            .isInstanceOf(Jackson2YamlLoader.class);
    }

    @Test void shouldFallBackToSnakeYaml() {
        assertThat(YamlRules.createLoader(rulesUrl(), genderUrl(), YamlRules.SNAKEYAML::equals))
            .isInstanceOf(SnakeYamlLoader.class);
    }

    @Test void shouldFallBackToSnakeYamlEngine() {
        assertThat(
            YamlRules.createLoader(rulesUrl(), genderUrl(), YamlRules.SNAKEYAML_ENGINE::equals)
        ).isInstanceOf(SnakeYamlEngineLoader.class);
    }

    @Test void shouldFailWhenNoYamlLibraryIsAvailable() {
        assertThatThrownBy(() -> YamlRules.createLoader(rulesUrl(), genderUrl(), name -> false))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No supported YAML library has been found on the classpath");
    }

    @Test void classNamesShouldBeValid() {
        assertThat(YamlRules.JACKSON3_YAML)
            .isEqualTo(tools.jackson.dataformat.yaml.YAMLMapper.class.getName());
        assertThat(YamlRules.JACKSON2_YAML)
            .isEqualTo(com.fasterxml.jackson.dataformat.yaml.YAMLFactory.class.getName());
        assertThat(YamlRules.SNAKEYAML)
            .isEqualTo(org.yaml.snakeyaml.Yaml.class.getName());
        assertThat(YamlRules.SNAKEYAML_ENGINE)
            .isEqualTo(org.snakeyaml.engine.v2.api.Load.class.getName());
    }

    private static YamlRules jackson2Rules(String rules, String gender) {
        return new YamlRules(new Jackson2YamlLoader(resource(rules), resource(gender)));
    }

    private static URL rulesUrl() {
        return resource("rules.yml");
    }

    private static URL genderUrl() {
        return resource("gender.yml");
    }

    private static URL resource(String path) {
        return YamlRulesTest.class.getClassLoader().getResource(path);
    }

}

/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich.rules.yaml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URL;

import com.github.aleksandy.petrovich.rules.yaml.jackson2.Jackson2YamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.jackson3.Jackson3YamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.snakeyaml.SnakeYamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.snakeyamlengine.SnakeYamlEngineLoader;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DatabindException;

class YamlLoaderParityTest {

    private static final URL RULES = resource("rules.yml");
    private static final URL GENDER = resource("gender.yml");

    private static final YamlRules JACKSON2 = new YamlRules(new Jackson2YamlLoader(RULES, GENDER));

    @Test void jackson3ShouldReadSameRulesAsJackson2() {
        YamlRules actual = new YamlRules(new Jackson3YamlLoader(RULES, GENDER));

        assertSameRules(JACKSON2, actual);
    }

    @Test void snakeYamlShouldReadSameRulesAsJackson2() {
        YamlRules actual = new YamlRules(new SnakeYamlLoader(RULES, GENDER));

        assertSameRules(JACKSON2, actual);
    }

    @Test void snakeYamlEngineShouldReadSameRulesAsJackson2() {
        YamlRules actual = new YamlRules(new SnakeYamlEngineLoader(RULES, GENDER));

        assertSameRules(JACKSON2, actual);
    }

    @Test void jackson3ShouldRejectInvalidRules() {
        YamlRules rules = new YamlRules(
            new Jackson3YamlLoader(resource("invalid-rule-test.yml"), GENDER)
        );

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(DatabindException.class)
            .hasMessageContaining("Rule test suffixes must not be empty");
    }

    @Test void snakeYamlShouldRejectInvalidRules() {
        YamlRules rules = new YamlRules(
            new SnakeYamlLoader(resource("invalid-rule-test.yml"), GENDER)
        );

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Rule test suffixes must not be empty");
    }

    @Test void snakeYamlEngineShouldRejectInvalidRules() {
        YamlRules rules = new YamlRules(
            new SnakeYamlEngineLoader(resource("invalid-rule-test.yml"), GENDER)
        );

        assertThatThrownBy(rules::firstName)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Rule test suffixes must not be empty");
    }

    @Test void jackson3ShouldRejectInvalidGender() {
        YamlRules rules = new YamlRules(
            new Jackson3YamlLoader(RULES, resource("invalid-gender.yml"))
        );

        assertThatThrownBy(rules::gender)
            .isInstanceOf(DatabindException.class)
            .hasMessageContaining("Invalid value 'unknown' for enum Gender");
    }

    @Test void snakeYamlShouldRejectInvalidGender() {
        YamlRules rules = new YamlRules(
            new SnakeYamlLoader(RULES, resource("invalid-gender.yml"))
        );

        assertThatThrownBy(rules::gender)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid value 'unknown' for enum Gender");
    }

    @Test void snakeYamlEngineShouldRejectInvalidGender() {
        YamlRules rules = new YamlRules(
            new SnakeYamlEngineLoader(RULES, resource("invalid-gender.yml"))
        );

        assertThatThrownBy(rules::gender)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid value 'unknown' for enum Gender");
    }

    private static void assertSameRules(YamlRules expected, YamlRules actual) {
        assertThat(actual.lastName()).isEqualTo(expected.lastName());
        assertThat(actual.firstName()).isEqualTo(expected.firstName());
        assertThat(actual.middleName()).isEqualTo(expected.middleName());

        assertThat(actual.gender().lastName()).isEqualTo(expected.gender().lastName());
        assertThat(actual.gender().firstName()).isEqualTo(expected.gender().firstName());
        assertThat(actual.gender().middleName()).isEqualTo(expected.gender().middleName());
    }

    private static URL resource(String path) {
        return YamlLoaderParityTest.class.getClassLoader().getResource(path);
    }

}

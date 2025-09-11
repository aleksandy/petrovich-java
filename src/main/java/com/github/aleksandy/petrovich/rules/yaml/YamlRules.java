package com.github.aleksandy.petrovich.rules.yaml;

import static com.github.aleksandy.petrovich.Utils.lazy;
import static java.util.Objects.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

public class YamlRules implements Rules {

    private static String DEFAULT_RULES_RESOURCE = "/rules.yml";

    private final Supplier<YamlRulesModel> rules;

    public YamlRules() {
        this(DEFAULT_RULES_RESOURCE);
    }

    public YamlRules(String resourcePath) {
        requireNonNull(resourcePath);
        this.rules = lazy(() -> load(resourcePath));
    }

    @Override
    public RuleSet lastName() {
        return this.rules.get().lastName();
    }

    @Override
    public RuleSet firstName() {
        return this.rules.get().firstName();
    }

    @Override
    public RuleSet middleName() {
        return this.rules.get().middleName();
    }

    private YamlRulesModel load(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
            .addMixIn(Rule.class, RuleMixIn.class);
        try (InputStream rulesStream = YamlRules.class.getResourceAsStream(resourcePath)) {
            return mapper.readValue(rulesStream, YamlRulesModel.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record YamlRulesModel(
        @JsonProperty("lastname") RuleSet lastName,
        @JsonProperty("firstname") RuleSet firstName,
        @JsonProperty("middlename") RuleSet middleName
    ) {}

    private static abstract class RuleMixIn {
        @JsonProperty("test") List<String> testSuffixes;
        @JsonProperty("mods") List<String> modSuffixes;
    }

}

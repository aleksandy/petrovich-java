package com.github.aleksandy.petrovich.rules.yaml;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

public class YamlRules implements Rules {

    private static final String DEFAULT_RULES_RESOURCE = "/rules.yml";

    private final Supplier<YamlRulesModel> rules;

    public YamlRules() {
        this(DEFAULT_RULES_RESOURCE);
    }

    public YamlRules(String resourcePath) {
        requireNonNull(resourcePath);
        this.rules = new Lazy<>(() -> load(resourcePath));
    }

    @Override
    public RuleSet lastName() {
        return this.rules.get().lastName;
    }

    @Override
    public RuleSet firstName() {
        return this.rules.get().firstName;
    }

    @Override
    public RuleSet middleName() {
        return this.rules.get().middleName;
    }

    private YamlRulesModel load(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
            .addMixIn(Rule.class, RuleMixIn.class)
            .registerModule(
                new SimpleModule("petrovich-rules-yaml")
                    .addDeserializer(Gender.class, new GenderDeserializer())
            );

        try (InputStream rulesStream = YamlRules.class.getResourceAsStream(resourcePath)) {
            return mapper.readValue(rulesStream, YamlRulesModel.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final class Lazy<T> implements Supplier<T> {

        private static final Object SENTINEL = new Object();
        private volatile T value = (T) SENTINEL;
        private Supplier<? extends T> delegate;

        private Lazy(Supplier<? extends T> delegate) {
            this.delegate = delegate;
        }

        @Override public T get() {
            if (this.value == SENTINEL) {
                synchronized (this) {
                    if (this.value == SENTINEL) {
                        this.value = this.delegate.get();
                        this.delegate = null;
                    }
                }
            }

            return this.value;
        }

    }

    private static final class YamlRulesModel {
        @JsonProperty("lastname") RuleSet lastName;
        @JsonProperty("firstname") RuleSet firstName;
        @JsonProperty("middlename") RuleSet middleName;
    }

    private static abstract class RuleMixIn {
        @JsonProperty("test") List<String> testSuffixes;
        @JsonProperty("mods") List<String> modSuffixes;
    }

    private static final class GenderDeserializer extends StdDeserializer<Gender> {

        private static final Map<String, Gender> VALUES = Stream.of(Gender.values())
            .collect(toUnmodifiableMap(v -> v.name().toLowerCase(Locale.ROOT), Function.identity()));

        private GenderDeserializer() {
            super(Gender.class);
        }

        @Override
        public Gender deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            var value = jsonParser.getText();
            var result = VALUES.get(value);
            if (result != null) {
                return result;
            }

            return context.reportInputMismatch(
                handledType(),
                "Invalid value '%s' for enum %s. Valid values: %s.",
                value,
                handledType().getSimpleName(),
                VALUES.keySet().toString()
            );
        }

    }

}

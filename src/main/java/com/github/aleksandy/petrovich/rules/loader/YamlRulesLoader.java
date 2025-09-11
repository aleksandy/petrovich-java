package com.github.aleksandy.petrovich.rules.loader;

import static com.github.aleksandy.petrovich.Utils.lazy;
import static java.util.Objects.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.data.Rules;

public class YamlRulesLoader implements RulesProvider {

    private static String DEFAULT_RULES_RESOURCE = "/rules.yml";

    private final Supplier<Rules> rules;

    public YamlRulesLoader() {
        this(DEFAULT_RULES_RESOURCE);
    }

    public YamlRulesLoader(String resourcePath) {
        requireNonNull(resourcePath);
        this.rules = lazy(() -> load(resourcePath));
    }

    @Override
    public Rules getRules() {
        return this.rules.get();
    }

    private Rules load(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream rulesStream = YamlRulesLoader.class.getResourceAsStream(resourcePath)) {
            return mapper.readValue(rulesStream, Rules.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

package com.github.aleksandy.petrovich.rules.loader;

import static java.util.Objects.*;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.aleksandy.petrovich.rules.data.Rules;

public class YamlRulesLoader implements RulesLoader {

    private final String resourcePath;

    public YamlRulesLoader() {
        this(DEFAULT_RULES_RESOURCE);
    }

    public YamlRulesLoader(String resourcePath) {
        this.resourcePath = requireNonNull(resourcePath);
    }

    @Override
    public Rules load() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (
            InputStream rulesStream = YamlRulesLoader.class.getResourceAsStream(resourcePath);
        ) {
            return mapper.readValue(rulesStream, Rules.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}

package com.github.aleksandy.petrovich.rules.yaml.jackson2;

import static java.util.stream.Collectors.toUnmodifiableMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.yaml.YamlLoader;

/**
 * Загрузчик моделей правил на Jackson 2.
 *
 * <p>Содержит всё, что нужно для чтения YAML именно этой библиотекой:
 * маппер, настройку имён свойств и десериализаторы {@link Gender}.
 */
public final class Jackson2YamlLoader extends YamlLoader {

    private static final String MODULE_NAME = "petrovich-rules-yaml";

    private static final Map<String, Gender> GENDER_VALUES = Stream.of(Gender.values())
        .collect(toUnmodifiableMap(v -> v.name().toLowerCase(Locale.ROOT), Function.identity()));

    private final ObjectMapper mapper;

    /**
     * Создаёт загрузчик для указанных ресурсов.
     *
     * @param rules ресурс с правилами склонения
     * @param gender ресурс с правилами определения пола
     */
    public Jackson2YamlLoader(URL rules, URL gender) {
        super(rules, gender);

        this.mapper = JsonMapper.builder(new YAMLFactory())
            .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE)
            .addMixIn(Rule.class, RuleMixIn.class)
            .addModule(
                new SimpleModule(MODULE_NAME)
                    .addDeserializer(Gender.class, new GenderDeserializer())
                    .addKeyDeserializer(Gender.class, new GenderKeyDeserializer())
            )
            .build();
    }

    @Override
    protected <T> T load(URL resource, Class<T> type) {
        try (InputStream stream = resource.openStream()) {
            return this.mapper.readValue(stream, type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Переименовывает компоненты {@link Rule} под ключи YAML-формата,
     * не добавляя зависимость от Jackson в API правил.
     */
    private abstract static class RuleMixIn {

        @JsonProperty("test")
        abstract List<String> testSuffixes();

        @JsonProperty("mods")
        abstract List<String> modSuffixes();

    }

    private static Gender deserializeGenderValue(
        String value,
        DeserializationContext context
    ) throws JsonMappingException {
        var result = GENDER_VALUES.get(value);
        if (result != null) {
            return result;
        }

        return context.reportInputMismatch(
            Gender.class,
            "Invalid value '%s' for enum %s. Valid values: %s.",
            value,
            Gender.class.getSimpleName(),
            GENDER_VALUES.keySet().toString()
        );
    }

    private static final class GenderDeserializer extends StdDeserializer<Gender> {

        private GenderDeserializer() {
            super(Gender.class);
        }

        @Override public Gender deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return deserializeGenderValue(jsonParser.getText(), context);
        }

    }

    private static final class GenderKeyDeserializer extends KeyDeserializer {

        @Override public Object deserializeKey(String key, DeserializationContext context) throws JsonMappingException {
            return deserializeGenderValue(key, context);
        }

    }

}

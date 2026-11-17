package com.github.aleksandy.petrovich.rules.yaml;

import static java.util.stream.Collectors.toUnmodifiableMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.GenderRule;
import com.github.aleksandy.petrovich.rules.GenderRuleSet;
import com.github.aleksandy.petrovich.rules.GenderRules;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

/**
 * Правила склонения и определения пола, загружаемые из YAML-ресурсов classpath.
 *
 * <p>По умолчанию используются ресурсы {@code rules.yml} (склонение)
 * и {@code gender.yml} (определение пола). Пути задаются относительно корня
 * classpath; ведущий {@code /} допускается. Существование ресурсов проверяется
 * сразу в конструкторе, а содержимое читается лениво — при первом обращении
 * к соответствующей группе правил.
 *
 * <p>Класс неизменяем и потокобезопасен: загруженные правила кэшируются.
 */
public class YamlRules implements Rules {

    private static final String DEFAULT_RULES_RESOURCE = "/rules.yml";
    private static final String DEFAULT_GENDER_RULES_RESOURCE = "/gender.yml";

    private static final String LAST_NAME = "lastname";
    private static final String FIRST_NAME = "firstname";
    private static final String MIDDLE_NAME = "middlename";

    private final Supplier<YamlRulesModel> rules;
    private final Supplier<GenderRules> genderRules;

    /**
     * Создаёт экземпляр с ресурсами по умолчанию: {@code rules.yml}
     * и {@code gender.yml}.
     */
    public YamlRules() {
        this(DEFAULT_RULES_RESOURCE, DEFAULT_GENDER_RULES_RESOURCE);
    }

    /**
     * Создаёт экземпляр с собственным ресурсом правил склонения
     * и ресурсом определения пола по умолчанию ({@code gender.yml}).
     *
     * @param resourcePath путь к YAML-ресурсу правил склонения относительно
     *                     корня classpath; ведущий {@code /} допускается
     * @throws NullPointerException если {@code resourcePath} равен {@code null}
     * @throws IllegalStateException если ресурс не найден в classpath
     */
    public YamlRules(String resourcePath) {
        this(resourcePath, DEFAULT_GENDER_RULES_RESOURCE);
    }

    /**
     * Создаёт экземпляр с собственными ресурсами правил склонения
     * и определения пола.
     *
     * @param resourcePath путь к YAML-ресурсу правил склонения относительно
     *                     корня classpath; ведущий {@code /} допускается
     * @param genderResourcePath путь к YAML-ресурсу правил определения пола
     *                           относительно корня classpath; ведущий {@code /}
     *                           допускается
     * @throws NullPointerException если любой из путей равен {@code null}
     * @throws IllegalStateException если любой из ресурсов не найден в classpath
     */
    public YamlRules(String resourcePath, String genderResourcePath) {
        URL rulesResource = resource(resourcePath, "Rules resource");
        URL genderResource = resource(genderResourcePath, "Gender rules resource");

        JsonMapper mapper = JsonMapper.builder(new YAMLFactory())
            .addMixIn(Rule.class, RuleMixIn.class)
            .addModule(
                new SimpleModule("petrovich-rules-yaml")
                    .addDeserializer(Gender.class, new GenderDeserializer())
                    .addKeyDeserializer(Gender.class, new GenderKeyDeserializer())
            )
            .build();

        this.rules = new Lazy<>(() -> load(rulesResource, mapper, YamlRulesModel.class));
        this.genderRules = new Lazy<>(() -> {
            var model = load(genderResource, mapper, GenderRulesModel.class);

            return new GenderRules() {
                @Override public GenderRuleSet lastName() {
                    return model.part(LAST_NAME);
                }

                @Override public GenderRuleSet firstName() {
                    return model.part(FIRST_NAME);
                }

                @Override public GenderRuleSet middleName() {
                    return model.part(MIDDLE_NAME);
                }
            };
        });
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

    @Override
    public GenderRules gender() {
        return this.genderRules.get();
    }

    /**
     * Явно загружает оба набора правил — склонения и определения пола.
     *
     * <p>Обычно ресурсы загружаются лениво при первом обращении к правилам.
     * Этот метод позволяет выполнить загрузку заранее, например при старте
     * приложения. Повторные вызовы не перечитывают ресурсы.
     *
     * @throws IllegalStateException если ресурс не найден в classpath
     * @throws UncheckedIOException если YAML-ресурс некорректен
     */
    public void load() {
        this.rules.get();
        this.genderRules.get();
    }

    private static URL resource(String path, String description) {
        if (path == null) {
            throw new NullPointerException(description + " path must not be null.");
        }

        // ClassLoader всегда ожидает путь от корня classpath, поэтому
        // ведущий '/' просто отбрасываем.
        URL resource = YamlRules.class.getClassLoader()
            .getResource(path.startsWith("/") ? path.substring(1) : path);
        if (resource == null) {
            throw new IllegalStateException(
                description + " '" + path + "' has not been found on the classpath."
            );
        }

        return resource;
    }

    private static <T> T load(URL resource, JsonMapper mapper, Class<T> type) {
        try (InputStream rulesStream = resource.openStream()) {
            return mapper.readValue(rulesStream, type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final class Lazy<T> implements Supplier<T> {

        private volatile Object value;
        private Supplier<? extends T> delegate;

        private Lazy(Supplier<? extends T> delegate) {
            this.delegate = delegate;
        }

        @Override public T get() {
            if (this.value == null) {
                synchronized (this) {
                    if (this.value == null) {
                        this.value = this.delegate.get();
                        this.delegate = null;
                    }
                }
            }

            return (T) this.value;
        }

    }

    private static final class YamlRulesModel {
        @JsonProperty("lastname") RuleSet lastName;
        @JsonProperty("firstname") RuleSet firstName;
        @JsonProperty("middlename") RuleSet middleName;
    }

    private static final class GenderRulesModel {
        @JsonProperty("gender") Map<String, GenderPartModel> parts;

        GenderRuleSet part(String name) {
            GenderPartModel model = this.parts == null ? null : this.parts.get(name);
            return model == null ? GenderRuleSet.EMPTY : model.toGenderRuleSet();
        }
    }

    private static final class GenderPartModel {
        @JsonProperty("exceptions") Map<Gender, List<String>> exceptions;
        @JsonProperty("suffixes") Map<Gender, List<String>> suffixes;

        GenderRuleSet toGenderRuleSet() {
            Map<String, GenderRule> exceptionRules = new HashMap<>();
            forEach(this.exceptions, (gender, words) ->
                words.forEach(word ->
                    exceptionRules.put(word.toLowerCase(Locale.ROOT), new GenderRule(gender, word))
                )
            );

            List<GenderRule> suffixRules = new ArrayList<>();
            forEach(this.suffixes, (gender, suffixes) ->
                suffixes.forEach(suffix -> suffixRules.add(new GenderRule(gender, suffix)))
            );

            return new GenderRuleSet(exceptionRules, suffixRules);
        }

        private static void forEach(
            Map<Gender, List<String>> source,
            BiConsumer<Gender, List<String>> action
        ) {
            if (source != null) {
                source.forEach(action);
            }
        }
    }

    private static abstract class RuleMixIn {
        @JsonProperty("test") List<String> testSuffixes;
        @JsonProperty("mods") List<String> modSuffixes;
    }

    private static final Map<String, Gender> VALUES = Stream.of(Gender.values())
        .collect(toUnmodifiableMap(v -> v.name().toLowerCase(Locale.ROOT), Function.identity()));

    private static Gender deserializeGenderValue(String value, DeserializationContext context) throws JsonMappingException {
        var result = VALUES.get(value);
        if (result != null) {
            return result;
        }

        return context.reportInputMismatch(
            Gender.class,
            "Invalid value '%s' for enum %s. Valid values: %s.",
            value,
            Gender.class.getSimpleName(),
            VALUES.keySet().toString()
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

        @Override public Object deserializeKey(String key, DeserializationContext context) throws IOException {
            return deserializeGenderValue(key, context);
        }

    }

}

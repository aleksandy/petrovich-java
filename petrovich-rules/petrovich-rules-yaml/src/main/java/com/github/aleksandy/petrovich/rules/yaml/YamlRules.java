package com.github.aleksandy.petrovich.rules.yaml;

import java.net.URL;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.aleksandy.petrovich.rules.GenderRules;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;
import com.github.aleksandy.petrovich.rules.yaml.jackson2.Jackson2YamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.jackson3.Jackson3YamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.snakeyaml.SnakeYamlLoader;
import com.github.aleksandy.petrovich.rules.yaml.snakeyamlengine.SnakeYamlEngineLoader;

/**
 * Правила склонения и определения пола, загружаемые из YAML-ресурсов classpath.
 *
 * <p>По умолчанию используются ресурсы {@code rules.yml} (склонение)
 * и {@code gender.yml} (определение пола). Пути задаются относительно корня
 * classpath; ведущий {@code /} допускается. Существование ресурсов проверяется
 * сразу в конструкторе, а содержимое читается лениво — при первом обращении
 * к соответствующей группе правил.
 *
 * <p>Библиотека разбора YAML выбирается автоматически по тому, что доступно
 * в classpath, в порядке приоритета: Jackson 3, SnakeYAML Engine, Jackson 2,
 * SnakeYAML. Явно выбрать реализацию можно через конструктор
 * {@link #YamlRules(YamlLoader)}.
 *
 * <p>Класс не зависит от библиотеки разбора YAML: чтение моделей выполняет
 * {@link YamlLoader}. Класс неизменяем и потокобезопасен:
 * загруженные правила кэшируются.
 */
public class YamlRules implements Rules {

    private static final String DEFAULT_RULES_RESOURCE = "/rules.yml";
    private static final String DEFAULT_GENDER_RULES_RESOURCE = "/gender.yml";

    static final String JACKSON3_YAML = "tools.jackson.dataformat.yaml.YAMLMapper";
    static final String JACKSON2_YAML = "com.fasterxml.jackson.dataformat.yaml.YAMLFactory";
    static final String SNAKEYAML = "org.yaml.snakeyaml.Yaml";
    static final String SNAKEYAML_ENGINE = "org.snakeyaml.engine.v2.api.Load";

    private final Supplier<RulesModel> rules;
    private final Supplier<GenderRules> genderRules;

    /**
     * Создаёт экземпляр с ресурсами по умолчанию: {@code rules.yml}
     * и {@code gender.yml}. Библиотека разбора YAML выбирается автоматически.
     */
    public YamlRules() {
        this(DEFAULT_RULES_RESOURCE, DEFAULT_GENDER_RULES_RESOURCE);
    }

    /**
     * Создаёт экземпляр с собственным ресурсом правил склонения
     * и ресурсом определения пола по умолчанию ({@code gender.yml}).
     * Библиотека разбора YAML выбирается автоматически.
     *
     * @param resourcePath путь к YAML-ресурсу правил склонения относительно
     *                     корня classpath; ведущий {@code /} допускается
     * @throws NullPointerException если {@code resourcePath} равен {@code null}
     * @throws IllegalStateException если ресурс не найден в classpath
     *                               или не найдена библиотека разбора YAML
     */
    public YamlRules(String resourcePath) {
        this(resourcePath, DEFAULT_GENDER_RULES_RESOURCE);
    }

    /**
     * Создаёт экземпляр с собственными ресурсами правил склонения
     * и определения пола. Библиотека разбора YAML выбирается автоматически.
     *
     * @param resourcePath путь к YAML-ресурсу правил склонения относительно
     *                     корня classpath; ведущий {@code /} допускается
     * @param genderResourcePath путь к YAML-ресурсу правил определения пола
     *                           относительно корня classpath; ведущий {@code /}
     *                           допускается
     * @throws NullPointerException если любой из путей равен {@code null}
     * @throws IllegalStateException если любой из ресурсов не найден в classpath
     *                               или не найдена библиотека разбора YAML
     */
    public YamlRules(String resourcePath, String genderResourcePath) {
        this(createLoader(
            resource(resourcePath, "Rules resource"),
            resource(genderResourcePath, "Gender rules resource")
        ));
    }

    /**
     * Создаёт экземпляр с указанным загрузчиком моделей правил.
     *
     * <p>Позволяет выбрать реализацию разбора YAML.
     *
     * @param loader загрузчик моделей правил
     * @throws NullPointerException если {@code loader} равен {@code null}
     */
    public YamlRules(YamlLoader loader) {
        this.rules = new Lazy<>(loader::loadRules);
        this.genderRules = new Lazy<>(loader::loadGender);
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
     * @throws RuntimeException если ресурс не удалось прочитать или разобрать;
     *                          конкретный тип зависит от реализации загрузчика
     */
    public void load() {
        this.rules.get();
        this.genderRules.get();
    }

    /**
     * Создаёт загрузчик по первой доступной библиотеке разбора YAML.
     *
     * <p>Порядок приоритета: Jackson 3, SnakeYAML Engine, Jackson 2, SnakeYAML.
     *
     * @param rules ресурс с правилами склонения
     * @param gender ресурс с правилами определения пола
     * @return загрузчик моделей правил
     * @throws IllegalStateException если ни одна из библиотек не найдена
     */
    private static YamlLoader createLoader(URL rules, URL gender) {
        return createLoader(rules, gender, YamlRules::isAvailable);
    }

    // Package-private перегрузка для проверки приоритета в тестах.
    static YamlLoader createLoader(URL rules, URL gender, Predicate<String> available) {
        if (available.test(JACKSON3_YAML)) {
            return new Jackson3YamlLoader(rules, gender);
        }
        if (available.test(SNAKEYAML_ENGINE)) {
            return new SnakeYamlEngineLoader(rules, gender);
        }

        if (available.test(JACKSON2_YAML)) {
            return new Jackson2YamlLoader(rules, gender);
        }
        if (available.test(SNAKEYAML)) {
            return new SnakeYamlLoader(rules, gender);
        }

        throw new IllegalStateException(
            "No supported YAML library has been found on the classpath. Add Jackson 3, Jackson 2, SnakeYAML or SnakeYAML Engine."
        );
    }

    private static boolean isAvailable(String className) {
        try {
            Class.forName(className, false, YamlRules.class.getClassLoader());

            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
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

    @SuppressWarnings("unchecked")
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

}
